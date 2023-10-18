package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.exceptions.ImageSizeException;
import nz.ac.canterbury.seng302.tab.exceptions.ImageTypeException;
import nz.ac.canterbury.seng302.tab.service.*;
import nz.ac.canterbury.seng302.tab.service.checkers.RegistrationChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

@Controller
public class ClubController {

    /** The logger */
    final Logger logger = LoggerFactory.getLogger(ClubController.class);

    /** The ModerationAPIController for moderation API logic */
    private final ModerationAPIController moderationAPIController;

    /** The TeamService for database logic */
    private final TeamService teamService;

    /** The UserService for database logic */
    private final UserService userService;

    /** The ClubService for database logic */
    private final ClubService clubService;

    /** The LocationService for database logic */
    private final LocationService locationService;

    /** The SportService for database logic */
    private final SportService sportService;

    /** The NumCommentsService to fetch the leaderboard */
    private final NumCommentsService numCommentsService;

    /** The FeedPostService for database logic */
    private final FeedPostService feedPostService;

    /** The CommentService for database logic */
    private final CommentService commentService;

    /** The NumPostsService for database logic */
    private final NumPostsService numPostsService;

    /** The TableController for common table methods */
    private final TableController tableController;

    /** Invalid address string constant */
    private static final String INVALID_ADDRESS = "invalidAddress";

    /** Invalid postcode string constant */
    private static final String INVALID_POSTCODE = "invalidPostcode";

    /** Invalid suburb string constant */
    private static final String INVALID_SUBURB = "invalidSuburb";

    /** Invalid city string constant */
    private static final String INVALID_CITY = "invalidCity";

    /** Invalid country string constant */
    private static final String INVALID_COUNTRY = "invalidCountry";

    /** Club form string constant */
    private static final String CLUB_FORM = "clubForm";

    /** Club id string constant */
    public static final String CLUB_ID = "clubId";

    /** Club name string constant */
    public static final String CLUB_NAME = "clubName";

    /** All clubs string constant */
    public static final String ALL_CLUBS = "allClubs";

    /** Error string constant */
    private static final String ERROR = "error";

    /** Team error string constant */
    private static final String TEAM_ERROR = "teamError";

    /** Redirect club post constant */
    private static final String REDIRECT_CLUB_POST = "redirect:/clubPost";

    /** Redirect home constant */
    private static final String REDIRECT_HOME = "redirect:/";

    /** Redirect club profile constant */
    private static final String REDIRECT_CLUB_PROFILE = "redirect:/clubProfile?clubId=";

    /** Page size constant */
    private static final int PAGE_SIZE = 10;


    /**
     * Constructor for the controller
     * @param clubService The club service
     * @param locationService The location service
     * @param teamService The team service
     * @param userService The user service
     * @param tableController The table controller
     * @param sportService The sport service
     * @param feedPostService The feed post service
     * @param moderationAPIController The moderation API controller
     * @param commentService The comment service
     * @param numCommentsService The num comments service
     * @param numPostsService The num posts service
     */
    @Autowired
    public ClubController(ClubService clubService,
                          LocationService locationService,
                          TeamService teamService,
                          UserService userService,
                          TableController tableController,
                          SportService sportService,
                          FeedPostService feedPostService,
                          CommentService commentService,
                          ModerationAPIController moderationAPIController,
                          NumCommentsService numCommentsService,
                          NumPostsService numPostsService) {
        this.clubService = clubService;
        this.locationService = locationService;
        this.teamService = teamService;
        this.userService = userService;
        this.tableController = tableController;
        this.sportService = sportService;
        this.feedPostService = feedPostService;
        this.commentService = commentService;
        this.moderationAPIController = moderationAPIController;
        this.numPostsService = numPostsService;
        this.numCommentsService = numCommentsService;
    }

    /**
     * Gets all the players in a team
     * @return a form with the teams,  default image and teamIds for checkboxes
     */
    @GetMapping("clubForm")
    public String getCreate(Model model) {
        model.addAttribute("dropdownTeams",addDropdownTeamsCreate());
        model.addAttribute("image", "images/default-img.png");
        model.addAttribute("teamIdCheckbox", -1);
        return CLUB_FORM;
    }

    /**
     * Gets the club information and checks that the
     * user is the manager of that club.
     * @param clubId the club id
     * @return club profile page
     */
    @GetMapping("clubProfile")
    public String getClubProfile(@RequestParam(name = CLUB_ID) long clubId,
                                 Model model) {
        Optional<Club> club = clubService.getClub(clubId);
        User currentUser = userService.getUserByEmail((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        model.addAttribute("isManager",false);
        if (club.isPresent()) {
            model.addAttribute(CLUB_NAME,club.get().getName());
            if (currentUser != null) {
                if (Objects.equals(club.get().getManager(), currentUser.getId())) {
                    model.addAttribute("isManager",true);
                    model.addAttribute("member",true);
                } else if (clubService.getAllUsersFromClub(clubId).contains(currentUser)) {
                    model.addAttribute("member",true);
                }
            }

            List<FeedPost> flaggedPosts = feedPostService.getFlaggedClubFeedPosts(clubId);
            List<Comment> flaggedComments = commentService.getFlaggedClubComments(clubId);
            model.addAttribute("feedPosts", buildCopyOfFeedPosts(clubId));
            model.addAttribute("flaggedFeedPosts", flaggedPosts);
            model.addAttribute("flaggedPostsPresent", !flaggedPosts.isEmpty());
            model.addAttribute("flaggedComments", flaggedComments);
            model.addAttribute("flaggedCommentsPresent", !flaggedComments.isEmpty());

            List<String> usersFlagged = new ArrayList<>();
            for (Comment comment : flaggedComments) {
                usersFlagged.add(comment.getAuthor().getFirstName() + " " + comment.getAuthor().getLastName());
            }
            model.addAttribute("usersFlagged", usersFlagged);


            model.addAttribute("canPost",clubService.managerCheck(clubId));
            model.addAttribute("id", clubId);
            model.addAttribute("sport",club.get().getSport());
            model.addAttribute("teams",club.get().getTeams());
            model.addAttribute("clubPicture",club.get().getProfilePicName());

            if (Boolean.parseBoolean((String) model.asMap().get("flag"))) {
                model.addAttribute("flaggedPost", true);
            }

            // Fetch the leaderboard for top commenters
            List<NumComments> topThreeCommenters = numCommentsService.getTopThreeByClub(clubId);

            // Fetch the leaderboard for top posters
            List<NumPosts> topThreePosters = numPostsService.getTopThreeByClub(clubId);

            model.addAttribute("topThreeCommenters", topThreeCommenters);
            model.addAttribute("topThreePosters", topThreePosters);

        } else {
            return REDIRECT_HOME;
        }
        return "clubProfile";
    }

    /**
     * Creates a deep copy of the feed posts for changing the style
     * @param clubId the id of the club
     * @return a deep copy of the feed posts
     */
    private List<FeedPost> buildCopyOfFeedPosts(long clubId) {
        List<FeedPost> feedPosts = feedPostService.getClubFeedPosts(clubId);
        List<FeedPost> copyOfFeedPost = new ArrayList<>();

        for (FeedPost original : feedPosts) {
            FeedPost copiedFeedPost = new FeedPost(original);
            copyOfFeedPost.add(copiedFeedPost);
        }

        List<NumComments> topThreeCommenters = numCommentsService.getTopThreeByClub(clubId);
        List<NumPosts> topThreePosters = numPostsService.getTopThreeByClub(clubId);

        for (NumComments commenter : topThreeCommenters) {
            for (FeedPost post : copyOfFeedPost) {
                if (commenter.getUser().getId().equals(post.getAuthor().getId())) {
                    post.setStyle(getBorderStyle(topThreeCommenters.indexOf(commenter)));
                }
            }
        }

        for (NumPosts poster : topThreePosters) {
            for (FeedPost post : copyOfFeedPost) {
                if (poster.getUser().getId().equals(post.getAuthor().getId())) {
                    post.setStyle(getBorderStyle(topThreePosters.indexOf(poster)));
                }
            }
        }

        return copyOfFeedPost;
    }

    /**
     * Gets the border style for the top three commenters and posters
     * @param index the position of the top three
     * @return the border style
     */
    private String getBorderStyle(int index) {
        return switch (index) {
            case 0 -> "border: 3px solid gold;";
            case 1 -> "border: 3px solid silver;";
            default -> "border: 3px solid saddlebrown;";
        };
    }


    /**
     * Removes a feed post from the database
     * @param postId the id of the post to be removed
     * @param clubId the id of the club that the post is in
     * @return the club profile page
     */
    @GetMapping("/removeFeedPost")
    public String removeFeedPost(@RequestParam(name = "postId") Long postId,
                                 @RequestParam(name = "id") Long clubId) {
        logger.info("POST /removeFeedPost");
        Optional<FeedPost> feedPost = feedPostService.getFeedPostById(postId);
        if (feedPost.isPresent()) {
            feedPostService.deleteFeedPost(postId);
        }
        return REDIRECT_CLUB_PROFILE + clubId;
    }

    /**
     * Adds and prefills the club form when wanting to
     * edit a club with the clubId
     * @param clubId the club id
     * @return club form page with prefilled details
     */
    @GetMapping("/clubEdit")
    public String editClub(@RequestParam(name = CLUB_ID) Long clubId,
                       Model model) {
        if (!clubService.managerCheck(clubId)) {
            return REDIRECT_HOME;
        }
        logger.info("GET /clubEdit");
        model.addAttribute(CLUB_ID, clubId);
        Optional<Club> club = clubService.getClub(clubId);
        if (club.isPresent()) {
            List<Team> selectedTeams = club.get().getTeams();
            model.addAttribute("dropdownTeams",addDropdownTeamsEdit(club.get()));
            model.addAttribute(CLUB_NAME, club.get().getName());
            model.addAttribute("selectedTeams", selectedTeams);
            model.addAttribute("address1", club.get().getLocation().getAddressOne());
            model.addAttribute("address2", club.get().getLocation().getAddressTwo());
            model.addAttribute("suburb", club.get().getLocation().getSuburb());
            model.addAttribute("postcode", club.get().getLocation().getPostcode());
            model.addAttribute("city", club.get().getLocation().getCity());
            model.addAttribute("country", club.get().getLocation().getCountry());
            model.addAttribute("image", club.get().getProfilePicName());
            return CLUB_FORM;
        }
        return CLUB_FORM;
    }


    /**
     * Runs when a post request is made from the clubForm
     * Creates a club based off of the clubForm information
     * @param clubId    the id of the club
     * @param name      the name of the club
     * @param address1  address line 1 of the club's location
     * @param address2  address line 1 of the club's location
     * @param city      city of the club's location
     * @param clubTeams the teams in the club
     * @param country   country of the club's location
     * @param file      club profile image
     * @param postcode  postcode of the club's location
     * @param suburb    suburb of the club's location
     * @return club profile page of the club
     */
    @PostMapping("clubForm")
    public String makeClub(@RequestParam(name = CLUB_NAME) String name,
                           @RequestParam(name = CLUB_ID, required = false) Long clubId,
                           @RequestParam(name = "clubTeam") List<Team> clubTeams,
                           @RequestParam(value = "file", defaultValue = "images/default-img.png") MultipartFile file,
                           @RequestParam(name = "address1", defaultValue = "") String address1,
                           @RequestParam(name = "address2", defaultValue = "") String address2,
                           @RequestParam(name = "suburb", defaultValue = "") String suburb,
                           @RequestParam(name = "postcode", defaultValue = "") String postcode,
                           @RequestParam(name = "city") String city,
                           @RequestParam(name = "country") String country,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        logger.info("POST /clubForm");

        // Error checking of the location
        boolean invalidLocation = false;
        if (!RegistrationChecker.isLocationValid(address1, address2, suburb, postcode, city, country)) {
            switch (RegistrationChecker.locationInvalidPartNum(address1, address2, suburb, postcode, city, country)) {
                case 1 -> model.addAttribute(INVALID_ADDRESS, "Please enter an address 1 containing at least one digit alongside any letters. Hyphens and apostrophes are also acceptable.");
                case 2 -> model.addAttribute(INVALID_ADDRESS, "Please enter an address 2 containing at least one digit alongside any letters. Hyphens and apostrophes are also acceptable.");
                case 3 -> {
                    model.addAttribute(INVALID_SUBURB, "Please enter a suburb which contains only letters.");
                    model.addAttribute(INVALID_ADDRESS, "");
                }
                case 4 -> {
                    model.addAttribute(INVALID_POSTCODE, "Please enter a postcode which only contains numbers, letters or hyphens.");
                    model.addAttribute(INVALID_ADDRESS, "");
                }
                case 5 -> {
                    model.addAttribute(INVALID_CITY, "Please enter a city containing only letters and hyphens.");
                    model.addAttribute(INVALID_ADDRESS, "");
                }
                case 6 -> {
                    model.addAttribute(INVALID_COUNTRY, "Please enter a country containing only letters and hyphens.");
                    model.addAttribute(INVALID_ADDRESS, "");
                }
                default ->
                        throw new IllegalStateException("Unexpected value: " + RegistrationChecker.locationInvalidPartNum(address1, address2, suburb, postcode, city, country));
            }
            invalidLocation = true;
        }

        // Error checking of blank fields
        if (address1.isBlank()) {
            model.addAttribute(INVALID_ADDRESS, "Address line 1 is required.");
            invalidLocation = true;
        }
        if (postcode.isBlank()) {
            model.addAttribute(INVALID_POSTCODE, "Postcode is required.");
            invalidLocation = true;
        }
        if (city.isBlank()) {
            model.addAttribute(INVALID_CITY, "City name is required.");
            invalidLocation = true;
        }
        if (country.isBlank()) {
            model.addAttribute(INVALID_COUNTRY, "Country name is required.");
            invalidLocation = true;
        }

        // Error checking if team is already in a club
        List<Team> selectedTeams = new ArrayList<>();
        try {
            for (Team teamId : clubTeams) {
                Team team = teamService.getTeam(teamId.getId()).orElseThrow();
                if (!clubService.checkIfTeamAlreadyInClubNoClubId(team.getId())) {
                    selectedTeams.add(team);
                } else if (clubId == null && clubService.checkIfTeamAlreadyInClubNoClubId(team.getId())) {
                    model.addAttribute(TEAM_ERROR, "This team is already part of a club: " + teamId.getName());
                    invalidLocation = true;
                }

            }
        } catch (NoSuchElementException e) {
            model.addAttribute(TEAM_ERROR, "Please select a team.");
        }


        if (invalidLocation) {
            addClubModelAttributes(model, name, clubId, clubTeams, address1, address2, suburb, postcode, city, country, selectedTeams);
            return CLUB_FORM;
        }
        User currentUser =  userService.getUserByEmail((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        // If the club is being edited not created
        if (clubId != null) {
            selectedTeams = new ArrayList<>();
            Club club;
            try {
                 club = clubService.getClub(clubId).orElseThrow();
            } catch (NoSuchElementException e) {
                return REDIRECT_HOME;
            }
            club.setName(name);
            club.getLocation().setAll(address1, address2, suburb, postcode, city, country);
            for (Team teamId : clubTeams) {
                if (clubService.checkIfTeamAlreadyInClub(teamId.getId(), clubId)) {
                    model.addAttribute(TEAM_ERROR, "This team is already part of a club: " + teamId.getName());
                    addClubModelAttributes(model, name, clubId, clubTeams, address1, address2, suburb, postcode, city, country, selectedTeams);
                    return CLUB_FORM;
                } else {
                    selectedTeams.add(teamId);
                }
            }
            club.setTeams(selectedTeams);
            clubService.save(club);
            if (file != null) {
                String error = uploadImage(club, file);
                if (error != null) {
                    redirectAttributes.addFlashAttribute(ERROR, error);
                }
            }
            return REDIRECT_CLUB_PROFILE + clubId;
        }

        // Adds the new club and the new location
        Location newLocation = new Location(address1, address2, suburb, postcode, city, country);
        locationService.addLocation(newLocation);
        Club club = new Club(currentUser,name,newLocation,selectedTeams);
        clubService.save(club);
        String error = uploadImage(club, file);
        if (error != null) {
            redirectAttributes.addFlashAttribute(ERROR, error);
        }
        return REDIRECT_CLUB_PROFILE + club.getId();
    }

    /**
     * Checks if a user is logged in
     * @return boolean whether the user is logged in
     */
    @ModelAttribute("loggedIn")
    boolean checkIsLoggedIn() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return !username.equals("anonymousUser") && !username.equals("anonymous");
    }

    /**
     * Gets a list of teams that the user manages and that are not part of any club
     * @return a list of teams that the user manages and that are not part of any club
     */
    protected List<Team> addDropdownTeamsCreate() {
        if (checkIsLoggedIn()) {
            User currentUser =  userService.getUserByEmail((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            return teamService.getFreeTeamsThatUserManagesOrCoachesFromUserId(currentUser.getId());
        }
        return List.of();
    }

    /**
     * Gets a list of teams that the user manages and that are not part of any club, as well as all the teams that is part of the club
     * @param club the club that is being edited.
     * @return a list of teams that the user manages and that are not part of any club, as well as all the teams that is part of the club
     */
    protected List<Team> addDropdownTeamsEdit(Club club) {
        if (checkIsLoggedIn()) {
            User currentUser =  userService.getUserByEmail((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            List<Team> freeTeams = teamService.getFreeTeamsThatUserManagesOrCoachesFromUserId(currentUser.getId());
            List<Team> clubTeams = club.getTeams();
            return Stream.concat(clubTeams.stream(), freeTeams.stream()).toList();
        }
        return List.of();
    }

    /**
     * Adds the given attributes to the model, so that they can be displayed when the page is redirected.
     * @param model       the model
     * @param name        the club's name
     * @param clubTeams   the club's initial team
     * @param address1    the first address line of the activity
     * @param address2    the second address line of the activity
     * @param postcode    the postcode of the location of the activity
     * @param suburb      the suburb of the location of the activity
     * @param city        the city of the location of the activity
     * @param country     the country of the location of the activity
     */
    public void addClubModelAttributes(Model model,
                                       String name,
                                       Long clubId,
                                       List<Team> clubTeams,
                                       String address1,
                                       String address2,
                                       String suburb,
                                       String postcode,
                                       String city,
                                       String country,
                                       List<Team> selectedTeams
    ) {
        model.addAttribute(CLUB_NAME, name);
        model.addAttribute(CLUB_ID, clubId);
        model.addAttribute("clubTeam", clubTeams);
        model.addAttribute("address1", address1);
        model.addAttribute("address2", address2);
        model.addAttribute("postcode", postcode);
        model.addAttribute("suburb", suburb);
        model.addAttribute("city", city);
        model.addAttribute("country", country);
        model.addAttribute("selectedTeams", selectedTeams);

    }

    /**
     * Adds the image uploaded from the clubForm
     * to the profile
     */
    public String uploadImage(Club club, MultipartFile file) {
        // Save the image file to disk
        try {
            String fileName = ImageHandler.uploadImage(file, "clubProfile-" + club.getId(), "profilePics");
            club.setProfilePicName(fileName);
            clubService.addProfilePic(club);
        } catch (ImageTypeException e) {
            return "Only JPG, PNG and SVG are allowed";
        } catch (ImageSizeException e) {
            return "Exceeded max file size of 10MB";
        } catch (IOException e) {
            return "Error uploading image";
        }
        return null;
    }

    /**
     * Gets the page showing the clubs the current logged-in user is in
     * @param page current page
     * @param model (map-like) representation of results to be used by thymeleaf
     * @return the myClubs page
     */
    @GetMapping("/myClubs")
    public String getMyClubs(@RequestParam(value="page", defaultValue = "1") Integer page,
                             Model model) {
        logger.info("GET /myTeams");

        // Check the clubs the current user is in
        User currentUser = userService.getUserByEmail((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        List<Club> clubs = clubService.getAllClubsFromUser(currentUser);

        // Set up pageable object
        Page<Club> clubsPage = tableController.convertToPage(page, PAGE_SIZE, clubs);

        // Set model attributes
        tableController.setModelAttributes(model, clubsPage, "myClubs", page);

        // Add empty table message if the table is empty
        if (clubs.isEmpty()) { model.addAttribute("noClubs", "You are not currently in a club"); }

        return "myClubs";
    }


    /**
     * Posts the form response of the search query for finding clubs
     * @param previousSearchUri the uri of the last search that was sent
     * @param ra    redirect attributes, to add attributes to the page that the user is redirected to
     * @param search  the search query
     * @return thymeleaf search, if an error occurs, or thymeleaf allClubs if a valid query
     */
    @PostMapping("/searchClubs")
    String getSearch(RedirectAttributes ra,
                     @RequestParam(name="previousSearchUri", required = false) String previousSearchUri,
                     @RequestParam(name="search") String search) {
        logger.info("POST /searchClubs");
        if (search.strip().length() < 3) {
            ra.addFlashAttribute("searchError", "Please enter at least 3 characters");
            ra.addFlashAttribute("loggedIn", checkIsLoggedIn());
            return "redirect:" + previousSearchUri;
        } else {
            ra.addAttribute("search",search);
            return "redirect:/allClubs";
        }
    }

    /**
     * Gets the thymeleaf page representing the /allClubs page - displays a list of clubs based on a search query,
     * selected sports and selected cities
     * @param page           the page of users to display
     * @param search         the search query
     * @param selectedSports the selected sports to filter by
     * @param selectedCities the selected cities to filter by
     * @param model          (map-like) representation of name, language and isJava boolean for use in thymeleaf
     * @return thymeleaf allClubs
     */
    @GetMapping("/allClubs")
    String viewClubs(@RequestParam(value="page", defaultValue = "1") Integer page,
                     @RequestParam(value="search", required = false) String search,
                     @RequestParam(value="selectedSports", required = false) List<Long> selectedSports,
                     @RequestParam(value="selectedCities", required = false) List<String> selectedCities,
                     HttpServletRequest httpServletRequest,
                     Model model) {


        Filter filter = tableController.initializeOptionalParameters(search, selectedSports, selectedCities);

        // Retrieve teams from the database depending on the filtering and search parameters
        List<Club> filteredClubs;
        List<Club> nonFilteredClubs;
        if (search == null || search.strip().length() < 3) {
            filteredClubs = clubService.getClubsFiltered(filter.getCities(), filter.getSports());
            nonFilteredClubs = clubService.getClubs();
        } else {
            filteredClubs = clubService.getClubsFilteredSearch(filter.getSearch(), filter.getCities(), filter.getSports());
            nonFilteredClubs = clubService.getByKeyword(search);
        }

        // Set up pageable object
        Page<Club> clubsPage = tableController.convertToPage(page, PAGE_SIZE, filteredClubs);

        // Populate the sports and cities list for the dropdown options
        List<Sport> allSports = sportService.findAll();
        List<String> allCities = clubService.getCitiesFromClubs(nonFilteredClubs);

        // Set model attributes
        tableController.setModelAttributes(model, clubsPage, ALL_CLUBS, page, allSports, allCities, filter);

        // Retrieve the URI and append the query string if it exists


        String requestUri = ALL_CLUBS;
        String queryString = httpServletRequest.getQueryString();
        if (queryString != null) {
            requestUri += "?" + queryString;
        }
        model.addAttribute("previousSearchUri", requestUri);

        // Add empty table message if the table is empty
        if (filteredClubs.isEmpty()) { model.addAttribute("noClubsDisplay", tableController.emptyEntriesMessage("clubs", search)); }

        return ALL_CLUBS;
    }


    /**
     * gets the page that is used to create club posts for a club.
     * @param id the id of the club being posted to.
     * @param model is used to enter information into the page.
     * */
    @GetMapping("/clubPost")
    public String getPostForm(@RequestParam(value="id") Long id, Model model) {
        model.addAttribute("submitPage","clubPost");
        model.addAttribute("title", "Add a Club Post");
        model.addAttribute("id", id);
        return "postForm";
    }

    /**
     * Creates a new post for a club
     * @param id                 the id of the club making the post
     * @param title              the title of the post
     * @param description        the post's description
     * @param file               the post's optional attachment
     * @param redirectAttributes the redirect attributes
     * @return redirect to home if invalid, to the user's profile if valid
     */
    @PostMapping("/clubPost")
    public String createPost(@RequestParam(value="id") Long id,
                           @RequestParam(value = "postTitle") String title,
                           @RequestParam(value = "postDescription") String description,
                           @RequestParam(value = "postAttachment", required = false) MultipartFile file,
                           RedirectAttributes redirectAttributes) {
        boolean isValid = true;

        if (!clubService.managerCheck(id)) {
            redirectAttributes.addFlashAttribute(ERROR, "You must be a manager of the club to add a post.");
            isValid = false;
        }
        isValid = isPostValid(title, description, redirectAttributes, isValid);
        if (!isValid) {
            addPostAttributes(redirectAttributes, id, title, description);
            return REDIRECT_CLUB_POST;
        }

        try {
            String fileName = null;
            if (!Objects.requireNonNull(file.getContentType()).substring(Objects.requireNonNull(file.getContentType()).lastIndexOf("/") + 1).equals("octet-stream")) {

                fileName = ImageHandler.uploadAttachment(file, ("attachment-" + LocalDateTime.now()).replace(":", ""), "postAttachments");
            }
            User currentUser =  userService.getUserByEmail((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            Club creatingClub = clubService.getClub(id).orElseThrow();
            FeedPost feedPost = new FeedPost(id, OwnerType.CLUB, creatingClub.getName(), title.trim(), description.trim(), currentUser, fileName);

            numPostsService.getByClubAndUser(id, currentUser.getId()).ifPresentOrElse(
                    numPosts -> {
                        numPosts.increment();
                        numPostsService.save(numPosts);
                    },
                    () -> {
                        NumPosts numPosts = new NumPosts(creatingClub, currentUser);
                        numPostsService.save(numPosts);
                    });

            feedPostService.save(moderationAPIController.moderateFeedPost(feedPost));
            if (feedPost.getFlagged()) {
                redirectAttributes.addFlashAttribute("flag", "true");
            }

        } catch (NullPointerException | IOException | NoSuchElementException e) {
            logger.error(e.toString());
            redirectAttributes.addFlashAttribute(ERROR,"Failed to Save.");
            addPostAttributes(redirectAttributes, id, title, description);
            return REDIRECT_CLUB_POST;
        } catch (ImageTypeException e) {
            redirectAttributes.addFlashAttribute(ERROR,"File type is invalid. Only JPG, PNG SVG, MP4, WEBM and OGG are allowed.");
            addPostAttributes(redirectAttributes, id, title, description);
            return REDIRECT_CLUB_POST;
        } catch (ImageSizeException e) {
            redirectAttributes.addFlashAttribute(ERROR,"Failed to Save. File is too large.");
            addPostAttributes(redirectAttributes, id, title, description);
            return REDIRECT_CLUB_POST;
        }
        redirectAttributes.addAttribute(CLUB_ID, id);
        return "redirect:/clubProfile";
    }

    static boolean isPostValid(@RequestParam("postTitle") String title, @RequestParam("postDescription") String description, RedirectAttributes redirectAttributes, boolean isValid) {
        if (title.isBlank()) {
            redirectAttributes.addFlashAttribute("postTitleError", "Post title is required.");
            isValid = false;
        }
        if (title.length() > 50) {
            redirectAttributes.addFlashAttribute("postTitleError", "Post title cannot be longer than 50 characters.");
            isValid = false;
        }
        if (description.length() > 200) {
            redirectAttributes.addFlashAttribute("postDescError", "Post description cannot be longer than 200 characters.");
            isValid = false;
        }
        return isValid;
    }


    /**
     * Adds the given attributes to the model, so that they can be displayed when the page is redirected.
     * @param ra          the redirect attributes
     * @param id          the id of the club
     * @param title       the post title
     * @param description the post description
     */
    public void addPostAttributes(RedirectAttributes ra,
                                       Long id,
                                       String title,
                                       String description) {
        ra.addAttribute("id", id);
        ra.addFlashAttribute("postTitle", title);
        ra.addFlashAttribute("postDescription", description);
    }


}

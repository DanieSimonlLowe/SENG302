package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.exceptions.ImageSizeException;
import nz.ac.canterbury.seng302.tab.exceptions.ImageTypeException;
import nz.ac.canterbury.seng302.tab.service.*;
import nz.ac.canterbury.seng302.tab.service.checkers.RegistrationChecker;
import nz.ac.canterbury.seng302.tab.service.checkers.TeamValidityChecker;
import nz.ac.canterbury.seng302.tab.service.sorters.TeamSorter;
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
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Controller for team form .
 */
@Controller
public class TeamFormController {

    /** The logger */
    final Logger logger = LoggerFactory.getLogger(TeamFormController.class);

    /** The TeamService for team database logic */
    private final TeamService teamService;

    /** The ActivityService for team database logic */
    private final ActivityService activityService;

    /** The LocationService for location database logic */
    private final LocationService locationService;

    /** The SportService for sport database logic */
    private final SportService sportService;

    /** The UserService for user database logic */
    private final UserService userService;

    /** The TableController for common table methods */
    private final TableController tableController;

    /** The clubService for getting the club the team is a part of */
    private final ClubService clubService;

    /** The ModerationAPIController for moderation API methods */
    private final ModerationAPIController moderationAPIController;

    /** The TeamMemberController for team member methods */
    private final TeamMemberController teamMemberController;

    /** The FeedPostService for database logic */
    private final FeedPostService feedPostService;

    /** The CommentService for database logic */
    private final CommentService commentService;

    /** Page size constant */
    private static final int PAGE_SIZE = 10;

    /** Model attribute string constant */
    private static final String IS_LOGGED_IN = "loggedIn";

    /** Model attribute string constant */
    private static final String IS_MANAGER = "isManager";

    /** Model attribute string constant */
    private static final String IS_MEMBER = "isMember";

    /** Invalid location string constant */
    private static final String INVALID_LOCATION = "invalidLocation";

    /** Invalid address1 string constant */
    private static final String INVALID_ADDRESS1 = "invalidAddress1";

    /** Invalid address2 string constant */
    private static final String INVALID_ADDRESS2 = "invalidAddress2";

    /** Invalid postcode string constant */
    private static final String INVALID_POSTCODE = "invalidPostcode";

    /** Invalid suburb string constant */
    private static final String INVALID_SUBURB = "invalidSuburb";

    /** Invalid city string constant */
    private static final String INVALID_CITY = "invalidCity";

    /** Invalid country string constant */
    private static final String INVALID_COUNTRY = "invalidCountry";

    /** Invalid sport name string constant */
    private static final String INVALID_SPORT = "invalidSport";

    /** Invalid team name string constant */
    private static final String INVALID_TEAM = "invalidTeam";

    /** Display ID string constant */
    private static final String DISPLAY_ID = "displayId";

    /** Team form string constant */
    private final String TEAM_FORM = "teamFormTemplate";

    /** All teams string constant */
    private static final String ALL_TEAMS = "allTeams";

    /** Error string constant */
    private static final String ERROR = "error";

    /** Url string constant */
    private static final String REDIRECT = "redirect:";

    /** Url string constant */
    private static final String REDIRECT_HOME = "redirect:/";

    /** Url string constant */
    private static final String REDIRECT_TEAM_POST = "redirect:/teamPost";

    /** Url string constant */
    private static final String REDIRECT_TEAM_PAGE = "redirect:/teamProfile?id=";

    /** Url string constant */
    private static final String REDIRECT_OWN_TEAM_PAGE = "redirect:/teamProfile";



    /**
     * Constructor for the TeamFormController class, which is the controller for teams logic
     * @param sportService    The sports service, for handling sports related data
     * @param teamService     The teams service, for handling teams related data
     * @param locationService The location service, for handling location related data
     * @param tableController The table controller, for handling common table methods
     * @param teamMemberController The team member controller, for handling common team member methods
     * @param clubService     The club service, for handling club related data
     * @param userService     The user service, for handling user related data
     * @param activityService The activity service, for handling activity related data
     * @param feedPostService The feed post service, for handling feed post related data
     * @param commentService The comment service, for handling comment related data
     * @param moderationAPIController The moderation API controller, for handling moderation API related data
     */
    @Autowired
    public TeamFormController(SportService sportService,
                              TeamService teamService,
                              LocationService locationService,
                              TableController tableController,
                              ClubService clubService,
                              TeamMemberController teamMemberController,
                              UserService userService,
                              ActivityService activityService,
                              FeedPostService feedPostService,
                              CommentService commentService,
                              ModerationAPIController moderationAPIController) {
        this.sportService = sportService;
        this.teamService = teamService;
        this.locationService = locationService;
        this.tableController = tableController;
        this.clubService = clubService;
        this.teamMemberController = teamMemberController;
        this.userService = userService;
        this.activityService = activityService;
        this.feedPostService = feedPostService;
        this.commentService = commentService;
        this.moderationAPIController = moderationAPIController;
    }

    /**
     * Adds the teams managed by the current user to the Thymeleaf model for use on the navbar
     * @return the list of teams managed by the current user
     */
    @ModelAttribute("dropdownTeams")
    public List<Team> addDropdownTeams() {
        if (checkIsLoggedIn()) {
            User currentUser =  userService.getUserByEmail((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            return teamService.getTeamsThatUserManagesFromUserId(currentUser.getId());
        }
        return List.of();
    }

    /**
     * Checks if a user is logged in
     * @return boolean whether the user is logged in
     */
    @ModelAttribute("loggedIn")
    private boolean checkIsLoggedIn() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        logger.info("Logged in: {}", !username.equals("anonymousUser") && !username.equals("anonymous"));
        return !username.equals("anonymousUser") && !username.equals("anonymous");
    }

    /**
     * Gets the form to be displayed (for adding and editing teams)
     * @return thymeleaf teamFormTemplate
     */
    @GetMapping("/form")
    public String form() {
        logger.info("GET /form");
        return TEAM_FORM;
    }

    /**
     * Gets the team form template with values pre-filled in to edit
     * @param displayName     name of the team
     * @param displaySport    sport the team plays
     * @param displayId       the id of the team
     * @param model           (map-like) representation of name, location and sport for use in thymeleaf,
     *                        with values being set to relevant parameters provided
     * @return thymeleaf teamFormTemplate
     */
    @GetMapping("/teamEdit")
    public String edit(@RequestParam(name = "displayName", defaultValue = "") String displayName,
                       @RequestParam(name = "displaySport", defaultValue = "") String displaySport,
                       @RequestParam(name = DISPLAY_ID) String displayId,
                       Model model) {
        logger.info("GET /teamEdit");
        Team currentTeam;
        try {
            currentTeam = teamService.getTeam(Integer.parseInt(displayId)).orElseThrow();
        } catch (Exception e) {
            logger.error(e.toString());
            return REDIRECT_HOME;
        }

        // Check that the logged-in user is a manager
        if(!teamMemberController.checkIsManager(Long.valueOf(displayId))) { return REDIRECT_HOME; }

        model.addAttribute("displayName", displayName);
        model.addAttribute("address1", currentTeam.getAddressOne());
        model.addAttribute("address2", currentTeam.getAddressTwo());
        model.addAttribute("suburb", currentTeam.getSuburb());
        model.addAttribute("postcode", currentTeam.getPostcode());
        model.addAttribute("city", currentTeam.getCity());
        model.addAttribute("country", currentTeam.getCountry());
        model.addAttribute("displaySport", displaySport);
        model.addAttribute(DISPLAY_ID, displayId);
        model.addAttribute("id", Integer.valueOf(displayId));
        model.addAttribute("location", currentTeam.getLocationEditString());
        return TEAM_FORM;
    }

    /**
     * Posts a team form response with name, location and sport
     * @param name     name of team
     * @param suburb   suburb of the team
     * @param sport    sport the team plays
     * @param model    (map-like) representation of name, location and sport for use in thymeleaf,
     *                 with values being set to relevant parameters provided
     * @param id       id of the team
     * @return thymeleaf teamFormTemplate
     */
    @PostMapping("/form")
    public String submitForm(@RequestParam(name = "name") String name,
                             @RequestParam(name = "address1", defaultValue = "") String address1,
                             @RequestParam(name = "address2", defaultValue = "") String address2,
                             @RequestParam(name="suburb", defaultValue = "") String suburb,
                             @RequestParam(name="postcode", defaultValue = "") String postcode,
                             @RequestParam(name="city") String city,
                             @RequestParam(name="country") String country,
                             @RequestParam(name = "sport") String sport,
                             @RequestParam(name = "id", required = false) Long id,
                             Model model, HttpServletResponse response) {
        logger.info("POST /form");
        model.addAttribute("nameError", null);
        model.addAttribute("sportError", null);
        model.addAttribute("locationError", null);

        // Check that the logged-in user is a manager
        if(id != null && !teamMemberController.checkIsManager(id)) {return REDIRECT_HOME; }

        boolean isValid = true;
        if (!TeamValidityChecker.isValidName(name)) {
            model.addAttribute(INVALID_TEAM, "Please enter a team name only made of alphanumeric characters, spaces, dots and curly brackets. The name cannot begin with a space");
            isValid = false;
        }
        if (!TeamValidityChecker.isValidSport(sport)) {
            model.addAttribute(INVALID_SPORT, "Please enter a team sport composed of only letters, spaces, apostrophes or dashes");
            isValid = false;
        }
        if (!RegistrationChecker.isLocationValid(address1, address2, suburb, postcode, city, country)) {
            switch (RegistrationChecker.locationInvalidPartNum(address1, address2, suburb, postcode, city, country)) {
                case 1:
                    model.addAttribute(INVALID_ADDRESS1, "Please enter an address containing at least one digit alongside any letters. Hyphens and apostrophes are also acceptable.");
                    break;
                case 2:
                    model.addAttribute(INVALID_ADDRESS2, "Please enter an address 2 which contains only letters or numbers. Hyphens and apostrophes are also acceptable.");
                    break;
                case 3:
                    model.addAttribute(INVALID_SUBURB, "Please enter a suburb which contains only letters.");
                    break;
                case 4:
                    model.addAttribute(INVALID_POSTCODE, "Please enter a postcode which only contains numbers, letters or hyphens.");
                    break;
                case 5:
                    model.addAttribute(INVALID_CITY, "Please enter a city containing only letters and hyphens.");
                    break;
                case 6:
                    model.addAttribute(INVALID_COUNTRY, "Please enter a country containing only letters and hyphens.");
                    break;
            }
            isValid = false;
        }
        if (!isValid) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute("displayName", name);
            model.addAttribute("address1", address1);
            model.addAttribute("address2", address2);
            model.addAttribute("suburb", suburb);
            model.addAttribute("postcode", postcode);
            model.addAttribute("city", city);
            model.addAttribute("country", country);
            model.addAttribute("displaySport", sport);
            if (id != null) {
                model.addAttribute(DISPLAY_ID, id);
            }
            return TEAM_FORM;

        } else if (id != null) {
            Team editTeam;
            try {
                editTeam = teamService.getTeam(id).orElseThrow();
            } catch (NoSuchElementException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                logger.error(e.toString());
                return REDIRECT_HOME;
            }
            editTeam.editingTeam(id, name, address1, address2, suburb, postcode, city, country, sport);
            teamService.updateTeam(editTeam);
            response.setStatus(HttpServletResponse.SC_OK);
            return REDIRECT_TEAM_PAGE + editTeam.getId();
        } else {
            Location newLocation = new Location(address1, address2, suburb, postcode, city, country);
            locationService.addLocation(newLocation);
            Team savedTeam = new Team(name, newLocation, sport);
            teamService.addTeam(savedTeam);
            model.addAttribute("displayName", name);
            model.addAttribute("address1", savedTeam.getAddressOne());
            model.addAttribute("address2", savedTeam.getAddressTwo());
            model.addAttribute("suburb", savedTeam.getSuburb());
            model.addAttribute("postcode", savedTeam.getPostcode());
            model.addAttribute("city", savedTeam.getCity());
            model.addAttribute("country", savedTeam.getCountry());
            model.addAttribute("displaySport", sport);
            response.setStatus(HttpServletResponse.SC_CREATED);
            return REDIRECT_TEAM_PAGE + savedTeam.getId();
        }

    }

    /**
     * Gets the thymeleaf page representing the /allTeams page - displays a list of teams based on a search query,
     * selected sports and selected cities
     * @param page           the page of users to display
     * @param search         the search query
     * @param selectedSports the selected sports to filter by
     * @param selectedCities the selected cities to filter by
     * @param model          (map-like) representation of name, language and isJava boolean for use in thymeleaf
     * @return thymeleaf allProfiles
     */
    @GetMapping("/allTeams")
    String viewTeams(@RequestParam(value="page", defaultValue = "1") Integer page,
                     @RequestParam(name = "previousSearchUri", required = false) String previousSearchUri,
                     @RequestParam(value="search", required = false) String search,
                     @RequestParam(value="selectedSports", required = false) List<Long> selectedSports,
                     @RequestParam(value="selectedCities", required = false) List<String> selectedCities,
                     HttpServletRequest httpServletRequest,
                     Model model) {
        logger.info("GET /allTeams");


        Filter filter = tableController.initializeOptionalParameters(search, selectedSports, selectedCities);

        // Retrieve teams from the database depending on the filtering and search parameters
        List<Team> filteredTeams;
        List<Team> nonFilteredTeams;
        if (search == null || search.strip().length() < 3) {
            filteredTeams = teamService.getTeamsFiltered(filter.getCities(), filter.getSports());
            nonFilteredTeams = teamService.getTeams();
        } else {
            filteredTeams = teamService.getTeamsFilteredSearch(filter.getSearch(), filter.getCities(), filter.getSports());
            nonFilteredTeams = teamService.getByKeyword(search);
        }

        // Sort teams if length greater than 1
        if (filteredTeams.size() > 1) { TeamSorter.sort(filteredTeams); }

        // For each team, get their corresponding club
        List<Club> filteredTeamClubs = new ArrayList<>(filteredTeams.size());

        for(int i = PAGE_SIZE * (page-1); i < PAGE_SIZE * page && i < filteredTeams.size(); i++) {
            Team filteredTeam = filteredTeams.get(i);
            Optional<Club> teamClubIfExists = clubService.getClubFromTeam(filteredTeam.getId());
            if(teamClubIfExists.isPresent()) {
                filteredTeamClubs.add(teamClubIfExists.get());
            } else {
                filteredTeamClubs.add(null);
            }
        }
        model.addAttribute("teamClubs", filteredTeamClubs);

        // Set up pageable object
        Page<Team> teamsPage = tableController.convertToPage(page, PAGE_SIZE, filteredTeams);

        // Populate the sports and cities list for the dropdown options
        List<Sport> allSports = sportService.findAll();
        List<String> allCities = teamService.getCitiesFromTeams(nonFilteredTeams);

        // Set model attributes
        tableController.setModelAttributes(model, teamsPage, ALL_TEAMS, page, allSports, allCities, filter);

        // Retrieve the URI and append the query string if it exists
        String requestUri = ALL_TEAMS;
        String queryString = httpServletRequest.getQueryString();
        if (queryString != null) {
            requestUri += "?" + queryString;
        }
        model.addAttribute("previousSearchUri", requestUri);

        // Add empty table message if the table is empty
        if (filteredTeams.isEmpty()) { model.addAttribute("noTeamsDisplay", tableController.emptyEntriesMessage("teams", search)); }

        return ALL_TEAMS;
    }

    /**
     * Posts the form response of the search query for finding teams
     * @param ra    redirect attributes, to add attributes to the page that the user is redirected to
     * @param search  the search query
     * @return thymeleaf search, if an error occurs, or thymeleaf allProfiles if a valid query
     */
    @PostMapping("/searchTeams")
    String getSearch(RedirectAttributes ra,
                     @RequestParam(name="previousSearchUri", required = false) String previousSearchUri,
                     @RequestParam(name="search") String search) {
        logger.info("POST /searchTeams");
        if (search.strip().length() < 3) {
            ra.addFlashAttribute("searchError", "Please enter at least 3 characters");
            ra.addFlashAttribute(IS_LOGGED_IN, checkIsLoggedIn());
            return REDIRECT + previousSearchUri;
        } else {
            return "redirect:/allTeams?search=" + URLEncoder.encode(search, StandardCharsets.UTF_8);
        }
    }


    /**
     * Gets the profile page of a team
     * @param id    the id of the team to be displayed
     * @param isVisibleMembers if the members section is expanded or not
     * @param model (map-like) representation of results to be used by thymeleaf
     * @return teamProfileView
     */
    @GetMapping("/teamProfile")
    public String getTemplate(@RequestParam(name = "id") Long id,
                              @RequestParam(name = "isVisibleMembers", defaultValue = "false") String isVisibleMembers,
                              @RequestParam(name = "isVisibleFormations", defaultValue = "false") String isVisibleFormations,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        logger.info("GET /teamProfile");

        Team currentTeam;
        try {
            currentTeam = teamService.getTeam(id).orElseThrow();
        } catch (NoSuchElementException e) {
            redirectAttributes.addAttribute("teamError", "Team does not exist");
            logger.error(e.toString());
            return REDIRECT_HOME;
        }

        List<FeedPost> flaggedPosts = feedPostService.getFlaggedTeamFeedPosts(id);
        List<Comment> flaggedComments = commentService.getFlaggedTeamComments(id);
        model.addAttribute("feedPosts", feedPostService.getTeamFeedPosts(id));
        model.addAttribute("flaggedFeedPosts", flaggedPosts);
        model.addAttribute("flaggedPostsPresent", !flaggedPosts.isEmpty());
        model.addAttribute("flaggedComments", flaggedComments);
        model.addAttribute("flaggedCommentsPresent", !flaggedComments.isEmpty());
        if (Boolean.parseBoolean((String) model.asMap().get("flag"))) {
            model.addAttribute("flaggedPost", true);
        }

        List<String> usersFlagged = new ArrayList<>();
        for (Comment comment : flaggedComments) {
            usersFlagged.add(comment.getAuthor().getFirstName() + " " + comment.getAuthor().getLastName());
        }
        model.addAttribute("usersFlagged", usersFlagged);



        model.addAttribute(IS_MANAGER, teamMemberController.checkIsManager(id));
        model.addAttribute(IS_MEMBER, teamMemberController.checkIsInTeam(id));
        model.addAttribute("isInTeam", teamMemberController.checkIsInTeam(id));
        model.addAttribute(DISPLAY_ID, id.toString());
        model.addAttribute("isVisibleMembers", isVisibleMembers);
        model.addAttribute("isVisibleFormations", isVisibleFormations);

        // Get the club for the team
        Optional<Club> teamClub = clubService.getClubFromTeam(id);
        if(teamClub.isPresent()) {
            Club club = teamClub.get();
            model.addAttribute("club", club);
        }

        teamMemberController.setTeamMemberModelAttributes(model, currentTeam);
        teamMemberController.setTeamModelAttributes(model, currentTeam);

        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getUserByEmail(email);

        List<Long> teamIds = new ArrayList<>();

        List<Activity> myTeamActivities;
        myTeamActivities = activityService.getActivitiesByTeamId(id);

        StringBuilder javascript = new StringBuilder();
        javascript.append('[');

        int i = 0;
        for (Activity activity: myTeamActivities) {
            javascript.append(activity.getJavaScriptEvent(i));
            i++;
        }
        javascript.append("]");

        model.addAttribute("activitiesJavaScript",javascript.toString());
        model.addAttribute("activities",myTeamActivities);

        ArrayList<Boolean> canEdit = new ArrayList<>();

        if (user != null) {
            teamIds = teamService.getTeamNamesWhoUserManageOrCoach(user.getId()).stream().map(Team::getId).toList();
            for (Activity activity : myTeamActivities) {
                if (activityService.getByIdCanEdit(activity.getId(), user) != null) {
                    canEdit.add(true);
                } else {
                    canEdit.add(false);
                }
            }
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("canFollowTeam", !teamService.isFollowingTeam(user,currentTeam));
            model.addAttribute("followingTeam", teamService.isFollowingTeam(user,currentTeam));
        } else {
            model.addAttribute("isLoggedIn", false);
            model.addAttribute("canFollowTeam", false);
            model.addAttribute("followingTeam", false);
        }

        model.addAttribute("canEdit", canEdit);
        boolean canPost = teamIds.contains(id);
        model.addAttribute("canPost",canPost);
        model.addAttribute("id", id);
        model.addAttribute("clubId", -1);


        return "teamProfileView";
    }

    /**
     * Upload an image file to team profile
     * @param id                  id of the team
     * @param file                multipartFile to be uploaded
     * @param redirectAttributes  attributes to add to the page that the user is redirected to
     * @return                    redirect:/teamProfile
     * @throws IOException        if I/O exception
     * @throws MultipartException if the resolution fails
     */
    @PostMapping("/teamProfile")
    public String uploadImage(@RequestParam(name = "id") Long id,
                              @RequestParam("file") MultipartFile file,
                              RedirectAttributes redirectAttributes) throws IOException, MultipartException {
        logger.info("POST /teamProfile");
        Team team;
        try {
            team = teamService.getTeam(id).orElseThrow();
        } catch (NoSuchElementException e) {
            logger.error(e.toString());
            redirectAttributes.addAttribute("teamError", "Team does not exist");
            return REDIRECT_HOME;
        }
        // Save the image file to disk
        try {
            String fileName = ImageHandler.uploadImage(file, "teamProfile-" + id, "profilePics");
            team.setProfilePicName(fileName);
            teamService.addProfilePic(team);

            redirectAttributes.addAttribute("id", id);
            return REDIRECT_OWN_TEAM_PAGE;
        } catch (ImageTypeException e) {
            redirectAttributes.addAttribute("id", id);
            redirectAttributes.addFlashAttribute(ERROR, "Only JPG, PNG and SVG are allowed");
            return REDIRECT_OWN_TEAM_PAGE;
        } catch (ImageSizeException e) {
            redirectAttributes.addAttribute("id", id);
            redirectAttributes.addFlashAttribute("fileSizeError", "Exceeded max file size of 10MB");
            return REDIRECT_OWN_TEAM_PAGE;

        }

    }


    /**
     * Regenerates the team's invite token
     * @param id    the id of the team
     * @return teamProfileView
     */
    @GetMapping("/regenerateToken")
    public String regenerateToken(@RequestParam(name = "id") Long id,
                                  HttpServletResponse httpServletResponse) {
        logger.info("GET /regenerateToken");
        Team currentTeam;
        try {
            currentTeam = teamService.getTeam(id).orElseThrow();
        } catch (NoSuchElementException e) {
            logger.error(e.toString());
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return REDIRECT_HOME;
        }

        String token = Team.generateToken();
        currentTeam.setToken(token);
        teamService.updateTeam(currentTeam);
        return REDIRECT_TEAM_PAGE + URLEncoder.encode(String.valueOf(id), StandardCharsets.UTF_8);
    }

    /**
     * Allows a user to follow a team
     * @param id      the id of the team to follow
     * @param request the request information
     * @return Error page or the team's profile page
     */
    @PostMapping("/followTeam")
    public String followTeam(@RequestParam(name = "id") Long id,
                             HttpServletRequest request) {
        logger.info("POST /followTeam");
        Team currentTeam;
        try {
            currentTeam = teamService.getTeam(id).orElseThrow();
        } catch (NoSuchElementException e) {
            logger.error(e.toString());
            return REDIRECT_HOME;
        }

        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getUserByEmail(email);
        user.followTeam(currentTeam);
        userService.save(user);
        Optional<String> old = Optional.ofNullable(request.getHeader("Referer")).map(requestUrl -> REDIRECT + requestUrl);
        return old.orElse(REDIRECT_HOME);
    }

    /**
     *  Unfollow a team that you have followed
     * @param id      the id of the team to unfollow
     * @param request the request information
     * @return redirect home or back to the team page depending on whether unfollow was successful
     */
    @PostMapping("/unfollowTeam")
    public String unfollowTeam(@RequestParam(name = "id") Long id,
                               HttpServletRequest request) {
        logger.info("POST /unfollowTeam");
        Team currentTeam;
        try {
            currentTeam = teamService.getTeam(id).orElseThrow();
        } catch (NoSuchElementException e) {
            logger.error(e.toString());
            return REDIRECT_HOME;
        }

        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getUserByEmail(email);
        if (user == null) {
            return REDIRECT_HOME;
        }
        else if (teamService.isFollowingTeam(user,currentTeam)) {
            user.unfollowTeam(currentTeam);
            userService.save(user);
        }
        Optional<String> old = Optional.ofNullable(request.getHeader("Referer")).map(requestUrl -> REDIRECT + requestUrl);
        return old.orElse(REDIRECT_HOME);
    }


    /**
     * Gets the page that is used to create team posts for a team.
     * @param id the id of the team being posted to.
     * @param model is used to enter information into the page.
     * */
    @GetMapping("/teamPost")
    public String getPostForm(@RequestParam(value="id") Long id, Model model) {
        model.addAttribute("submitPage","teamPost");
        model.addAttribute("title", "Add a Team Post");
        model.addAttribute("id", id);
        return "postForm";
    }

    /**
     * Creates a new post for a team
     * @param id                 the id of the team making the post
     * @param title              the title of the post
     * @param description        the post's description
     * @param file               the post's optional attachment
     * @param redirectAttributes the redirect attributes
     * @return redirect to home if invalid, to the user's profile if valid
     */
    @PostMapping("/teamPost")
    public String createPost(@RequestParam(value = "id") Long id,
                             @RequestParam(value = "postTitle") String title,
                             @RequestParam(value = "postDescription") String description,
                             @RequestParam(value = "postAttachment", required = false) MultipartFile file,
                             RedirectAttributes redirectAttributes) {
        User user =  userService.getUserByEmail((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        List<Long> teamIds =  teamService.getTeamNamesWhoUserManageOrCoach(user.getId()).stream().map(Team::getId).toList();
        boolean isValid = true;

        if (!teamIds.contains(id)) {
            redirectAttributes.addFlashAttribute(ERROR, "You must be a manager or coach of the team to add a post.");
            isValid = false;
        }

        isValid = ClubController.isPostValid(title, description, redirectAttributes, isValid);
        if (!isValid) {
            addPostAttributes(redirectAttributes, id, title, description);
            return REDIRECT_TEAM_POST;
        }

        try {
            String fileName = null;
            if (!Objects.requireNonNull(file.getContentType()).substring(Objects.requireNonNull(file.getContentType()).lastIndexOf("/") + 1).equals("octet-stream")) {
                fileName = ImageHandler.uploadAttachment(file, ("attachment-" + LocalDateTime.now()).replace(":", ""), "postAttachments");
            }
            User currentUser =  userService.getUserByEmail((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            if (teamService.getTeam(id).isPresent()) {
                FeedPost feedPost = new FeedPost(id, OwnerType.TEAM, teamService.getTeam(id).orElseThrow().getName(), title, description, currentUser, fileName);
                feedPostService.save(moderationAPIController.moderateFeedPost(feedPost));
                if (feedPost.getFlagged()) {
                    redirectAttributes.addFlashAttribute("flag", "true");
                }
            }

        } catch (NullPointerException | IOException | NoSuchElementException e) {
            logger.error(e.toString());
            redirectAttributes.addFlashAttribute(ERROR,"Failed to Save.");
            addPostAttributes(redirectAttributes, id, title, description);
            return REDIRECT_TEAM_POST;
        } catch (ImageTypeException e) {
            redirectAttributes.addFlashAttribute(ERROR,"File type is invalid. Only JPG, PNG SVG, MP4, WEBM and OGG are allowed.");
            addPostAttributes(redirectAttributes, id, title, description);
            return REDIRECT_TEAM_POST;
        } catch (ImageSizeException e) {
            redirectAttributes.addFlashAttribute(ERROR,"Failed to Save. File is too large.");
            addPostAttributes(redirectAttributes, id, title, description);
            return REDIRECT_TEAM_POST;
        }
        return REDIRECT_TEAM_PAGE + URLEncoder.encode(String.valueOf(id), StandardCharsets.UTF_8);
    }

    /**
     * Adds the given attributes to the model, so that they can be displayed when the page is redirected.
     * @param ra          the redirect attributes
     * @param id          the id of the team
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

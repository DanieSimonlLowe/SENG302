package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.exceptions.ImageSizeException;
import nz.ac.canterbury.seng302.tab.exceptions.ImageTypeException;
import nz.ac.canterbury.seng302.tab.service.*;
import nz.ac.canterbury.seng302.tab.service.checkers.RegistrationChecker;
import nz.ac.canterbury.seng302.tab.service.checkers.SearchChecker;
import nz.ac.canterbury.seng302.tab.service.email.EmailService;
import nz.ac.canterbury.seng302.tab.service.email.PasswordEmailHandler;
import nz.ac.canterbury.seng302.tab.service.email.RegistrationEmailHandler;
import nz.ac.canterbury.seng302.tab.service.sorters.UserAlphaNameSorter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.Optional;

/**
 * This the basic spring boot controller for user logic, note the @link{Controller} annotation which defines this.
 * This controller defines endpoints as functions with specific HTTP mappings
 */
@Controller
public class UserController {

    /** The logger */
    final Logger logger = LoggerFactory.getLogger(UserController.class);

    /** The UserService for database logic */
    private final UserService userService;

    /** The TeamService for database logic */
    private final TeamService teamService;

    /** The LocationService for database logic */
    private final LocationService locationService;


    /** The SportService for database logic */
    private final SportService sportService;

    /** The TeamMemberService for database logic */
    private final TeamMemberService teamMemberService;

    /** The handler for the registration emails */
    private final RegistrationEmailHandler registrationEmailHandler;

    /** The handler for the reset password emails */
    private final PasswordEmailHandler passwordEmailHandler;

    /** The service for registration tokens */
    private final TokenService registrationTokenService;

    /** The service for emails */
    private final EmailService emailService;

    /** The TableController for common table methods */
    private final TableController tableController;

    /** The FeedPostService for database logic */
    private final FeedPostService feedPostService;

    /** Error string constant - used in the model */
    private static final String ERROR = "error";

    /** Invalid email string constant */
    private static final String INVALID_EMAIL = "invalidEmail";

    /** Invalid email message string constant */
    private static final String INVALID_EMAIL_MESSAGE = "Invalid email";

    /** Invalid password string constant */
    private static final String INVALID_PASSWORD = "invalidPassword";

    /** Invalid first name string constant */
    private static final String INVALID_FNAME = "invalidFName";
    
    /** Invalid last name string constant */
    private static final String INVALID_LNAME = "invalidLName";

    /** Invalid date of birth string constant */
    private static final String INVALID_DOB = "invalidDOB";

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

    /** Logged in string constant */
    private static final String LOGGED_IN = "loggedIn";

    /** Anonymous user string constant */
    private static final String ANONYMOUS_USER = "anonymousUser";

    /** Email string constant */
    private static final String EMAIL = "email";

    /** First name string constant */
    private static final String FNAME = "fName";

    /** Last name string constant */
    private static final String LNAME = "lName";

    /** Date of birth string constant */
    private static final String DOB = "dateOfBirth";

    /** Location string constant */
    private static final String LOCATION = "location";

    /** Suburb string constant */
    private static final String SUBURB = "suburb";

    /** Page size constant */
    private static final int PAGE_SIZE = 10;

    /** Url string constant */
    private static final String URL_MID_PAGE = "&page=";

    /** Url string constant */
    private static final String REDIRECT_LOGIN = "redirect:/login";

    /** Url string constant */
    private static final String REDIRECT_PROFILE = "redirect:/profile";

    /** Url string constant */
    private static final String REDIRECT_ERROR = "redirect:/error";

    /** Url string constant */
    private static final String REDIRECT_HOME = "redirect:/";

    /** Url string constant */
    private static final String REDIRECT = "redirect:";

    /** All profiles string constant */
    private static final String ALL_PROFILES = "allProfiles";

    /** No teams constant */
    private static final String NO_TEAMS = "noTeams";

    /** No sports constant */
    private static final String NO_SPORTS = "noSports";


    /**
     * Constructor for the UserController class
     * @param userService              the UserService
     * @param sportService             the SportService
     * @param registrationEmailHandler the handler for the registration email
     * @param passwordEmailHandler     the handler for reset password emails
     * @param registrationTokenService the service for the registration token
     * @param emailService             the service for the email
     * @param tableController          the table controller, for handling common table methods
     * @param teamMemberService        the service for team members
     * @param teamService              the service for teams
     * @param feedPostService          the service for the feed post
     */
    @Autowired
    public UserController(UserService userService,
                          SportService sportService,
                          RegistrationEmailHandler registrationEmailHandler,
                          PasswordEmailHandler passwordEmailHandler,
                          TokenService registrationTokenService,
                          EmailService emailService,
                          TableController tableController,
                          LocationService locationService,
                          TeamMemberService teamMemberService,
                          TeamService teamService,
                          FeedPostService feedPostService) {
        this.userService = userService;
        this.sportService = sportService;
        this.registrationEmailHandler = registrationEmailHandler;
        this.passwordEmailHandler = passwordEmailHandler;
        this.registrationTokenService = registrationTokenService;
        this.emailService = emailService;
        this.tableController = tableController;
        this.locationService = locationService;
        this.teamMemberService = teamMemberService;
        this.teamService = teamService;
        this.feedPostService = feedPostService;
    }

    /**
     * Getter of the local date
     * @param date the local date
     * @return the local date in the format DD : MM : YYYY
     */
    static String getDateDisplay(LocalDate date) {
        return date.getDayOfMonth() + " / " + date.getMonthValue() + " / " + date.getYear();
    }

    /**
     * Checks if a user is logged in
     * @return boolean whether the user is logged in
     */
    @ModelAttribute("loggedIn")
    private boolean checkIsLoggedIn() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return !username.equals(ANONYMOUS_USER) && !username.equals("anonymous");
    }

    /**
     * Adds the teams owned by the current user to the Thymeleaf model for use on the navbar
     * @return the list of teams owned by the current user
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
     * Gets the thymeleaf page representing the /register page - the registration page for new users
     * @return thymeleaf demoTemplate
     */
    @GetMapping("/register")
    public String register() {
        logger.info("GET /register");
        if (checkIsLoggedIn()) {
            return REDIRECT_PROFILE;
        }
        return "registration";
    }

    /**
     * Posts a form response from a user's registration
     * @param ra              attributes for teh page that the user is redirected to
     * @param firstName       user's first name
     * @param lastName        user's last name
     * @param email           user's email address
     * @param password        user's password
     * @param confirmPassword user's password confirmation
     * @param dateOfBirth     user's date of birth
     * @param address1   street address 1 of the user's location
     * @param address2   street address 1 of the user's location
     * @param suburb      the suburb of the user's location
     * @param postcode    the user's postcode
     * @param city        the user's city
     * @param country     the user's country
     * @param model (map-like) representation with values being set to relevant parameters provided
     * @return thymeleaf login (redirected) if register details are valid, or back to register if details are invalid
     */
    @PostMapping("/register")
    public String createUser(RedirectAttributes ra,
                             @RequestParam(name = "firstName") String firstName,
                             @RequestParam(name = "lastName") String lastName,
                             @RequestParam(name = "email") String email,
                             @RequestParam(name = "password") String password,
                             @RequestParam(name = "confirmPassword") String confirmPassword,
                             @RequestParam(name = "DOB") String dateOfBirth,
                             @RequestParam(name = "address1", defaultValue = "") String address1,
                             @RequestParam(name = "address2", defaultValue = "") String address2,
                             @RequestParam(name = "suburb", defaultValue = "") String suburb,
                             @RequestParam(name = "postcode", defaultValue = "") String postcode,
                             @RequestParam(name = "city") String city,
                             @RequestParam(name = "country") String country,
                             Model model) {

        logger.info("POST /register");

        boolean isValid = true;

        if (userService.getUserByEmail(email) != null || registrationTokenService.findByEmail(email) != null) {
            model.addAttribute(INVALID_EMAIL, "This email is already in use");
            isValid = false;
        }
        if (!RegistrationChecker.isValidEmail(email)) {
            logger.info("Email is invalid");
            model.addAttribute(INVALID_EMAIL, INVALID_EMAIL_MESSAGE);
            isValid = false;
        }
        if (!RegistrationChecker.isPasswordValid(password,new String[]{firstName,lastName,email})) {
            model.addAttribute(INVALID_PASSWORD, "Password must be at least 8 characters and contain a number and a special character");
            isValid = false;
        }
        if (!password.equals(confirmPassword)) {
            model.addAttribute(INVALID_PASSWORD, "Passwords do not match");
            isValid = false;
        }
        if (!RegistrationChecker.isValidName(firstName)) {
            model.addAttribute(INVALID_FNAME, "First name is not valid");
            isValid = false;
        }
        if (!RegistrationChecker.isValidName(lastName)) {
            model.addAttribute(INVALID_LNAME, "Last name is not valid");
            isValid = false;
        }

        if (!RegistrationChecker.isLocationValid(address1, address2, suburb, postcode, city, country)) {
            setLocationError(model, address1, address2, suburb, postcode, city, country);
            isValid = false;
        }

        if(!dateOfBirth.matches("^\\d{4}-\\d{2}-\\d{2}$")){
            isValid = false;
            model.addAttribute(INVALID_DOB, "Invalid Date Format");
        } else if (!RegistrationChecker.isBirthDateValid(LocalDate.parse(dateOfBirth))) {
            model.addAttribute(INVALID_DOB, "Enter a valid DOB. Must be 13 years or older");
            isValid = false;
        }


        if(!isValid) {
            model.addAttribute(EMAIL,email);
            model.addAttribute("firstName", firstName);
            model.addAttribute("lastName", lastName);
            model.addAttribute("DOB", dateOfBirth);
            model.addAttribute("address1", address1);
            model.addAttribute("address2", address2);
            model.addAttribute("suburb", suburb);
            model.addAttribute("postcode", postcode);
            model.addAttribute("city", city);
            model.addAttribute("country", country);
            return "registration";
        }
        Location newLocation = new Location(address1, address2, suburb, postcode, city, country);
        locationService.addLocation(newLocation);
        if (!registrationEmailHandler.setUpRegistration(email, firstName, lastName, LocalDate.parse(dateOfBirth), newLocation, password) ) {
            ra.addFlashAttribute("loginInfo", "There was a problem in sending the email please try again.");
            return REDIRECT_LOGIN;
        } else if (checkIsLoggedIn()) {
            return REDIRECT_PROFILE;
        } else {
            ra.addFlashAttribute("loginInfo", "You're almost fully registered. Please check your email for a confirmation link before logging in.");
            return REDIRECT_LOGIN;
        }

    }

     /**
      * Gets the thymeleaf page representing the /login page - the login page for users     
      * @param error any errors as a result of a user inputting invalid credentials
      * @param model (map-like) representation of name, language and isJava boolean for use in thymeleaf
      * @return thymeleaf login
      */
    @GetMapping("/login")
    public String getTemplate(@RequestParam(value=ERROR, required = false) BadCredentialsException error,
                              Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Email address is unknown or the password is invalid");
            model.addAttribute("isError", true);
        }
        return "login";
    }

    /**
     * Posts a form response for the user who is logging in
     * 
     * @param email      the email of the user logging in 
     * @param password   the password of the user logging in
     * @return thymeleaf login
     */
    @PostMapping("/login")
    public String loginForm(@RequestParam(name="email") String email,
                            @RequestParam(name="password") String password) {
        logger.info("POST /login");
        return "login";
    }

    /**
     * Logs the user out and redirects them to the login page    
     * @param request  the request information
     * @param response the response information
     * @return thymeleaf login
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        logger.info("GET /logout");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            authentication.setAuthenticated(false);
            SecurityContextLogoutHandler ctx = new SecurityContextLogoutHandler();
            ctx.setClearAuthentication(true);
            ctx.logout(request, response, authentication);
        }
        return REDIRECT_LOGIN;
    }

    /**
     * Gets the thymeleaf page representing the /profile page - the user's profile page
     * @param model (map-like) representation of name, language and isJava boolean for use in thymeleaf
     * @return thymeleaf profile
     */
    @GetMapping("/profile")
    public String profileView(Model model) {
        logger.info("GET /profile");

        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getUserByEmail(email);

        if(user == null) {
            return REDIRECT_LOGIN;
        }
        model.addAttribute(EMAIL,user.getEmail());
        model.addAttribute(FNAME,user.getFirstName());
        model.addAttribute(LNAME,user.getLastName());
        model.addAttribute(DOB,getDateDisplay(user.getDate()));
        model.addAttribute(LOCATION,user.getLocationString());
        model.addAttribute("image",user.getProfilePicName());
        model.addAttribute("selectedPrivacy",user.getPrivacyType());

        List<FeedPost> feedPosts = feedPostService.getPersonalFeedPosts(user.getId());
        model.addAttribute("feedPosts", feedPosts);
        model.addAttribute("isManager", false);

        List<User> initSuggestedUsers = List.of(userService.getRecommendedUsers(user));
        List<User> suggestedUsers = new ArrayList<>();

        List<Team> initSuggestedTeams = teamService.getRecommendedTeams(user);
        List<Team> suggestedTeams = new ArrayList<>();




        for ( int i = 0; i < initSuggestedUsers.size(); i++) {
            if (!userService.findFollowedUsersByUserId(user.getId()).contains(initSuggestedUsers.get(i))) {
                suggestedUsers.add(initSuggestedUsers.get(i));
            }
        }

        for ( int i = 0; i < initSuggestedTeams.size(); i++) {
            if (!teamService.getFollowedTeams(user).contains(initSuggestedTeams.get(i))) {
                suggestedTeams.add(initSuggestedTeams.get(i));
            }
        }

        model.addAttribute("suggestedUsers", suggestedUsers);
        model.addAttribute("suggestedTeams", suggestedTeams);
        model.addAttribute("clubId", -1);

        setupProfileModelAttributes(model, user);



        return "profile";
    }

    /**
     * Handles the change in someone's profile picture.
     * @param file the picture being uploaded
     * @param redirectAttributes attributes for the page that the user is redirected to
     * @return redirection link to the user's profile
     */
    @PostMapping("/profile")
    public String profilePictureUpload(@RequestParam("file") MultipartFile file,
                                       RedirectAttributes redirectAttributes) {
        logger.info("POST /profile");
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getUserByEmail(email);
        // Save the image file to disk
        try {
            String fileName = ImageHandler.uploadImage(file, "userProfile-" + user.getId(), "profilePics");
            user.setProfilePicName(fileName);
            userService.save(user);
        } catch (ImageTypeException e) {
            redirectAttributes.addFlashAttribute(ERROR, "Only JPG, SVG and PNG files are allowed");
        } catch (ImageSizeException e) {
            redirectAttributes.addFlashAttribute("fileSizeError", "Exceeded max file size of 10MB");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute(ERROR, "There was an error uploading your image");
        }
        return REDIRECT_PROFILE;
    }

    /**
     * Gets the thymeleaf page representing the /edit page - the user's edit profile
     * @param model (map-like) representation of name, language and isJava boolean for use in thymeleaf
     * @return thymeleaf editor
     */
    @GetMapping("/edit")
    public String editView(Model model) {
        logger.info("GET /edit");


        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getUserByEmail(email);
        List<Sport> sports = user.getSports();

        List<String> favSportsList = new ArrayList<>();
        if (sports != null) {
            for (Sport sport : sports) {
                favSportsList.add(sport.getSportName());
            }
        }

        List<String> sportsFromRepo = new ArrayList<>();
        for (Sport savedSport : sportService.findAll()) {
            sportsFromRepo.add(savedSport.getSportName());
        }

        StringBuilder sportOptions = new StringBuilder();
        for (String sport : sportsFromRepo) {
            sportOptions.append("<option value=\"").append(sport).append("\">").append(sport).append("</option>");
        }
        StringBuilder favSportsDisplay = new StringBuilder();
        StringBuilder htmlSports = new StringBuilder();
        for (String sport : favSportsList) {
            favSportsDisplay.append("<div><span>").append(sport).append("</span><button onClick=\"deleteSport(this)\" type=\"button\" value=\"").append(sport).append("\">Delete</button></div>,");
            htmlSports.append("<div><span>").append(sport).append("</span><button onClick=\"deleteSport(this)\" type=\"button\" value=\"").append(sport).append("\">Delete</button></div>");
        }

        model.addAttribute("sportOptions", sportOptions);
        model.addAttribute("favSportsDisplay", htmlSports.toString());
        model.addAttribute("sportHtml", favSportsDisplay.toString());
        model.addAttribute("sportNames", favSportsList);

        model.addAttribute(EMAIL,user.getEmail());
        model.addAttribute(FNAME,user.getFirstName());
        model.addAttribute(LNAME,user.getLastName());
        model.addAttribute("DOB",user.getDate());
        model.addAttribute(LOCATION,user.getLocationEditString());
        model.addAttribute("address1", user.getAddressOne());
        model.addAttribute("address2", user.getAddressTwo());
        model.addAttribute(SUBURB,user.getSuburb());
        model.addAttribute("postcode", user.getPostcode());
        model.addAttribute("city", user.getCity());
        model.addAttribute("country", user.getCountry());
        return "editor";
    }

    /**
     * Posts the form response of the user's updated details
     * @param email       the user's current email
     * @param firstName   the user's current first name
     * @param lastName    the user's current last name
     * @param dateOfBirth the user's current date of birth
     * @param favSports   a list of the user's favourite sports
     * @param address1   street address 1 of the user's location
     * @param address2   street address 1 of the user's location
     * @param suburb      the suburb of the user's location
     * @param postcode    the user's postcode
     * @param city        the user's city
     * @param country     the user's country
     * @param model       (map-like) representation of name, language and isJava boolean for use in thymeleaf,
     *                    with values being set to relevant parameters provided
     * @return thymeleaf editor if details are invalid, redirected to the user's profile if details are valid
     */
    @PostMapping("/edit")
    public String editProfile(@RequestParam(name="email") String email,
                              @RequestParam(name="fName") String firstName,
                              @RequestParam(name="lName") String lastName,
                              @RequestParam(name="DOB") String dateOfBirth,
                              @RequestParam(name="favSportsList") List<String> favSports,
                              @RequestParam(name="favSportsListUpdated") boolean favSportsListUpdated,
                              @RequestParam(name = LOCATION, defaultValue = "") String location,
                              @RequestParam(name = "address1", defaultValue = "") String address1,
                              @RequestParam(name = "address2", defaultValue = "") String address2,
                              @RequestParam(name="suburb", defaultValue = "") String suburb,
                              @RequestParam(name="postcode", defaultValue = "") String postcode,
                              @RequestParam(name="city") String city,
                              @RequestParam(name="country") String country,
                              Model model) {
        logger.info("POST /edit");

        String oldEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean isValid = true;
        String output = "";
        if (!email.equalsIgnoreCase(oldEmail)) {
            if (userService.getUserByEmail(email) != null) {
                model.addAttribute(INVALID_EMAIL, "Email is already in use");
                isValid = false;
            } else if (!RegistrationChecker.isValidEmail(email)) {
                model.addAttribute(INVALID_EMAIL, INVALID_EMAIL_MESSAGE);
                isValid = false;
            }
        }
        if (!RegistrationChecker.isValidName(firstName)) {
            model.addAttribute(INVALID_FNAME, "First name is not valid");
            isValid = false;
        }
        if (!RegistrationChecker.isValidName(lastName)) {
            model.addAttribute(INVALID_LNAME, "Last name is not valid");
            isValid = false;
        }
        if (!RegistrationChecker.isLocationValid(address1, address2, suburb, postcode, city, country)) {
            setLocationError(model, address1, address2, suburb, postcode, city, country);
            isValid = false;
        }

        if(!dateOfBirth.matches("^\\d{4}-\\d{2}-\\d{2}$")){
            isValid = false;
            model.addAttribute(INVALID_DOB, "Invalid Date Format");
        } else if (!RegistrationChecker.isBirthDateValid(LocalDate.parse(dateOfBirth))) {
            model.addAttribute(INVALID_DOB, "Enter a valid DOB. Must be 13 years or older");
            isValid = false;
        }

        List<Sport> favSportsList = new ArrayList<>();

        for (String sportName : favSports) {
            if (sportService.findSportByName(sportName) == null) {
                sportService.add(new Sport(sportName));
            }
            Sport sport = sportService.findSportByName(sportName);
            if (favSportsList.stream().noneMatch(s -> (Objects.equals(s.getId(), sport.getId())))) {
                favSportsList.add(sport);
            }
        }

        // Updates the user's details if they are valid
        if (isValid) {
            User user = userService.getUserByEmail(oldEmail);
            user.setDate(LocalDate.parse(dateOfBirth));
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.editLocation(address1, address2, suburb, postcode, city, country);
            user.setSports(favSportsList);
            userService.save(user);
            if (email.equalsIgnoreCase(oldEmail)) {
                return REDIRECT_PROFILE;
            } else {
                return "redirect:/confirmEmailEdit?email="+email;
            }
        }
        model.addAttribute(ERROR,output);
        model.addAttribute(EMAIL,email);
        model.addAttribute(FNAME,firstName);
        model.addAttribute(LNAME,lastName);
        model.addAttribute("DOB",dateOfBirth);
        model.addAttribute(LOCATION,location);
        model.addAttribute("address1", address1);
        model.addAttribute("address2", address2);
        model.addAttribute(SUBURB,suburb);
        model.addAttribute("postcode", postcode);
        model.addAttribute("city", city);
        model.addAttribute("country", country);

        if (!favSportsListUpdated) {
            String email2 = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userService.getUserByEmail(email2);
            List<Sport> sports = user.getSports();
            if (sports != null) {
                for (Sport sport : sports) {
                    favSports.add(sport.getSportName());
                }
            }
        }
        List<String> sportsFromRepo = new ArrayList<>();
        for (Sport savedSport : sportService.findAll()) {
            sportsFromRepo.add(savedSport.getSportName());
        }

        StringBuilder favSportsDisplay = new StringBuilder();
        StringBuilder htmlSports = new StringBuilder();
        for (String sport : favSports) {
            favSportsDisplay.append("<div><span>").append(sport).append("</span><button onClick=\"deleteSport(this)\" type=\"button\" value=\"").append(sport).append("\">Delete</button></div>,");
            htmlSports.append("<div><span>").append(sport).append("</span><button onClick=\"deleteSport(this)\" type=\"button\" value=\"").append(sport).append("\">Delete</button></div>");
        }
        model.addAttribute("sportOptions", sportsFromRepo);
        model.addAttribute("favSportsDisplay", htmlSports.toString());
        model.addAttribute("sportHtml", favSportsDisplay.toString());
        model.addAttribute("sportNames", favSports);
        return "editor";
    }

    @GetMapping("/confirmEmailEdit")
    public String confirmEditView(@RequestParam(value="email") String email, Model model) {
        model.addAttribute("email",email);
        return "userConfirmEmailEdit";
    }

    @PostMapping("/confirmEmailEdit")
    public String confirmEdit(@RequestParam(value="email") String email, RedirectAttributes ra) {
        User user = userService.getUserByEmail((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        user.setEmail(email);
        user.deactivate();
        if (registrationEmailHandler.updateUser(user)) {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(ANONYMOUS_USER, null));
            ra.addFlashAttribute("loginInfo", "A confirmation email has been sent to you.");
            return REDIRECT_LOGIN;
        } else {
            ra.addFlashAttribute(ERROR,"Server error: Failed to send email.");
            return "redirect:/edit";
        }
    }

    /**
     * Updates the user's email address
     * @param token the token the user has been given to update their email address
     * @param ra  redirect attributes
     * @return redirect to the login page
     */
    @GetMapping("/updateUser")
    public String confirmUser (@RequestParam(value="token", defaultValue="") String token, RedirectAttributes ra) {
        User user = userService.findByUpdateToken(token);
        user.activate();
        userService.save(user);

        ra.addFlashAttribute("loginInfo", "Your account has now been updated.");
        return REDIRECT_LOGIN;
    }

    /**
     * Gets the thymeleaf page representing the /allProfiles page - displays a list of users based on a search query
     * @param page           the page of users to display
     * @param search         the search query
     * @param selectedSports the selected sports to filter the users by
     * @param selectedCities the selected cities to filter the users by
     * @param model          (map-like) representation of name, language and isJava boolean for use in thymeleaf
     * @return thymeleaf allProfiles
     */
    @GetMapping("/allProfiles")
    public String viewProfiles (@RequestParam(value="page", defaultValue = "1") Integer page,
                                @RequestParam(value="search", required = false) String search,
                                @RequestParam(value="selectedSports", required = false) List<Long> selectedSports,
                                @RequestParam(value="selectedCities", required = false) List<String> selectedCities,
                                HttpServletRequest httpServletRequest,
                                Model model) {
        logger.info("GET /allProfiles");

        Filter filter = tableController.initializeOptionalParameters(search, selectedSports, selectedCities);

        // Retrieve users from the database depending on the filtering and search parameters
        List<User> filteredUsers;
        List<User> nonFilteredUsers;
        if(search == null || search.strip().length() < 3) {
            filteredUsers = userService.filterUsers(filter.getCities(), filter.getSports());
            nonFilteredUsers = userService.findAll();
        } else {
            filteredUsers = userService.filterUsersKeyword(filter.getSearch(), filter.getCities(), filter.getSports());
            nonFilteredUsers = userService.findByFirstOrLastName(filter.getSearch());
        }

        // Sort users if length greater than 1
        if (filteredUsers.size() > 1) { filteredUsers.sort(new UserAlphaNameSorter()); }

        // Set up pageable object
        Page<User> usersPage = tableController.convertToPage(page, PAGE_SIZE, filteredUsers);

        // Populate the sports and cities list for the dropdown options
        List<Sport> allSports = sportService.findAll();
        List<String> allCities = userService.getCitiesFromUsers(nonFilteredUsers);

        // Set model attributes
        tableController.setModelAttributes(model, usersPage, ALL_PROFILES, page, allSports, allCities, filter);

        // Retrieve the URI and append the query string if it exists
        String requestUri = ALL_PROFILES;
        String queryString = httpServletRequest.getQueryString();
        if (queryString != null) {
            requestUri += "?" + queryString;
        }
        model.addAttribute("previousSearchUri", requestUri);

        // Add empty table message if the table is empty
        if (filteredUsers.isEmpty()) { model.addAttribute("noUsersDisplay", tableController.emptyEntriesMessage("users", search)); }
        return ALL_PROFILES;
    }

    /**
     * Posts the form response of the search query for finding users
     * @param ra    redirect attributes, to add attributes to the page that the user is redirected to
     * @param search  the search query
     * @return thymeleaf search, if an error occurs, or thymeleaf allProfiles if a valid query
     */
    @PostMapping("/search")
    String getSearch(RedirectAttributes ra,
                     @RequestParam(name="previousSearchUri", required = false) String previousSearchUri,
                     @RequestParam(name="search") String search) {
        logger.info("POST /search");
        if (!SearchChecker.isValidSearch(search)) {
            ra.addFlashAttribute("searchError", "Invalid search. Can only contain characters present in a name and search must contain at least 3 characters.");
            ra.addFlashAttribute(LOGGED_IN, checkIsLoggedIn());
            return REDIRECT + previousSearchUri;
        } else {
            return "redirect:/allProfiles?search=" + URLEncoder.encode(search, StandardCharsets.UTF_8);
        }
    }


    /**
     * Gets the thymeleaf page representing the /otherProfile page - a display page for other users
     * @param email the user to display's email
     * @param prevPage the page the user was on before clicking on the user's profile
     * @param model (map-like) representation of name, language and isJava boolean for use in thymeleaf
     * @return thymeleaf otherProfile
     */
    @GetMapping("/otherProfile")
    String getOtherProfile(@RequestParam(value="email") String email,
                           @RequestParam(value="prevPage", required = false) Integer prevPage,
                           @RequestParam(value="search", required = false) String search,

                           Model model) {

        String currentSearch = "";
        int currentPage = 0;
        String backToSearch = "<a class=\"light-button-md\" href=\"/allProfiles?search=" + currentSearch + URL_MID_PAGE + currentPage + "\">< Back to user list</a>";

        model.addAttribute("backToSearch",backToSearch);

        User user = userService.getUserByEmail(email);
        User loggedInUser = userService.getUserByEmail((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        if(loggedInUser != null) {
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("canFollow",!loggedInUser.isFollowing(user) && !Objects.equals(user.getId(), loggedInUser.getId()));
            model.addAttribute("userId",user.getId());
            model.addAttribute("friends", userService.areFriends(loggedInUser, user));
        }

        model.addAttribute(EMAIL,user.getEmail());
        model.addAttribute(FNAME,user.getFirstName());
        model.addAttribute(LNAME,user.getLastName());
        model.addAttribute(DOB,user.getDate());
        model.addAttribute(LOCATION,user.getLocationString());
        model.addAttribute("image",user.getProfilePicName());
        model.addAttribute("prevPage",prevPage);
        model.addAttribute("search",search);

        setupProfileModelAttributes(model, user);

        model.addAttribute("shown", user.isViewable(userService.areFriends(loggedInUser, user)));

        return "otherProfile";
    }

    /**
     * Helper function to set any model attributes necessary for the profile pages
     * @param model the model to add attributes to
     * @param user the user whose profile is being displayed
     */
    private void setupProfileModelAttributes(Model model, User user) {
        List<TeamMember> userTeams = teamMemberService.getAllTeamsFromUser(user);
        model.addAttribute(NO_TEAMS, userTeams.isEmpty());
        if (!userTeams.isEmpty()) {
            List<String> roleStr = userTeams.stream()
                    .map(userTeam -> teamMemberService.getRoleStringFromRoleNum(userTeam.getRole().ordinal()))
                    .toList();
            List<String> teamNameStr = userTeams.stream()
                    .map(userTeam -> userTeam.getTeamMemberId().getTeam().getName())
                    .toList();
            List<Long> teamIds = userTeams.stream()
                    .map(userTeam -> userTeam.getTeamMemberId().getTeam().getId())
                    .toList();
            model.addAttribute("teamRoles", roleStr);
            model.addAttribute("teamNames", teamNameStr);
            model.addAttribute("teamIds", teamIds);
        }
        List<Sport> sports = user.getSports();
        model.addAttribute(NO_SPORTS, sports.isEmpty());
        if (!sports.isEmpty()) {
            String favSportsString = sports.stream()
                    .limit(3)
                    .map(Sport::getSportName)
                    .collect(Collectors.joining(" • "));
            model.addAttribute("favSportsString", favSportsString);
        }
    }

    /**
     * Gets the thymeleaf page representing the /confirmUser page - confirms if the registration token the user has is valid.
     * @param ra    redirect attributes, to add attributes to the page that the user is redirected to
     * @param token the token that the user is trying to confirm their registration with
     * @param res   the HTTP response
     * @return thymeleaf: either redirect the user to an error page if the token they are using is invalid, or the login page
     *                    if the token is valid
     */
    @GetMapping("/confirmUser")
    public String confirmUserForm(RedirectAttributes ra,
                                  @RequestParam(value="token") String token,
                                  HttpServletResponse res) {
        boolean out = registrationEmailHandler.registerUser(token);
        if (!out) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            ra.addFlashAttribute("errorCode", "404 Not Found");
            ra.addFlashAttribute("errorMessage", "Invalid token.");
            return REDIRECT_ERROR;
        } else {
            ra.addFlashAttribute("loginInfo", "You're now fully registered. Please log in again.\n You can't use the confirmation link anymore.");
            return REDIRECT_LOGIN;
        }
    }

    /**
     * Gets the thymeleaf page representing the /error page - displays an error page when the user's registration token is invalid
     * @return thymeleaf error
     */
    @GetMapping("/error")
    public String errorPage() {
            return ERROR;
    }

    /**
     * Gets the thymeleaf page representing the /user/userPasswordReset page - allows a user to input an email to send their reset password link to
     * @return the lost password form
     */
    @GetMapping("/lostPassword")
    public String lostPasswordForm() {
        logger.info("GET /lostPassword");
        return "lostPassword";
    }

    /**
     * Posts the /users/lostPassword form to email the user to allow them to update their password
     * @param email the user's email address
     * @param model (map-like) representation of name, language and isJava boolean for use in thymeleaf
     * @return thymeleaf: userPasswordResetEmail, the reset password form to allow users to input their email address
     */
    @PostMapping("/lostPassword")
    public String getLostPasswordEmail(@RequestParam(value="email") String email,
                                        Model model,
                                        HttpServletResponse response) {
        logger.info("POST /lostPassword");
        User user = userService.getUserByEmail(email);

        if (!RegistrationChecker.isValidEmail(email)) {
            logger.info(INVALID_EMAIL_MESSAGE);
            model.addAttribute(ERROR, INVALID_EMAIL_MESSAGE);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else if (user == null) {
            logger.info("Unknown email");
            model.addAttribute("info", "An email was sent to the address if it was recognised.");
            response.setStatus(HttpServletResponse.SC_OK);
            return "lostPassword";
        } else {
            logger.info("Known email");
            model.addAttribute("info", "An email was sent to the address if it was recognised.");
            passwordEmailHandler.sendResetLink(user.getEmail(), user.getFirstName(), user.getLastName(), user.getDate(), user.getLocation(), "");
            response.setStatus(HttpServletResponse.SC_OK);
            return "lostPassword";
        }

        return "lostPassword";
    }

    /**
     * Gets the thymeleaf page representing the /resetPassword page - allows a user to update their password
     * @param ra    redirect attributes, to add attributes to the page that the user is redirected to
     * @param token the token that the user is trying to confirm their request to update their password with
     * @param res   the HTTP response
     * @return thymeleaf: either redirect the user to an error page if the token they are using is invalid, or the resetPassword page
     *                    if the token is valid
     */
    @GetMapping("/resetPassword")
    public String resetPasswordForm(RedirectAttributes ra,
                                    @RequestParam(value="token") String token,
                                    HttpServletResponse res) {
        logger.info("GET /resetPassword");
        boolean validToken = passwordEmailHandler.checkToken(token);

        if (!validToken) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            ra.addFlashAttribute("errorCode", "404 Not Found");
            ra.addFlashAttribute("errorMessage", "Invalid token.");
            return REDIRECT_ERROR;
        }
        return "resetPassword";
    }

    /**
     * Posts the /resetPassword form to email the user to allow them to update their password
     * @param newPass     the user's new password
     * @param confirmPass the user's confirmed password
     * @param token       the user's reset password token
     * @param ra          redirect attributes, to add attributes to the page that the user is redirected to
     * @return thymeleaf: either redirect the user to the resetPassword page if the inputted details are invalid, or the login page
     *                    if the new password is valid
     */
    @PostMapping("/resetPassword")
    public String resetPassword(@RequestParam(value="newPass") String newPass,
                                @RequestParam(value="confirmPass") String confirmPass,
                                @RequestParam(value="token") String token,
                                RedirectAttributes ra) {
        logger.info("POST /resetPassword");

        String email = token.substring(23);
        User user = userService.getUserByEmail(email);

        String update = user.resetPasswordDetails(newPass,confirmPass);

        if ("true".equals(update)) {
            userService.save(user);
            emailService.sendHTMLMail(email,"<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<style>" +
            "p{margin-top: 20px;margin-bottom: 20px;}" +
            "</style>" +
            "<h2>Your password has been changed.</h2>"+
            "<p>The password for the account <b>"+email+"</b> has been updated.</p>"+
            "</html>", "TAB Password Updated");

            ra.addFlashAttribute("loginInfo", "Your password has been updated. Please log in again.");
            return REDIRECT_LOGIN;
        } else {
            ra.addFlashAttribute("invalidPassword", "");
            ra.addFlashAttribute("invalidConfirm", "");

            if ("".equals(newPass)) {
                ra.addFlashAttribute("invalidPassword", "This field is required");
            }
            if (confirmPass == null || "".equals(confirmPass)) {
                ra.addFlashAttribute("invalidConfirm", "This field is required");
            }

            if ("noMatch".equals(update)) {
                ra.addFlashAttribute("invalidPassword", "New password and confirm password do not match.");
                ra.addFlashAttribute("invalidConfirm", "New password and confirm password do not match.");
            }
            if ("invalid".equals(update) && !"".equals(newPass)) {
                ra.addFlashAttribute("invalidPassword", "Password must be at least 8 characters, contain a number and a special character, and cannot be equal to any of your other details.");
            }

            ra.addFlashAttribute(LOGGED_IN, checkIsLoggedIn());
            return "redirect:/resetPassword?token=" + token;
        }
    }

    /**
     * Gets the thymeleaf page representing the /user/updatePassword page - displays a form for the user to update their password
     * @return the userPasswordUpdate form
     */
    @GetMapping("/user/updatePassword")
    public String getUpdatePasswordForm() {
        return "userPasswordUpdate";
    }

    /**
     * Posts the /users/updatePassword form for updating a user's password. Contains the error checking for updating a password.
     * @param oldPassword  the user's old password
     * @param newPassword1 the user's new password
     * @param newPassword2 the user's new password (confirmation)
     * @param model        (map-like) representation of name, language and isJava boolean for use in thymeleaf
     * @return thymeleaf: either redirect the user to their profile page if there are no errors, or back to the updatePassword page
     *                    if there are errors
     */
    @PostMapping("/user/updatePassword")
    public String updatePassword(@RequestParam(value="oldPassword") String oldPassword,
                                 @RequestParam(value="newPassword1") String newPassword1,
                                 @RequestParam(value="newPassword2") String newPassword2,
                                 Model model) {
                                    
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getUserByEmail(email);
        String update = user.updatePasswordDetails(oldPassword,newPassword1,newPassword2);
        if ("true".equals(update)) {
            userService.save(user);
            emailService.sendHTMLMail(email,"<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<style>" +
            "p{margin-top: 20px;margin-bottom: 20px;}" +
            "</style>" +
            "<h2>Your password has been changed.</h2>"+
            "<p>The password for the account <b>"+email+"</b> has been updated.</p>"+
            "</html>", "TAB Password Updated");


            return REDIRECT_PROFILE;
        } else {
            model.addAttribute("invalidPassword", "");
            model.addAttribute("invalidCurrent", "");
            model.addAttribute("invalidConfirm", "");

            if (oldPassword == null || "".equals(oldPassword)) {
                model.addAttribute("invalidCurrent", "This field is required");
            }
            if ("".equals(newPassword1)) {
                model.addAttribute("invalidPassword", "This field is required");
            }
            if (newPassword2 == null || "".equals(newPassword2)) {
                model.addAttribute("invalidConfirm", "This field is required");
            }

            if ("noMatch".equals(update)) {
                model.addAttribute("invalidPassword", "New password and confirm password do not match.");
                model.addAttribute("invalidConfirm", "New password and confirm password do not match.");
            }
            if ("invalid".equals(update) && !"".equals(newPassword1)) {
                model.addAttribute("invalidPassword", "Password must be at least 8 characters, contain a number and a special character, and cannot be equal to any of your other details.");
            }
            if ("incorrect".equals(update)) {
                model.addAttribute("invalidCurrent", "Old password is not correct.");
            }
            return "userPasswordUpdate";
        }
    }

    /**
     * Attaches the appropriate location error message to the given model
     * @param model       the given model
     * @param address1    street address 1 of the user's location
     * @param address2    street address 1 of the user's location
     * @param suburb      the suburb of the user's location
     * @param postcode    the user's postcode
     * @param city        the user's city
     * @param country     the user's country
     */
    private void setLocationError(Model model,
                                  String address1,
                                  String address2,
                                  String suburb, String postcode,
                                  String city,
                                  String country) {
        switch (RegistrationChecker.locationInvalidPartNum(address1, address2, suburb, postcode, city, country)) {
            case 1 ->
                    model.addAttribute(INVALID_ADDRESS1, "Please enter an address containing at least one digit alongside any letters. Hyphens and apostrophes are also acceptable.");
            case 2 ->
                    model.addAttribute(INVALID_ADDRESS2, "Please enter an address 2 containing at least one digit alongside any letters. Hyphens and apostrophes are also acceptable.");
            case 3 ->
                    model.addAttribute(INVALID_SUBURB, "Please enter a suburb which contains only letters.");
            case 4 ->
                    model.addAttribute(INVALID_POSTCODE, "Please enter a postcode which only contains numbers, letters or hyphens.");
            case 5 ->
                    model.addAttribute(INVALID_CITY, "Please enter a city containing only letters and hyphens.");
            case 6 ->
                    model.addAttribute(INVALID_COUNTRY, "Please enter a country containing only letters and hyphens.");
        }
    }

    /**
     * updates the logged in users privacy settings.
     * @param type the new PrivacyType of the user
     * */
    @PostMapping("/user/updatePrivacy")
    public String setUserPrivacy(@RequestParam(value="type") PrivacyType type) {
        logger.info("user privacy updated");
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getUserByEmail(username);
        if (user == null) {
            return REDIRECT_HOME;
        }
        user.setPrivacyType(type);
        userService.save(user);
        return REDIRECT_PROFILE;
    }


    /**
     * makes the currently logged in user follow the specified user.
     * @param userId the id of the user being followed
     * @param request used to get the
     * */
    @PostMapping("/follow")
    public String follow(@RequestParam(value="id") Long userId, HttpServletRequest request) {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getUserByEmail(email);
        Optional<User> other = userService.getUser(userId);
        if (user == null || other.isEmpty()) {
            return REDIRECT_HOME;
        }
        user.follow(other.get());
        userService.save(user);

        Optional<String> old = Optional.ofNullable(request.getHeader("Referer")).map(requestUrl -> REDIRECT + requestUrl);
        return old.orElse(REDIRECT_HOME);
    }

    @PostMapping("/unfollow")
    public String unfollow(@RequestParam(value="id") Long userId, HttpServletRequest request) {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getUserByEmail(email);
        Optional<User> other = userService.getUser(userId);




        if (user == null || other.isEmpty()) {
            logger.info("EMPTY");

            return REDIRECT_HOME;
        }
        if (!user.isFollowing(other.get())) {
            logger.info("NOT FOLLOW");
            return REDIRECT_HOME;
        }

        user.unfollow(other.get());
        userService.save(user);

        Optional<String> old = Optional.ofNullable(request.getHeader("Referer")).map(requestUrl -> REDIRECT + requestUrl);
        return old.orElse(REDIRECT_HOME);
    }


}

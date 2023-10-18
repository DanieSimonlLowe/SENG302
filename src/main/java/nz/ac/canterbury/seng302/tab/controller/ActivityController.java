package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletResponse;
import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.exceptions.NotFoundException;
import nz.ac.canterbury.seng302.tab.service.*;
import nz.ac.canterbury.seng302.tab.service.checkers.ActivityValidityChecker;
import nz.ac.canterbury.seng302.tab.service.checkers.RegistrationChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * controller for things related to activity's.
 */
@Controller
public class ActivityController {
    /** The logger */
    final Logger logger = LoggerFactory.getLogger(ActivityController.class);

    /** ID string constant */
    private static final String ID = "id";

    /** Display ID string constant */
    private static final String DISPLAY_ID = "displayId";

    /** Location string constant */
    private static final String LOCATION = "location";

    /** Postcode string constant */
    private static final String POSTCODE = "postcode";

    /** Suburb string constant */
    private static final String SUBURB = "suburb";

    /** Description string constant */
    private static final String DESCRIPTION = "description";

    /** Start string constant */
    private static final String START = "start";

    /** Team string constant */
    private static final String TEAM = "team";

    /** Activity Description string constant */
    private static final String ACTIVITY_DESCRIPTION = "actDesc";

    /** Activity Team string constant */
    private static final String ACTIVITY_TEAM = "actTeam";

    /** Activity Type string constant */
    private static final String ACTIVITY_TYPE = "actType";

    /** Activity Start string constant */
    private static final String ACTIVITY_START = "actStart";

    /** Activity End string constant */
    private static final String ACTIVITY_END = "actEnd";

    /** Players string constant */
    private static final String PLAYERS = "players";

    /** Formations string constant */
    private static final String FORMATIONS = "formations";

    /** Formation store string constant */
    private static final String FORMATION_STORE = "formationStore";

    /** Substitution string constant */
    private static final String SUBSTITUTIONS = "substitutions";

    /** End string constant */
    private static final String END = "end";

    /** Activity opposition string constant */
    private static final String ACTIVITY_OPPOSITION = "actOpposition";

    /** Address 1 string constant */
    private static final String ADDRESS_1 = "address1";

    /** Address 2 string constant */
    private static final String ADDRESS_2 = "address2";

    /** City string constant */
    private static final String CITY = "city";

    /** Country string constant */
    private static final String COUNTRY = "country";

    /** Invalid address 1 string constant */
    private static final String INVALID_ADDRESS_1 = "invalidAddress1";

    /** Invalid address 2 string constant */
    private static final String INVALID_ADDRESS_2 = "invalidAddress2";

    /** Invalid postcode string constant */
    private static final String INVALID_POSTCODE = "invalidPostcode";

    /** Invalid suburb string constant */
    private static final String INVALID_SUBURB = "invalidSuburb";

    /** Invalid city string constant */
    private static final String INVALID_CITY = "invalidCity";

    /** Invalid country string constant */
    private static final String INVALID_COUNTRY = "invalidCountry";

    /** Invalid lineup string constant */
    private static final String INVALID_LINEUP = "invalidLineup";

    /** Type error string constant */
    private static final String TYPE_ERROR = "typeError";

    /** Team error string constant */
    private static final String TEAM_ERROR = "teamError";

    /** Start time error string constant */
    private static final String START_TIME_ERROR = "startTimeError";

    /** End time error string constant */
    private static final String END_TIME_ERROR = "endTimeError";

    /** Description error string constant */
    private static final String DESCRIPTION_ERROR = "descError";

    /** Opposition error string constant */
    private static final String OPPOSITION_ERROR = "oppositionError";

    /** Activity Form string constant */
    private static final String ACTIVITY_FORM = "activityForm";

    /** The LocationService for database logic */
    private final LocationService locationService;

    /** The TeamService for database logic */
    private final TeamService teamService;

    /** The UserService for database logic */
    private final UserService userService;

    /** The ActivityService for database logic */
    private final ActivityService activityService;

    /** The FormationService for database logic*/
    private final FormationService formationService;

    /** The LineupService for database logic*/
    private final LineupService lineupService;

    /** The LineupPlayerService for database logic*/
    private final LineupPlayerService lineupPlayerService;

    /** The FeedPostService for database logic*/
    private final FeedPostService feedPostService;

    /**
     * Constructor for the ActivityController class
     * @param teamService         \the TeamService
     * @param userService         the UserService
     * @param activityService     the ActivityService
     * @param locationService     the LocationService
     * @param formationService    the FormationService
     * @param lineupService       the LineupService
     * @param lineupPlayerService the LineupPlayerService
     * @param feedPostService     the feedPostService
     */
    @Autowired
    public ActivityController(TeamService teamService, UserService userService, ActivityService activityService, LocationService locationService, FormationService formationService, LineupService lineupService, LineupPlayerService lineupPlayerService, FeedPostService feedPostService) {
        this.teamService = teamService;
        this.userService = userService;
        this.activityService = activityService;
        this.locationService = locationService;
        this.formationService = formationService;
        this.lineupService = lineupService;
        this.lineupPlayerService = lineupPlayerService;
        this.feedPostService = feedPostService;
    }

    /**
     * Gets a list of teams that the user manages
     * @return a list of teams that the user manages
     */
    @ModelAttribute("dropdownTeams")
    public List<Team> addDropdownTeams() {
        if (checkIsLoggedIn()) {
            User currentUser =  userService.getUserByEmail((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            return teamService.getTeamNamesWhoUserManageOrCoach(currentUser.getId());
        }
        return List.of();
    }

    /**
     * Gets a list of all teams
     * @return a list of all teams
     */
    @ModelAttribute("dropdownOpposition")
    public List<Team> addOppositionTeams() {
        if (checkIsLoggedIn()) {
            return teamService.getTeams();
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
        return !username.equals("anonymousUser") && !username.equals("anonymous");
    }

    /**
     * is run when a get request is made for the activityForm and returns the activityForm which is used to create activity's.
     * @param model stores information to be displayed on the page.
     * @return the page to be displayed.
     * */
    @GetMapping("/activityForm")
    public String activityForm(Model model) {
        logger.info("get activityForm");
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getUserByEmail(email);
        model.addAttribute("dropdownTeams",teamService.getTeamNamesWhoUserManageOrCoach(user.getId()));
        return ACTIVITY_FORM;
    }

    /**
     * is run when a post request is made for the activityForm.
     * it creates an activity described by the post request.
     * @param type the type of the activity to be made.
     * @param description the description of the activity to be made.
     * @param start the start date of the activity to be made.
     * @param end the end date of the activity to be made.
     * @param teamId the id of the team the activity is for.
     * @param oppositionId the id of the opposition team.
     * @param address1 the first address line of the activity.
     * @param address2 the second address line of the activity.
     * @param suburb the suburb of the activity.
     * @param postcode the postcode of the activity.
     * @param city the city of the activity.
     * @param country the country of the activity.
     * @param id the id of the activity to be edited.
     * @param players the players in the lineup.
     * @param formationId the id of the formation used in the lineup.
     * @param substitutions the players that were substituted.
     * @param model stores information to be displayed on the page.
     * @param response the response to be sent back to the user.
     * @return the page to be displayed.
     * */
    @PostMapping("/activityForm")
    public String activityPost(@RequestParam(name = ACTIVITY_TYPE) String type,
                               @RequestParam(name = ACTIVITY_DESCRIPTION) String description,
                               @RequestParam(name = ACTIVITY_START) String start,
                               @RequestParam(name = ACTIVITY_END) String end,
                               @RequestParam(name = ACTIVITY_TEAM) Long teamId,
                               @RequestParam(name = ACTIVITY_OPPOSITION) Long oppositionId,
                               @RequestParam(name = ADDRESS_1, defaultValue = "") String address1,
                               @RequestParam(name = ADDRESS_2, defaultValue = "") String address2,
                               @RequestParam(name = SUBURB, defaultValue = "") String suburb,
                               @RequestParam(name = POSTCODE, defaultValue = "") String postcode,
                               @RequestParam(name = CITY) String city,
                               @RequestParam(name = COUNTRY) String country,
                               @RequestParam(name = ID, required = false) Long id,
                               @RequestParam(name = PLAYERS, required = false) String players,
                               @RequestParam(name = FORMATION_STORE, defaultValue = "-1") Long formationId,
                               @RequestParam(name = SUBSTITUTIONS, required = false) String substitutions,
                               Model model,
                               HttpServletResponse response) {
        logger.info("POST activityForm");

        boolean isValid = true;
        if (type.equals("None")) {
            isValid = false;
            model.addAttribute(TYPE_ERROR, "An activity type is required");
        }
        if (description.isBlank()) {
            isValid = false;
            model.addAttribute(DESCRIPTION_ERROR, "An activity description is required");
        }
        if (start.isBlank()) {
            isValid = false;
            model.addAttribute(START_TIME_ERROR, "An activity start time is required");
        } else if (!start.matches("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}$")) {
            isValid = false;
            model.addAttribute(START_TIME_ERROR, "Invalid Date Format");
        }
        if (end.isBlank()) {
            isValid = false;
            model.addAttribute(END_TIME_ERROR, "An activity end time is required");
        } else if (!end.matches("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}$")) {
            isValid = false;
            model.addAttribute(END_TIME_ERROR, "Invalid Date Format");
        }
        if (address1.isBlank()) {
            isValid =  false;
            model.addAttribute(INVALID_ADDRESS_1, "Please enter an address 1 containing at least one digit alongside any letters. Hyphens and apostrophes are also acceptable.");
        }
        if (postcode.isBlank()) {
            isValid =  false;
            model.addAttribute(INVALID_POSTCODE, "Please enter a postcode which only contains numbers, letters or hyphens.");
        }
        if (city.isBlank()) {
            isValid = false;
            model.addAttribute(INVALID_CITY, "Please enter a city which can only contain letters or hyphens.");
        }
        if (country.isBlank()) {
            isValid = false;
            model.addAttribute(INVALID_COUNTRY, "Please enter a country which can only contain letters or hyphens.");
        }

        if (!isValid) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            addActivityModelAttributes(model, description, start, end, teamId, oppositionId, type, address1, address2, postcode, suburb, city, country, id);
            // Add the dropdown teams to the model if they aren't on there
            return ACTIVITY_FORM;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime startTimeParsed = LocalDateTime.parse(start, formatter);
        LocalDateTime endTimeParsed = LocalDateTime.parse(end, formatter);

        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getUserByEmail(email);

        Team team = null;

        if (teamId != 0) {
            try {
                team = teamService.getTeam(teamId).orElseThrow();
            } catch (NoSuchElementException e) {
                model.addAttribute(TEAM_ERROR, "Team does not exist.");
            }
        }

        Team opposition = null;
        if (oppositionId != 0) {
            try {
                opposition = teamService.getTeam(oppositionId).orElseThrow();
            } catch (NoSuchElementException e) {
                model.addAttribute(OPPOSITION_ERROR, "Team does not exist.");
            }
        }

        if (!ActivityValidityChecker.isValidType(type, team)) {
             isValid = false;
             model.addAttribute(TYPE_ERROR, "Activity types 'Game' and 'Friendly' require a team.");
         }
         if (!ActivityValidityChecker.isValidStartEnd(startTimeParsed, endTimeParsed)) {
             isValid = false;
             model.addAttribute(END_TIME_ERROR, "The end time of an activity cannot be before the start time.");
         }
         if (team != null && !ActivityValidityChecker.isValidTeamCreation(startTimeParsed, endTimeParsed, team)) {
             isValid = false;
             model.addAttribute(START_TIME_ERROR, "One of the teams was created after the given start or end time.");
         }
         if (!ActivityValidityChecker.isValidDescription(description)) {
             isValid = false;
             model.addAttribute(DESCRIPTION_ERROR, "Please enter a description made up of alphabetical characters and is no longer than 150 characters.");
         }
        if (!RegistrationChecker.isLocationValid(address1, address2, suburb, postcode, city, country)) {
            switch (RegistrationChecker.locationInvalidPartNum(address1, address2, suburb, postcode, city, country)) {
                case 1 -> model.addAttribute(INVALID_ADDRESS_1, "Please enter an address 1 containing at least one digit alongside any letters. Hyphens and apostrophes are also acceptable.");
                case 2 -> model.addAttribute(INVALID_ADDRESS_2, "Address 2 must contain at least one digit alongside any letters. Hyphens and apostrophes are also acceptable.");
                case 3 -> model.addAttribute(INVALID_SUBURB, "Suburb must contain only letters.");
                case 4 -> model.addAttribute(INVALID_POSTCODE, "Please enter a postcode which only contains numbers, letters or hyphens.");
                case 5 -> model.addAttribute(INVALID_CITY, "Please enter a city containing only letters and hyphens.");
                case 6 -> model.addAttribute(INVALID_COUNTRY, "Please enter a country containing only letters and hyphens.");
                default -> logger.error("Invalid location part number");
            }
            isValid = false;
        }
         if (!isValid) {
             response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
             addActivityModelAttributes(model, description, start, end, teamId, oppositionId, type, address1, address2, postcode, suburb, city, country, id);
             // Add the dropdown teams to the model if they aren't on there
             return ACTIVITY_FORM;
         }
        Activity activity;
        Location activityLocation;
        ActivityType actType = ActivityType.valueOf(type.toUpperCase());

         if (id == null) { // Creating a new activity
             activityLocation = new Location(address1, address2, suburb, postcode, city, country);
             activity = new Activity(actType,team,opposition,description,startTimeParsed,endTimeParsed, user, activityLocation);

         } else { // Editing an existing activity
             activity = activityService.getByIdCanEdit(id,user);
             activityLocation = activity.getLocation();
             activity.setTeam(team);
             activity.setOpposition(opposition);
             activity.setType(actType);
             activity.setDescription(description);
             activity.setStartDate(startTimeParsed);
             activity.setEndDate(endTimeParsed);

             // Update the location
             activityLocation.setAll(address1, address2, suburb, postcode, city, country);
         }

        if (formationId != -1) {
            String validLineup = checkLineup(players, substitutions, formationId);
            if (!validLineup.equals("True")) {
                model.addAttribute(INVALID_LINEUP, validLineup);
                addActivityModelAttributes(model, description, start, end, teamId, oppositionId, type, address1, address2, postcode, suburb, city, country, id);
                return ACTIVITY_FORM;
            }
            try {
                locationService.addLocation(activityLocation);
                activityService.save(activity);
                Optional <LineUp> lineup = lineupService.getLineupByActivityId(activity.getId());
                if (lineup.isPresent()) {
                    lineupService.removeLineupByActivityId(activity.getId());
                    lineupPlayerService.removeLineupPlayersByLineUpId(lineup.get().getId());
                }

                LineUp createdLineUp = new LineUp(formationService.getFormationById(formationId).orElseThrow(), activityService.getActivity(activity.getId()));
                lineupService.save(createdLineUp);

                String[] playerPositions = players.split(",");
                for (int i = 0; i < playerPositions.length; i++) {
                    User player = userService.getUser(Long.parseLong(playerPositions[i])).orElseThrow();
                    LineupPlayer lineupPlayer = new LineupPlayer(player, createdLineUp, i);
                    lineupPlayerService.save(lineupPlayer);
                }

                if (!substitutions.isEmpty()) {
                    String[] substitutionList = substitutions.split(",");
                    for (String s : substitutionList) {
                        User player = userService.getUser(Long.parseLong(s)).orElseThrow();
                        LineupPlayer lineupPlayer = new LineupPlayer(player, createdLineUp, -1);
                        lineupPlayerService.save(lineupPlayer);
                    }
                }
            } catch (NotFoundException e) {
                logger.error("Could not load activity.");
            } catch (NoSuchElementException e) {
                logger.error("Could not find formation.");
            }
        } else {
            locationService.addLocation(activityLocation);
            activityService.save(activity);
            lineupService.getLineupByActivityId(activity.getId()).ifPresent(lineup -> {
                lineupService.removeLineupByActivityId(activity.getId());
                lineupPlayerService.removeLineupPlayersByLineUpId(lineup.getId());
            });
        }

        FeedPost feedPost = createFeedPost(activity);
        feedPostService.save(feedPost);
        return "redirect:/viewActivities";
    }

    /**
     * Creates a feed post from an activity
     * @param activity the activity to create a feed post from
     * @return a feed post
     */
    public static FeedPost createFeedPost(Activity activity) {
        Long ownerId;
        OwnerType ownerType;
        String ownerName;
        User user = activity.getUser();
        if (activity.getTeam() != null) {
            ownerId = activity.getTeam().getId();
            ownerType = OwnerType.TEAM;
            ownerName = activity.getTeam().getName();
        } else {
            ownerId = activity.getUser().getId();
            ownerType = OwnerType.USER;
            ownerName = activity.getUser().getFirstName();
        }
        String postMessage = activity.getDescription() + "\n\n" + activity.getType() + " on " + activity.getStartTime().toString();
        String postTitle = user.getFirstName() + " created a new activity";
        return new FeedPost(ownerId, ownerType, ownerName, postTitle, postMessage, user, null);
    }

    /**
     * Gets the thymeleaf page representing the /viewActivities page - displays a user's activities
     * @param model          (map-like) representation of name, language and isJava boolean for use in thymeleaf
     * @return thymeleaf activityCalendar
     */
    @GetMapping("/viewActivities")
    public String viewMyActivities(Model model) {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getUserByEmail(email);

        List<Activity> myTeamActivities;
        List<Activity> myPersonalActivities;

        myTeamActivities = activityService.getMyTeamActivities(user.getId());
        myPersonalActivities = activityService.getMyPersonalActivities(user.getId());

        List<Activity> activities = Stream.concat(myPersonalActivities.stream(), myTeamActivities.stream()).toList();

        StringBuilder javascript = new StringBuilder();
        javascript.append('[');

        for (Activity activity: activities) {
            String colour;
            if (activity.getTeam() != null) {
                colour = activityService.generateHexColour(activity.getTeam().getId());
            } else {
                colour = "#DDDDDD";
            }
            String colourAct = activity.getJavaScriptEvent(Math.toIntExact(activity.getId())).replace("color: \"\"", String.format("backgroundColor: \"%s\"", colour));
            javascript.append(colourAct);
        }
        javascript.append("]");

        model.addAttribute("activitiesJavaScript",javascript.toString());
        model.addAttribute("activities",activities);

        List<Boolean> canEdit = activities.stream().map(activity -> activityService.getByIdCanEdit(activity.getId(), user) != null).toList();
        model.addAttribute("canEdit", canEdit);

        return "activityCalendar";
    }


    /**
     * Called when the user is trying to edit an activity.
     * @param model Holds the variables for the page. For this page its holding the activity that the user is trying
     *              to edit along with useful information about this activity
     * @param id    The id of the activity to edit.
     * @return A thymeleaf template. For this function, returns the activity form for editing.
     */
    @GetMapping("/editActivity")
    public String getEditActivityPage(Model model,
                                      @RequestParam(value=ID) Long id) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getUserByEmail(username);
        Activity activity = activityService.getByIdCanEdit(id, user);

        if (activity == null) {
            return "redirect:/viewActivities";
        }

        model.addAttribute(DISPLAY_ID,activity.getId());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm");

        model.addAttribute(ACTIVITY_TYPE,activity.getType().toString().toLowerCase());
        model.addAttribute(DESCRIPTION,activity.getDescription());
        model.addAttribute(START,activity.getStartDate().format(formatter));

        Team activityTeam = activity.getTeam();
        if (activityTeam == null) {
            model.addAttribute(ACTIVITY_TEAM,0);
        } else {
            model.addAttribute(ACTIVITY_TEAM,activity.getTeam().getId());
        }

        if (activity.getType().toString().equalsIgnoreCase("game") || activity.getType().toString().equalsIgnoreCase("friendly")) {

            Team team = activity.getTeam();
            long teamId = team.getId();

            Team opposition = activity.getOpposition();

            model.addAttribute(TEAM, team);

            if (opposition == null) {
                model.addAttribute(ACTIVITY_OPPOSITION, 0);
            } else {
                model.addAttribute(ACTIVITY_OPPOSITION, opposition.getId());
            }

            List<User> userList = userService.findUsersWhoAreInTeam(team);
            List<Formation> formationList = formationService.getFormationsByTeamId(teamId);

            model.addAttribute(PLAYERS, userList);
            model.addAttribute(FORMATIONS, formationList);

            // Add the current lineup to the model
            // Pass the id of the selected formation
            LineUp lineUp;
            Formation selectedFormation;
            long formationId;

            try {
                lineUp = lineupService.getLineupByActivityId(activity.getId()).orElseThrow();
                selectedFormation = lineUp.getFormation();
                formationId = selectedFormation.getId();
                model.addAttribute(FORMATION_STORE, formationId);
                logger.info("Adding formationStore: {}", formationId);
                List<Long> lineupUserIds = lineupPlayerService.getAllUserIdsByLineUpId(lineUp.getId());
                // Convert the list of longs to strings and join then with commas.
                String players = lineupUserIds.stream().map(userId -> Long.toString(userId)).collect(Collectors.joining(","));
                logger.info("Adding players: {}", players);
                model.addAttribute(PLAYERS, players);
            } catch(NoSuchElementException e) {
                logger.info("No lineup saved for activity");
            }

        }

        Location activityLocation = activity.getLocation();

        model.addAttribute(END,activity.getEndDate().format(formatter));
        model.addAttribute(LOCATION,activityLocation.toString());
        model.addAttribute(ADDRESS_1,activityLocation.getAddressOne());
        model.addAttribute(ADDRESS_2, activityLocation.getAddressTwo());
        model.addAttribute(SUBURB,activityLocation.getSuburb());
        model.addAttribute(POSTCODE,activityLocation.getPostcode());
        model.addAttribute(CITY,activityLocation.getCity());
        model.addAttribute(COUNTRY,activityLocation.getCountry());
        return ACTIVITY_FORM;
    }

    /**
     * Adds the given attributes to the model, so that they can be displayed when the page is redirected.
     * @param model       the model
     * @param description the activity's description
     * @param start       the time the activity starts
     * @param end         the time the activity ends
     * @param teamId      the id of the team the activity is for
     * @param oppositionId the id of the opposition team
     * @param type        the type of the activity
     * @param address1    the first address line of the activity
     * @param address2    the second address line of the activity
     * @param postcode    the postcode of the location of the activity
     * @param suburb      the suburb of the location of the activity
     * @param city        the city of the location of the activity
     * @param country     the country of the location of the activity
     * @param id          the activity's id
     */
    public void addActivityModelAttributes(Model model,
                                           String description,
                                           String start,
                                           String end,
                                           Long teamId,
                                           Long oppositionId,
                                           String type,
                                           String address1,
                                           String address2,
                                           String postcode,
                                           String suburb,
                                           String city,
                                           String country,
                                           Long id
                                           ) {
        model.addAttribute(DESCRIPTION, description);
        model.addAttribute(START, start);
        model.addAttribute(END, end);
        model.addAttribute(ACTIVITY_TEAM, teamId);
        model.addAttribute(ACTIVITY_OPPOSITION, oppositionId);
        model.addAttribute(ACTIVITY_TYPE, type);
        model.addAttribute(ADDRESS_1, address1);
        model.addAttribute(ADDRESS_2, address2);
        model.addAttribute(POSTCODE, postcode);
        model.addAttribute(SUBURB, suburb);
        model.addAttribute(CITY, city);
        model.addAttribute(COUNTRY, country);
        model.addAttribute(DISPLAY_ID, id);
    }

    /**
     * Checks if the lineup is valid.
     * @param players the players in the lineup.
     * @param substitutions the players that were substituted.
     * @param formationId the id of the formation used in the lineup.
     * @return a string describing the error if there is one, otherwise returns "True".
     */
    public String checkLineup(String players, String substitutions, Long formationId) {
        List<String> playerIdList =  (players.isBlank()) ? new ArrayList<>() : new ArrayList<>(List.of(players.split(",")));
        Set<String> playerIdSet = new HashSet<>(playerIdList);
        List<String> substitutionsIdList = (substitutions.isBlank()) ? new ArrayList<>() : List.of(substitutions.split(","));
        Set<String> substitutionsIdSet = new HashSet<>(substitutionsIdList);

        List<String> allPlayersList = Stream.concat(playerIdList.stream(), substitutionsIdList.stream()).toList();
        Set<String> allPlayersSet = Stream.concat(playerIdSet.stream(), substitutionsIdSet.stream()).collect(Collectors.toSet());
        try {
            Formation formationUsed = formationService.getFormationById(formationId).orElseThrow();
            if (playerIdSet.contains("-1") || (playerIdList.size() < Stream.of(formationUsed.getPlayersPerSectionString().split("-")).mapToInt(Integer::parseInt).sum())) {
                logger.info("Lineup could not be saved as not all player slots have been filled.");
                return "Lineup could not be saved as not all player slots have been filled.";
            }
            if (allPlayersSet.size() < allPlayersList.size()){
                logger.info("Lineup could not be saved a player cannot be in more than one position.");
                return "Lineup could not be saved a player cannot be in more than one position.";
            }
        } catch (NoSuchElementException e) {
            logger.info("Could not load formation.");
            return "Could not load formation.";
        }
        logger.info("Valid formation.");
        return "True";
    }
}

package nz.ac.canterbury.seng302.tab.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.LineUp;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.Formation;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.entity.json.*;
import nz.ac.canterbury.seng302.tab.entity.stats.Fact;
import nz.ac.canterbury.seng302.tab.entity.stats.GameScore;
import nz.ac.canterbury.seng302.tab.exceptions.InvalidSubstitutionException;
import nz.ac.canterbury.seng302.tab.exceptions.NotFoundException;
import nz.ac.canterbury.seng302.tab.service.*;
import nz.ac.canterbury.seng302.tab.entity.stats.IndividualScore;
import nz.ac.canterbury.seng302.tab.entity.stats.Substituted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@RestController
public class ActivityRestController {

    /** The logger */
    final Logger logger = LoggerFactory.getLogger(ActivityRestController.class);

    /** The ActivityService for database logic */
    private final ActivityService activityService;

    /** The ActivityStatService for database logic */
    private final ActivityStatService activityStatService;

    /** The TeamService for database logic */
    private final TeamService teamService;

    /** The LineupService for database logic */
    private final LineupService lineupService;

    /** The TeamMemberService for database logic */
    private final TeamMemberService teamMemberService;

    /** The UserService for database logic */
    private final UserService userService;

    /** Invalid request string constant */
    private static final String INVALID_REQUEST = "Invalid request.";

    /** JSON string message string constant */
    private static final String JSON_MESSAGE = "Sending json string {}";

    /**
     * Constructor for the activity rest controller
     * @param activityService the activity service
     * @param teamService the team service
     * @param lineupService the lineup service
     * @param activityStatService the activity stat service
     * @param teamMemberService the team member service
     * @param userService the user service
     */
    @Autowired
    public ActivityRestController(ActivityService activityService,
                                  TeamService teamService,
                                  LineupService lineupService,
                                  ActivityStatService activityStatService,
                                  TeamMemberService teamMemberService,
                                  UserService userService){
        this.activityService = activityService;
        this.teamService = teamService;
        this.lineupService = lineupService;
        this.activityStatService = activityStatService;
        this.teamMemberService = teamMemberService;
        this.userService = userService;
    }

    /**
     * Adds a substitution to the database
     * @param body the request body containing the necessary details for substitution
     * @return a response entity containing the status of the substitution addition
     */
    @PostMapping("/activities/addSubstitution")
    public ResponseEntity<String> addSubstitution(@RequestBody String body) {
        logger.info("POST /activities/addSubstitution");
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonResponse = mapper.readTree(body);
            Activity activity = activityService.getActivity(jsonResponse.get("activityId").asLong());
            int minute = jsonResponse.get("minute").asInt();
            LocalDateTime actStart = activity.getStartDate();
            LocalDateTime actEnd = activity.getEndDate();
            if (actStart.plusMinutes(minute).isAfter(actEnd)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Time is after activity ends.");
            }
            User playerOn = userService.getUser(jsonResponse.get("playerOn").asLong()).orElseThrow();
            User playerOff = userService.getUser(jsonResponse.get("playerOff").asLong()).orElseThrow();
            Team team = teamService.getTeam(jsonResponse.get("team").asLong()).orElseThrow();
            Substituted substituted = new Substituted(activity, playerOff, playerOn, minute, team);

            Optional<LineUp> lineUp = lineupService.getLineupByActivityId(activity.getId());
            if (lineUp.isPresent() && !lineUp.get().isValidSubstitution(substituted)) {
                logger.info("Invalid Substituted");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Substituted.");

            }

            activityStatService.save(substituted);
            return ResponseEntity.status(HttpStatus.OK).body("Created substitution.");
        } catch(Exception e) {
            logger.error(e.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(INVALID_REQUEST);
        }
    }

    /**
     * Adds a score to the database
     * @param body the request body containing the necessary details for score
     * @return a response entity containing the status of the score addition
     */
    @PostMapping("/activities/addScore")
    public ResponseEntity<String> addScore(@RequestBody String body) {
        logger.info("POST /activities/addScore");
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonResponse = mapper.readTree(body);
            Activity activity = activityService.getActivity(jsonResponse.get("activityId").asLong());
            Team team = teamService.getTeam(jsonResponse.get("team").asLong()).orElseThrow();
            User player = userService.getUser(jsonResponse.get("player").asLong()).orElseThrow();
            int minute = jsonResponse.get("minute").asInt();
            int points = jsonResponse.get("points").asInt();
            IndividualScore individualScore = new IndividualScore(activity, team, minute, player, points);
            activityStatService.save(individualScore);
            return ResponseEntity.status(HttpStatus.OK).body("Added score.");
        } catch(Exception e) {
            logger.error(e.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(INVALID_REQUEST);
        }
    }

    /**
     * Gets the lineup for the activity
     * @param activityId the id of the activity
     * @return a response entity containing the lineup and the data required to display it
     */
    @GetMapping("/activities/getLineup/{activityId}")
    public ResponseEntity<String> getLineup(@PathVariable Long activityId) {
        logger.info("GET /activities/getLineup");
        try {
            Activity activity = activityService.getActivity(activityId);
            LineUp activityLineup;
            Formation activityLineupFormation;
            Team activityTeam = activity.getTeam();

            logger.info("Activity ID: {}", activity.getId());

            Optional <LineUp> lineup = lineupService.getLineupByActivityId(activityId);
            if (lineup.isPresent()) {
                activityLineup = lineup.get();
            } else {
                // Lineup doesn't exist for activity
                logger.info("Lineup doesn't exist for activity");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(INVALID_REQUEST);
            }

            // apply the substations to the lineup
            activityLineup.applySubstitutions(activityStatService.getAllActivitySubstituted(activityId));

            // Get the default sport for the background view of the lineup.
            String pitchString = "rugby_field";
            if (activityTeam.getSport() != null) {
                String sport = activityTeam.getSport().toLowerCase();
                if (Arrays.asList("basketball","netball","volleyball").contains(sport)) {
                    pitchString = sport + "_court";
                } else if (Arrays.asList("baseball","rugby","softball").contains(sport)) {
                    pitchString = sport + "_field";
                } else if (Arrays.asList("football","hockey").contains(sport)) {
                    pitchString = sport + "_pitch";
                }
            }

            // Get the formation format
            activityLineupFormation = activityLineup.getFormation();

            String formationString = activityLineupFormation.getPlayersPerSectionString();


            // Get the players in lineup as user objects
            List<User> usersInLineup = activityLineup.getPlayersInOrder();

            // Convert them to response objects to send via JSON
            List<ResponsePlayerObject> playerObjects = usersInLineup.stream().map(
                    user -> new ResponsePlayerObject(user.getId(),
                    user.getFirstName(),
                    user.getProfilePicName())).toList();

            // Convert the list of longs to strings and join then with commas in the right order.
            String formationPlayerIds = usersInLineup.stream().map(u -> Long.toString(u.getId())).collect(Collectors.joining(","));

            LineupResponseObject response = new LineupResponseObject(
                    pitchString,
                    formationPlayerIds,
                    formationString,
                    playerObjects
            );

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(response);
            return ResponseEntity.status(HttpStatus.OK).body(jsonString);

        } catch (NotFoundException | InvalidSubstitutionException | JsonProcessingException e) {
            logger.error(e.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(INVALID_REQUEST);
        }
    }

    /**
     * Gets all the players in a team
     * @param teamId the id of the team
     * @return a response entity containing the players in the team
     */
    @GetMapping("/activities/getPlayers/{teamId}")
    public ResponseEntity<String> getPlayers(@PathVariable Long teamId) {
        logger.info("GET /activities/getPlayers");
        try {
            Team team = teamService.getTeam(teamId).orElseThrow();
            // Gets all the players in the team
            List<User> players = teamMemberService.getAllTeamMembersFromTeam(team)
                    .stream()
                    .map(player -> userService.getUser(
                                    player.getTeamMemberId()
                                            .getUser()
                                            .getId())
                            .orElseThrow())
                    .toList();
            // Convert the list of user's to player response objects
            List<ResponsePlayerObject> responsePlayers = players
                    .stream()
                    .map(player -> new ResponsePlayerObject(player.getId(),
                            player.getFirstName(),
                            player.getProfilePicName()))
                    .toList();

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(responsePlayers);
            logger.info(JSON_MESSAGE, jsonString);
            return ResponseEntity.status(HttpStatus.OK).body(jsonString);
        } catch (Exception e) {
            logger.error(e.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(INVALID_REQUEST);
        }
    }

    /**
     * Gets all the substitutions in an activity
     * @param activityId the id of the activity
     * @return a response entity containing the substitutions in the activity
     */
    @GetMapping("/activities/getSubstitutions/{activityId}")
    public ResponseEntity<String> getSubstitutions(@PathVariable Long activityId) {
        logger.info("GET /activities/getSubstitutions");
        try {
            List<Substituted> substitutions = activityStatService.getAllActivitySubstituted(activityId);
            substitutions.sort(Comparator.comparing(Substituted::getSubMinute));
            List<ResponseSubstitutionObject> responseSubstitutionObjects = substitutions.stream().map(substituted -> new ResponseSubstitutionObject(
                    substituted.getNewPlayer().getId(),
                    (userService.getUser(substituted.getNewPlayer().getId())).orElseThrow().getProfilePicName(),
                    (userService.getUser(substituted.getNewPlayer().getId())).orElseThrow().getFirstName(),
                    substituted.getOldPlayer().getId(),
                    (userService.getUser(substituted.getOldPlayer().getId())).orElseThrow().getProfilePicName(),
                    (userService.getUser(substituted.getOldPlayer().getId())).orElseThrow().getFirstName(),
                    substituted.getSubMinute(),
                    substituted.getTeam().getId(),
                    (teamService.getTeam(substituted.getTeam().getId())).orElseThrow().getProfilePicName(),
                    substituted.getId()
                    )).toList();

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(responseSubstitutionObjects);
            logger.info("Sending json string substitution {}", jsonString);
            return ResponseEntity.status(HttpStatus.OK).body(jsonString);

        } catch (Exception e) {
            logger.error(e.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(INVALID_REQUEST);
        }
    }

    /**
     * Gets all the scores in an activity
     * @param activityId the id of the activity
     * @return a response entity containing the scores in the activity
     */
    @GetMapping("/activities/getScores/{activityId}")
    public ResponseEntity<String> getScores(@PathVariable Long activityId) {
        logger.info("GET /activities/getScores");
        try {
            List<IndividualScore> scores = activityStatService.getAllActivityIndividualScores(activityId);
            List<ResponseScoreObject> responseScoreObjects = scores.stream().map(score -> new ResponseScoreObject(
                    score.getTeam().getProfilePicName(),
                    score.getPlayer().getId(),
                    score.getPlayer().getFirstName(),
                    score.getScoreMinute(),
                    score.getPoints()
            )).toList();

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(responseScoreObjects);
            logger.info(JSON_MESSAGE, jsonString);
            return ResponseEntity.status(HttpStatus.OK).body(jsonString);
        } catch (Exception e) {
            logger.error(e.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(INVALID_REQUEST);
        }
    }


    /**
     * Adds a fact to an activity
     * @param body the body of the request containing everything needed to create a fact
     * @return a response entity containing the status of the request
     */
    @PostMapping("/activities/addFact")
    public ResponseEntity<String> addFact(@RequestBody String body) {
        logger.info("POST /activities/addFact");
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonResponse = mapper.readTree(body);
            String description = jsonResponse.get("description").asText();
            String time = jsonResponse.get("time").asText();
            long actId = Long.parseLong(jsonResponse.get("actId").asText());
            Activity activity = activityService.getActivity(actId);
            long minutes  = Long.parseLong(time);
            LocalDateTime activityStart = activity.getStartDate();
            LocalDateTime activityEnd = activity.getEndDate();
            LocalDateTime factStart = activityStart.plusMinutes(minutes);

            if (factStart.isAfter(activityEnd)) {
                logger.error("Fact is after activity ends ");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Fact is after activity ends.");
            }

            Fact fact = new Fact(activity, description, time);
            activityStatService.save(fact);

        } catch(Exception e) {
            logger.info(e.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(INVALID_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.OK).body("Added fact.");
    }

    /**
     * Adds a fact to an activity
     * @param activityId the id of the activity
     * @return a response entity containing the facts in the activity
     */
    @GetMapping("/activities/getFacts/{activityId}")
    public ResponseEntity<String> getFacts(@PathVariable long activityId) {
        logger.info("GET /activities/getFacts");
        List<Fact> facts;
        String factsJsonStr;
        try {
            facts = activityStatService.getAllActivityFacts(activityId);

            StringBuilder factsJson = new StringBuilder("{\"data\": [");
            for (Fact fact : facts) {
                factsJson.append("{\"factId\":\"").append(fact.getId()).append("\", \"desc\": \"").append(fact.getDescription()).append("\", \"time\": \"").append(fact.getTime()).append("\"},");
            }
            factsJson.deleteCharAt(factsJson.length()-1);
            factsJson.append("]}");
            factsJsonStr = factsJson.toString();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(INVALID_REQUEST);
        }
        logger.info(factsJsonStr);
        return ResponseEntity.status(HttpStatus.OK).body(factsJsonStr);
    }

    /**
     * Deletes a fact from an activity
     * @param factId the id of the fact
     * @return a response entity containing the status of the request
     */
    @DeleteMapping("/activities/deleteFact/{factId}")
    public ResponseEntity<String> deleteFact(@PathVariable long factId) {
        logger.info("DELETE /activities/deleteFact");
        try {
            activityStatService.deleteFact(factId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(INVALID_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.OK).body("Fact deleted.");
    }

    /**
     * Gets the activity details from a given activity id
     * @param activityId the id of the activity
     * @return a response entity containing the activity details
     */
    @GetMapping("/activities/getActivityDetails/{activityId}")
    public ResponseEntity<String> getActivityDetails(@PathVariable long activityId) {
        logger.info("GET /activities/getActivityDetails");
        try {
            Activity activity = activityService.getActivity(activityId);
            Team team = activity.getTeam();
            Team opposition = activity.getOpposition();

            Long teamId = null;
            String teamName = null;
            String teamProfilePicName = null;

            if (team != null) {
                teamId = team.getId();
                teamName = team.getName();
                teamProfilePicName = team.getProfilePicName();
            }

            Long oppositionId = null;
            String oppositionName = null;
            String oppositionProfilePicName = null;

            if (opposition != null) {
                oppositionId = opposition.getId();
                oppositionName = opposition.getName();
                oppositionProfilePicName = opposition.getProfilePicName();
            }

            ResponseActivityObject responseActivityObject = new ResponseActivityObject(activityId, activity.getType().toString(), teamId, teamName, teamProfilePicName, oppositionId, oppositionName, oppositionProfilePicName);
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(responseActivityObject);
            logger.info(JSON_MESSAGE, jsonString);
            return ResponseEntity.status(HttpStatus.OK).body(jsonString);
        } catch (Exception e) {
            logger.error(e.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(INVALID_REQUEST);
        }
    }


    /**
     * saves the team scores of an activity
     * @param body contains the data for the game score.
     * @param activityId the id of the activity that the game score is being added to
     * @return a response that says if the games core was saved successfully or not
     */
    @PostMapping("/activities/saveGameScore/{activityId}")
    public ResponseEntity<String> saveGameScores(@PathVariable long activityId, @RequestBody String body) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonResponse = mapper.readTree(body);
            String score1 = jsonResponse.get("score1").asText();
            String score2 = jsonResponse.get("score2").asText();

            Activity activity = activityService.getActivity(activityId);
            GameScore gameScore = new GameScore(activity,score1,score2);

            activityStatService.saveGameScore(gameScore);

            return ResponseEntity.status(HttpStatus.OK).body("Added Game Score.");
        } catch (Exception e) {
            logger.error(e.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(INVALID_REQUEST);
        }
    }

    /**
     * gets the game score of an activity
     * @param activityId the id of the activity that the game score is being got of.
     * @return a response entity containing the status of the request
     * */
    @GetMapping("/activities/getGameScore/{activityId}")
    public ResponseEntity<String> getGameScores(@PathVariable long activityId) {
        try {
            Optional<GameScore> gameScore = activityStatService.getActivityGameScores(activityId);
            ResponseGameScoreObject responseGameScoreObject;

            responseGameScoreObject = gameScore.map(
                    score -> new ResponseGameScoreObject(score.getScore1(), score.getScore2())
            ).orElseGet(
                    () -> new ResponseGameScoreObject("", "")
            );

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(responseGameScoreObject);
            return ResponseEntity.status(HttpStatus.OK).body(jsonString);
        } catch (Exception e) {
            logger.error(e.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(INVALID_REQUEST);
        }
    }
}

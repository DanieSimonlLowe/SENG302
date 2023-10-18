package nz.ac.canterbury.seng302.tab.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.entity.json.ResponseFollowingObject;
import nz.ac.canterbury.seng302.tab.entity.json.ResponseFollowingTeamsObject;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class UserRestController {

    /** The logger */
    final Logger logger = LoggerFactory.getLogger(UserRestController.class);

    /** The UserService for database logic */
    private final UserService userService;

    /** The TeamService for database logic */
    private final TeamService teamService;

    /**
     * Constructor for the user rest controller
     * @param teamService the team service
     * @param userService the user service
     */
    @Autowired
    public UserRestController(UserService userService, TeamService teamService) {
        this.userService = userService;
        this.teamService = teamService;
    }

    /**
     * If the user is following the given user, then they will unfollow
     */
    @PostMapping("/users/unfollow")
    public ResponseEntity<String> unfollow(
            @RequestBody String body) throws JsonProcessingException {


        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonResponse = mapper.readTree(body);

        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getUserByEmail(email);
        Optional<User> other = Optional.empty();


        List<User> fNom = userService.getUsersByFirstNameContains(jsonResponse.get("fName").asText());
        List<User> lNom = userService.getUsersByLastNameContains(jsonResponse.get("lName").asText());

        for (User value : fNom) {
            for (User item : lNom) {
                if (value == item) {
                    other = Optional.ofNullable(value);
                    break;
                }
            }
            if (!other.isEmpty()) {
                break;
            }
        }

        if (user == null|| other.isEmpty() ) {
            logger.info("EMPTY");

            return ResponseEntity.badRequest().body("Error getting following: EMPTY USER");
        }
        if (!user.isFollowing(other.get())) {
            logger.info("NOT FOLLOW");
            return ResponseEntity.badRequest().body("Error getting following: USER NOT FOLLOWED");
        }

        user.unfollow(other.get());
        userService.save(user);

        return ResponseEntity.status(HttpStatus.OK).body("OK");
    }


    /**
     * Gets the list of users that the user follows to be displayed on their profile page
     * @return a ResponseEntity containing the list of users as json
     */
    @GetMapping("/users/getFollowing")
    public ResponseEntity<String> getFollowing() {
        // Get logged in user
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            if (email == null) {
                return ResponseEntity.badRequest().body("No user logged in");
            } else {
                User loggedInUser = userService.getUserByEmail(email);
                List<User> following = userService.findFollowedUsersByUserId(loggedInUser.getId());
                List<ResponseFollowingObject> response = following.stream().map(user -> new ResponseFollowingObject(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), userService.areFriends(loggedInUser, user) ,user.getProfilePicName())).toList();
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonString = objectMapper.writeValueAsString(response);
                return ResponseEntity.status(HttpStatus.OK).body(jsonString);
            }
        } catch (Exception e) {
            logger.error("Error getting following: {}", e.getMessage());
            logger.error(String.format("Error getting following: %s", e.getMessage()));
            return ResponseEntity.badRequest().body("Error getting following: " + e.getMessage());
        }
    }


    /**
     * Gets the list of teams that the user follows to be displayed on their profile page
     * @return a ResponseEntity containing the list of teams as json
     */
    @GetMapping("/users/getFollowingTeams")
    public ResponseEntity<String> getFollowingTeams() {
        // Get logged in user
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            if (email == null) {
                return ResponseEntity.badRequest().body("No user logged in");
            } else {
                User loggedInUser = userService.getUserByEmail(email);
                List<Team> following = teamService.getFollowedTeams(loggedInUser);
                List<ResponseFollowingTeamsObject> response = following.stream().map(team -> new ResponseFollowingTeamsObject(team.getId(), team.getName(), team.getProfilePicName())).toList();
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonString = objectMapper.writeValueAsString(response);
                return ResponseEntity.status(HttpStatus.OK).body(jsonString);
            }
        } catch (Exception e) {
            logger.error("Error getting followed teams: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error getting followed teams: " + e.getMessage());
        }
    }
}

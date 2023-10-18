package nz.ac.canterbury.seng302.tab.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.tab.entity.Comment;
import nz.ac.canterbury.seng302.tab.entity.FeedPost;
import nz.ac.canterbury.seng302.tab.entity.ModerationResult;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.APIRateLimiter;
import nz.ac.canterbury.seng302.tab.service.ModerationAPI;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * This is a rest controller handling requests with the moderation API key, note the @link{RestController} annotation which defines this.
 * This controller defines endpoints as functions with specific HTTP mappings
 */
@Controller
public class ModerationAPIController {
    /** The logger */
    final Logger logger = LoggerFactory.getLogger(ModerationAPIController.class);

    /** The rate limiter for the API */
    final APIRateLimiter apiRateLimiter;

    /** The user service */
    final UserService userService;

    /** Api key */
    @Value("${moderation.api.key}")
    private String apiKey;

    /**
     * Constructor for the controller
     * @param userService The user service
     * @param apiRateLimiter The rate limiter
     */
    @Autowired
    public ModerationAPIController(UserService userService,
                                   APIRateLimiter apiRateLimiter) {
        this.userService = userService;
        this.apiRateLimiter = apiRateLimiter;
    }

    /**
     * Moderates a feed post
     * @param feedPost The feed post to moderate
     * @return The moderated feed post
     */
    public FeedPost moderateFeedPost(FeedPost feedPost) {
        logger.info("Moderating feed post: {}", feedPost.getId());
        try {
            // Request moderation
            String response = Objects.requireNonNull(requestModeration(feedPost.getTitle() + " " + feedPost.getMessage()).getBody());
            // Parse response, removing the outer braces
            ModerationResult result = parseModerationResponse(response);
            feedPost.setFlagged(result.isFlagged());
            feedPost.setFlaggedReason(result.getFlaggedReason());
        } catch (JsonProcessingException | ParseException e) {
            logger.warn("Failed to moderate feed post: {} {}", feedPost.getId(), e.getMessage());
        }
        return feedPost;
    }

    /**
     * Moderates a comment
     * @param comment The comment to moderate
     * @return The moderated comment
     */
    public Comment moderateComment(Comment comment) {
        logger.info("Moderating comment: {}", comment.getId());
        try {
            // Request moderation
            String response = Objects.requireNonNull(requestModeration(comment.getMessage()).getBody());
            // Parse response
           ModerationResult result = parseModerationResponse(response);
           comment.setFlagged(result.isFlagged());
           comment.setFlaggedReason(result.getFlaggedReason());
        } catch (JsonProcessingException | ParseException e) {
            logger.warn("Failed to moderate comment: {} {}", comment.getId(), e.getMessage());
        }
        return comment;
    }

    /**
     * Parses the response from the moderation API
     * @param response The response to parse
     * @return The parsed response
     * @throws ParseException If the response cannot be parsed
     */
    public ModerationResult parseModerationResponse(String response) throws ParseException {
        ModerationResult result = new ModerationResult(false, null);
        // Parse response, removing the outer braces
        String jsonString = response.substring(1, response.length() - 1);
        JSONParser parser = new JSONParser(jsonString);
        LinkedHashMap<String, Object> data = parser.parseObject();
        result.setFlagged((Boolean) data.get("flagged"));

        String input = data.get("categories").toString();
        // Remove curly braces and split the string by commas
        String[] keyValuePairs = input.substring(1, input.length() - 1).split(", ");
        StringBuilder reasonsBuilder = new StringBuilder();

        for (String pair : keyValuePairs) {
            // Split each key-value pair by '='
            String[] parts = pair.split("=");
            if (parts.length == 2) {
                String key = parts[0];
                boolean value = Boolean.parseBoolean(parts[1]);
                if (value) {
                    reasonsBuilder.append(key).append(", ");
                }
            }
        }
        if (!reasonsBuilder.isEmpty()) { result.setFlaggedReason(reasonsBuilder.toString()); }
        return result;
    }


    /**
     * Makes a request to <a href="https://api.openai.com/v1/moderations">...</a> and returns
     * the JSON it responds with.
     * @param text The text to be moderated
     * @throws JsonProcessingException If the JSON cannot be processed
     * @return A ResponseEntity containing the JSON
     */
    public ResponseEntity<String> requestModeration(String text) throws JsonProcessingException {
        logger.info("GET /moderation-request");
        // Check if the request is allowed
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getUserByEmail(email);

        if (user == null) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found."); }

        if (!apiRateLimiter.allowRequest(user.getId())) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Rate limit exceeded. Try again later.");
        } else {
            ModerationAPI moderationAPI = new ModerationAPI(apiKey);
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(moderationAPI.getResult("{\"input\":\"" + text + "\"}"));
            return ResponseEntity.status(HttpStatus.OK).body(jsonString);
        }
    }
}

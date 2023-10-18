package nz.ac.canterbury.seng302.tab.controller;
import nz.ac.canterbury.seng302.tab.entity.FeedAlerts;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.tab.entity.FeedPost;
import nz.ac.canterbury.seng302.tab.entity.json.ResponseAlertObject;
import nz.ac.canterbury.seng302.tab.entity.json.ResponseAlertNumberObject;
import nz.ac.canterbury.seng302.tab.service.FeedPostService;
import nz.ac.canterbury.seng302.tab.service.UserAlertsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.ZoneOffset;
import java.util.List;

@RestController
public class AlertRestController {

    /** The feed post service */
    private final FeedPostService feedPostService;
    /** The user alerts service */
    private final UserAlertsService userAlertsService;
    /** The logger */
    private final Logger logger = LoggerFactory.getLogger(AlertRestController.class);

    /**
     * Creates a new alert rest controller
     * @param feedPostService The feed post service
     * @param userAlertsService The user alerts service
     */
    @Autowired
    AlertRestController(FeedPostService feedPostService,
                        UserAlertsService userAlertsService) {
        this.feedPostService = feedPostService;
        this.userAlertsService = userAlertsService;
    }

    /**
     * Gets the alerts for a user
     * @param userId The id of the user
     * @return The alerts for the user
     */
    @GetMapping("/userAlerts/{userId}")
    public ResponseEntity<String> getUserAlerts(@PathVariable Long userId) {
        try {
            List<FeedPost> feedPostList = feedPostService.getPersonalFeedPosts(userId);

            List<ResponseAlertObject> responseAlertObjects = feedPostList.stream()
                    .map( post -> new ResponseAlertObject(post.getOwnerName(), post.getMessage(), post.getAttachmentName(),post.getAttachmentType(), post.getDateTimeString()))
                    .toList();

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(responseAlertObjects);
            return ResponseEntity.status(HttpStatus.OK).body(jsonString);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.toString());
        }
    }


    /**
     * Gets the number of read alerts for a user
     * @param userId The id of the user
     * @return The number of read alerts
     */
    @GetMapping("/userAlertNumber/{userId}")
    public ResponseEntity<String> getUserAlertNumber(@PathVariable Long userId) {
        try {
            FeedAlerts feedPostNumber = userAlertsService.getUserAlertsById(userId).orElseThrow();
            ResponseAlertNumberObject responseAlertNumberObject = new ResponseAlertNumberObject(feedPostNumber.getReadAlerts());

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(responseAlertNumberObject);
            return ResponseEntity.status(HttpStatus.OK).body(jsonString);
        } catch (Exception e) {
            logger.error("Error getting alerts for user: {}", userId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.toString());
        }
    }

    /**
     * Updates the number of read alerts for a user
     * @param userId The id of the user
     * @param body The body of the request
     * @return A response entity
     */
    @PostMapping("/userAlertNumber/{userId}/updateReadPosts")
    public ResponseEntity<String> updateReadPosts(@PathVariable Long userId,
                                                  @RequestBody String body) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonResponse = mapper.readTree(body);
            Long readAlerts = jsonResponse.get("readAlerts").asLong();

            FeedAlerts feedAlerts = userAlertsService.getUserAlertsById(userId).orElseThrow();
            feedAlerts.setReadAlerts(readAlerts);
            userAlertsService.save(feedAlerts);

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString("Success");

            return ResponseEntity.status(HttpStatus.OK).body(jsonString);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.toString());
        }
    }
}

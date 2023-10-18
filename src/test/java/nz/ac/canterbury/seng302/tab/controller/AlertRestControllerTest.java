package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.FeedAlerts;
import nz.ac.canterbury.seng302.tab.entity.FeedPost;
import nz.ac.canterbury.seng302.tab.service.FeedPostService;
import nz.ac.canterbury.seng302.tab.service.UserAlertsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
public class AlertRestControllerTest {

    /**
     * The mock feedPostService.
     */
    @Mock
    private FeedPostService feedPostService;

    /**
     * The mock userAlertsService.
     */
    @Mock
    private UserAlertsService userAlertsService;

    /**
     * The alertRestController.
     */
    @InjectMocks
    private AlertRestController alertRestController;

    /**
     * The mockMvc.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * The feedAlerts.
     */
    @BeforeEach
    void setup() {
        feedPostService = Mockito.mock(FeedPostService.class);
        userAlertsService = Mockito.mock(UserAlertsService.class);

        alertRestController = new AlertRestController(feedPostService, userAlertsService);
    }


    /**
     * Tests that the getAlerts method returns a 200 status code.
     */
    private FeedPost makeMockFeedPost(String ownerName, String message, String attachment, String attachmentType, LocalDateTime time) {
        FeedPost feedPost = Mockito.mock(FeedPost.class);
        Mockito.when(feedPost.getOwnerName()).thenReturn(ownerName);
        Mockito.when(feedPost.getMessage()).thenReturn(message);
        Mockito.when(feedPost.getAttachmentName()).thenReturn(attachment);
        Mockito.when(feedPost.getAttachmentType()).thenReturn(attachmentType);
        Mockito.when(feedPost.getDateTime()).thenReturn(time);
        return feedPost;
    }

    /**
     * Tests that the getAlerts method returns the correct Json.
     */
    @Test
    void getUserAlertNumber() {
        FeedAlerts feedPostNum = Mockito.mock(FeedAlerts.class);

        Mockito.when(userAlertsService.getUserAlertsById(Mockito.anyLong())).thenReturn(Optional.of(feedPostNum));
        Mockito.when(feedPostNum.getReadAlerts()).thenReturn(2L);
        ResponseEntity<String> out = alertRestController.getUserAlertNumber(2L);

        String expectedJson = "{\"readAlerts\":2}";

        Assertions.assertEquals(expectedJson, out.getBody());
    }

    /**
     * Tests that the updateAlerts works and returns the correct Json.
     */
    @Test
    void postUpdateReadPosts() {
        FeedAlerts feedPostNum = Mockito.mock(FeedAlerts.class);
        String requestBody = "{\"readAlerts\":2}";
        String expectedResponse = "\"Success\"";

        Mockito.when(userAlertsService.getUserAlertsById(Mockito.anyLong())).thenReturn(Optional.of(feedPostNum));

        ResponseEntity<String> out = alertRestController.updateReadPosts(2L, requestBody);

        Assertions.assertEquals(expectedResponse, out.getBody());
    }
}

package nz.ac.canterbury.seng302.tab.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import nz.ac.canterbury.seng302.tab.entity.Comment;
import nz.ac.canterbury.seng302.tab.entity.FeedPost;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.APIRateLimiter;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootTest
@AutoConfigureMockMvc
class ModerationAPIControllerTest {

    private ModerationAPIController moderationAPIController;

    private Authentication authentication;

    private APIRateLimiter apiRateLimiter;

    private UserService userService;

    @BeforeEach
    void setUp() {
        SecurityContext context = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(context);
        this.authentication = Mockito.mock(Authentication.class);
        Mockito.when(context.getAuthentication()).thenReturn(authentication);

        this.userService = Mockito.mock(UserService.class);
        this.apiRateLimiter = Mockito.mock(APIRateLimiter.class);
        this.moderationAPIController = Mockito.spy(new ModerationAPIController(userService, apiRateLimiter));
    }

    @Test
    void testModerateFeedPost() throws JsonProcessingException {
        FeedPost feedPost = Mockito.mock(FeedPost.class);
        Mockito.when(feedPost.getTitle()).thenReturn("Test Title");
        Mockito.when(feedPost.getMessage()).thenReturn("Test Message");

        ResponseEntity responseEntity = Mockito.mock(ResponseEntity.class);
        Mockito.when(responseEntity.getBody()).thenReturn("[{\"flagged\":false," +
                "\"categories\":{\"sexual\":false,\"hate\":false,\"harassment\":false,\"self-harm\":false,\"sexual/minors\":false,\"hate/threatening\":false,\"violence/graphic\":false,\"self-harm/intent\":false,\"self-harm/instructions\":false,\"harassment/threatening\":false,\"violence\":false}," +
                "\"category_scores\":{\"sexual\":0.0012596443993970752,\"hate\":7.599094533361495E-5,\"harassment\":9.230645810021088E-5,\"self-harm\":4.361209357739426E-5,\"sexual/minors\":2.925005219367449E-6,\"hate/threatening\":1.4075258150114678E-6,\"violence/graphic\":2.081236380035989E-5,\"self-harm/intent\":2.729771949816495E-6,\"self-harm/instructions\":1.313146299253276E-6,\"harassment/threatening\":8.81807750374719E-7,\"violence\":5.2090032113483176E-5}}]");

        Mockito.when(moderationAPIController.requestModeration("Test Title Test Message")).thenReturn(responseEntity);
        moderationAPIController.moderateFeedPost(feedPost);
        Mockito.verify(feedPost, Mockito.times(1)).setFlagged(false);
        Mockito.verify(feedPost, Mockito.times(1)).setFlaggedReason(null);
    }

    @Test
    void testModerateFeedPosts_throwException() throws JsonProcessingException {
        FeedPost feedPost = Mockito.mock(FeedPost.class);
        Mockito.when(feedPost.getTitle()).thenReturn("Test Title");
        Mockito.when(feedPost.getMessage()).thenReturn("Test Message");

        Mockito.when(moderationAPIController.requestModeration("Test Title Test Message")).thenThrow(Mockito.mock(JsonProcessingException.class));
        moderationAPIController.moderateFeedPost(feedPost);
        Mockito.verify(feedPost, Mockito.times(0)).setFlagged(false);
        Mockito.verify(feedPost, Mockito.times(0)).setFlaggedReason(null);
    }

    @Test
    void testModerateComment() throws JsonProcessingException {
        Comment comment = Mockito.mock(Comment.class);
        Mockito.when(comment.getMessage()).thenReturn("Test Message");

        ResponseEntity responseEntity = Mockito.mock(ResponseEntity.class);
        Mockito.when(responseEntity.getBody()).thenReturn("[{\"flagged\":false," +
                "\"categories\":{\"sexual\":false,\"hate\":false,\"harassment\":false,\"self-harm\":false,\"sexual/minors\":false,\"hate/threatening\":false,\"violence/graphic\":false,\"self-harm/intent\":false,\"self-harm/instructions\":false,\"harassment/threatening\":false,\"violence\":false}," +
                "\"category_scores\":{\"sexual\":0.0012596443993970752,\"hate\":7.599094533361495E-5,\"harassment\":9.230645810021088E-5,\"self-harm\":4.361209357739426E-5,\"sexual/minors\":2.925005219367449E-6,\"hate/threatening\":1.4075258150114678E-6,\"violence/graphic\":2.081236380035989E-5,\"self-harm/intent\":2.729771949816495E-6,\"self-harm/instructions\":1.313146299253276E-6,\"harassment/threatening\":8.81807750374719E-7,\"violence\":5.2090032113483176E-5}}]");

        Mockito.when(moderationAPIController.requestModeration("Test Message")).thenReturn(responseEntity);
        moderationAPIController.moderateComment(comment);
        Mockito.verify(comment, Mockito.times(1)).setFlagged(false);
        Mockito.verify(comment, Mockito.times(1)).setFlaggedReason(null);
    }

    @Test
    void testModerateComment_throwException() throws JsonProcessingException {
        Comment comment = Mockito.mock(Comment.class);
        Mockito.when(comment.getMessage()).thenReturn("Test Message");

        Mockito.when(moderationAPIController.requestModeration("Test Message")).thenThrow(Mockito.mock(JsonProcessingException.class));
        moderationAPIController.moderateComment(comment);
        Mockito.verify(comment, Mockito.times(0)).setFlagged(false);
        Mockito.verify(comment, Mockito.times(0)).setFlaggedReason(null);
    }

    @Test
    void testRequestModeration_noUser() throws JsonProcessingException {
        Mockito.when(authentication.getPrincipal()).thenReturn(null);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, moderationAPIController.requestModeration("Test Message").getStatusCode());
    }

    @Test
    void testRequestModeration_tooManyRequests() throws JsonProcessingException {
        User user = Mockito.mock(User.class);
        Mockito.when(authentication.getPrincipal()).thenReturn("email");
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(user.getId()).thenReturn(1L);

        Mockito.when(apiRateLimiter.allowRequest(1L)).thenReturn(false);
        Assertions.assertEquals(HttpStatus.TOO_MANY_REQUESTS, moderationAPIController.requestModeration("Test Message").getStatusCode());
    }

    @Test
    void testRequestModeration() throws JsonProcessingException {
        User user = Mockito.mock(User.class);
        Mockito.when(authentication.getPrincipal()).thenReturn("email");
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(user.getId()).thenReturn(1L);
        Mockito.when(apiRateLimiter.allowRequest(1L)).thenReturn(true);
        Assertions.assertEquals(HttpStatus.OK, moderationAPIController.requestModeration("Test Message").getStatusCode());
    }
}
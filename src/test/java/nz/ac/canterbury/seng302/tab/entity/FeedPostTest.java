package nz.ac.canterbury.seng302.tab.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.stream.Stream;

class FeedPostTest {

    User user;

    @BeforeEach
    void setUp() {
        user = Mockito.mock(User.class);
    }

    @Test
    void createFeedPostTest() {
        LocalDateTime dateTime = LocalDateTime.now();
        FeedPost feedPost = new FeedPost(1L, OwnerType.USER, "test", "test", "test", user, null);
        Assertions.assertEquals(1L, feedPost.getOwnerId());
        Assertions.assertEquals(OwnerType.USER, feedPost.getOwnerType());
        Assertions.assertEquals("test", feedPost.getOwnerName());
        Assertions.assertEquals("test", feedPost.getMessage());
        Assertions.assertEquals(user, feedPost.getAuthor());
        Assertions.assertNull(feedPost.getAttachmentName());
        Assertions.assertFalse(feedPost.getFlagged());
        Assertions.assertEquals("N/A", feedPost.getFlaggedReason());
    }

    @Test
    void videoAttachment() {
        String attachmentName = "filename.mp4";
        FeedPost feedPost = new FeedPost(1L, OwnerType.USER, "test", "test", "test", user, attachmentName);
        Assertions.assertTrue(feedPost.isVideo());
        Assertions.assertFalse(feedPost.isImg());
    }

    @Test
    void imgAttachment() {
        String attachmentName = "filename.jpg";
        FeedPost feedPost = new FeedPost(1L, OwnerType.USER, "test", "test", "test", user, attachmentName);
        Assertions.assertFalse(feedPost.isVideo());
        Assertions.assertTrue(feedPost.isImg());
    }


    private static Stream<Arguments> provideStringsForAttachmentTypes() {
        return Stream.of(
                Arguments.of("jpg", "filename.jpg"),
                Arguments.of("jpeg", "filename.jpeg"),
                Arguments.of("svg", "filename.svg")
        );
    }

    @ParameterizedTest
    @MethodSource("provideStringsForAttachmentTypes")
    void getAttachmentType(String expected, String attachmentName) {
        FeedPost feedPost = new FeedPost(1L, OwnerType.USER, "test", "test", "test", user, attachmentName);
        Assertions.assertEquals(expected, feedPost.getAttachmentType());
    }

    @Test
    void hasFlagReason() {
        LocalDateTime dateTime = LocalDateTime.now();
        FeedPost feedPost = new FeedPost(1L, OwnerType.USER, "test", "test", "test", user, null);
        feedPost.setFlagged(true);
        feedPost.setFlaggedReason("aReasonToFlag");
        Assertions.assertEquals("aReasonToFlag", feedPost.getFlaggedReason());
    }

    @Test
    void hasFlagReason_notFlagged() {
        LocalDateTime dateTime = LocalDateTime.now();
        FeedPost feedPost = new FeedPost(1L, OwnerType.USER, "test", "test", "test", user, null);
        feedPost.setFlagged(false);
        feedPost.setFlaggedReason("aReasonToFlag");
        Assertions.assertEquals("N/A", feedPost.getFlaggedReason());
    }

    @Test
    void copyFeedPost_test() {
        LocalDateTime dateTime = LocalDateTime.now();
        FeedPost feedPost = new FeedPost(1L, OwnerType.USER, "test", "test", "test", user, null);
        FeedPost copy = new FeedPost(feedPost);
        Assertions.assertEquals(feedPost.getOwnerId(), copy.getOwnerId());
        Assertions.assertEquals(feedPost.getOwnerType(), copy.getOwnerType());
        Assertions.assertEquals(feedPost.getOwnerName(), copy.getOwnerName());
        Assertions.assertEquals(feedPost.getMessage(), copy.getMessage());
        Assertions.assertEquals(feedPost.getAuthor(), copy.getAuthor());
        Assertions.assertEquals(feedPost.getAttachmentName(), copy.getAttachmentName());
        Assertions.assertEquals(feedPost.getFlagged(), copy.getFlagged());
        Assertions.assertEquals(feedPost.getFlaggedReason(), copy.getFlaggedReason());
    }
}

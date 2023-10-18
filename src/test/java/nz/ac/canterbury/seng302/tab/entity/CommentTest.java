package nz.ac.canterbury.seng302.tab.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

class CommentTest {
    User user;
    @BeforeEach
    void setUp() {
        user = Mockito.mock(User.class);
    }
    @Test
    void testConstructor() {
        Long postId = 1L;
        Long parentCommentId = 2L;
        String message = "message";
        LocalDateTime localDateTime = LocalDateTime.now();

        Comment comment = new Comment(postId, parentCommentId, message, user, localDateTime);
        Assertions.assertEquals(postId, comment.getPostId());
        Assertions.assertEquals(parentCommentId, comment.getParentCommentId());
        Assertions.assertEquals(message, comment.getMessage());
        Assertions.assertEquals(user, comment.getAuthor());
        Assertions.assertEquals(localDateTime, comment.getDateTime());
        Assertions.assertFalse(comment.getFlagged());
        Assertions.assertEquals("N/A", comment.getFlaggedReason());
    }

    @Test
    void testSetFlagged() {
        Comment comment = new Comment(1L, 2L, "message", user, LocalDateTime.now());
        comment.setFlagged(true);
        Assertions.assertTrue(comment.getFlagged());
    }

    @Test
    void testSetFlaggedReason_noFlag() {
        Comment comment = new Comment(1L, 2L, "message", user, LocalDateTime.now());
        comment.setFlaggedReason("reason");
        Assertions.assertEquals("N/A", comment.getFlaggedReason());
    }

    @Test
    void testSetFlaggedReason_flagged() {
        Comment comment = new Comment(1L, 2L, "message", user, LocalDateTime.now());
        comment.setFlagged(true);
        comment.setFlaggedReason("reason");
        Assertions.assertEquals("reason", comment.getFlaggedReason());
    }
}

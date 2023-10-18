package nz.ac.canterbury.seng302.tab.repository;

import jakarta.annotation.Resource;
import nz.ac.canterbury.seng302.tab.entity.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootTest
class CommentRepositoryTest {

    @Resource
    private CommentRepository commentRepository;

    @Resource
    private FeedPostRepository feedPostRepository;

    @Resource
    private UserRepository userRepository;

    @Resource
    private LocationRepository locationRepository;

    @Test
    void findById() {
        Optional<Comment> comment = commentRepository.findById(1L);
        Assertions.assertTrue(comment.isPresent());
        Assertions.assertEquals(1L, comment.get().getId());
    }

    @Test
    void findAllByPostIdAndParentCommentIdIsNull() {
        Assertions.assertEquals(2, commentRepository.findAllByPostIdAndParentCommentIdIsNull(2L).size());
    }

    @Test
    void findAllByParentCommentId() {
        Assertions.assertEquals(1, commentRepository.findAllByParentCommentId(1L).size());
    }


    @Test
    void findFlaggedTeamComments() {
        List<Comment> ogCommentPosts = commentRepository.findFlaggedTeamComments(1L);
        int ogNumFlaggedTeamComments = ogCommentPosts.size();

        Location location = new Location("test", "test", "test", "test", "test", "test");
        User user = new User("test", "test", "test", LocalDate.now(), location, "test");
        FeedPost feedPost = new FeedPost(1L,
                OwnerType.TEAM,
                "Nathan Harper",
                "Post title1",
                "Post message1",
                user,
                null);

        feedPost.setFlagged(true);
        locationRepository.save(location);
        userRepository.save(user);
        Long feedPostId = feedPostRepository.save(feedPost).getId();
        Comment comment = new Comment(feedPostId, null, "message", user, LocalDateTime.now());
        comment.setFlagged(true);

        Long commentId = commentRepository.save(comment).getId();

        List<Comment> dbCommentPosts = commentRepository.findFlaggedTeamComments(1L);
        int numFlaggedTeamComments = dbCommentPosts.size();

        Assertions.assertEquals(1, numFlaggedTeamComments - ogNumFlaggedTeamComments);

        commentRepository.deleteById(commentId);
        dbCommentPosts = commentRepository.findFlaggedTeamComments(1L);
        Assertions.assertEquals(numFlaggedTeamComments - 1, dbCommentPosts.size());
    }


    @Test
    void findFlaggedClubComments() {
        List<Comment> ogCommentPosts = commentRepository.findFlaggedTeamComments(1L);
        int ogNumFlaggedClubComments = ogCommentPosts.size();

        Location location = new Location("test", "test", "test", "test", "test", "test");
        User user = new User("test", "test", "test", LocalDate.now(), location, "test");
        FeedPost feedPost = new FeedPost(1L,
                OwnerType.CLUB,
                "Nathan Harper",
                "Post title1",
                "Post message1",
                user,
                null);

        feedPost.setFlagged(true);
        locationRepository.save(location);
        userRepository.save(user);
        Long feedPostId = feedPostRepository.save(feedPost).getId();
        Comment comment = new Comment(feedPostId, null, "message", user, LocalDateTime.now());
        comment.setFlagged(true);

        Long commentId = commentRepository.save(comment).getId();
        List<Comment> dbCommentPosts = commentRepository.findFlaggedClubComments(1L);
        int numFlaggedClubComments = dbCommentPosts.size();

        Assertions.assertEquals(1, numFlaggedClubComments - ogNumFlaggedClubComments);

        commentRepository.deleteById(commentId);
        dbCommentPosts = commentRepository.findFlaggedClubComments(1L);
        Assertions.assertEquals(numFlaggedClubComments - 1, dbCommentPosts.size());
    }
}

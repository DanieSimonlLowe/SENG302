package nz.ac.canterbury.seng302.tab.repository;
import nz.ac.canterbury.seng302.tab.entity.FeedPost;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.OwnerType;
import nz.ac.canterbury.seng302.tab.entity.User;
import org.springframework.boot.test.context.SpringBootTest;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
class FeedPostRepositoryTest {

    @Resource
    private FeedPostRepository feedPostRepository;

    @Resource
    private UserRepository userRepository;

    @Resource
    private LocationRepository locationRepository;

    @Test
    void findPersonalFeedPosts() {
        List<FeedPost> databaseFeedPosts = feedPostRepository.findPersonalFeedPosts(1L);
        Assertions.assertEquals(4, databaseFeedPosts.size());
    }

    @Test
    void delete() {
        Location location = new Location("test", "test", "test", "test", "test", "test");
        User user = new User("test", "test", "test", LocalDate.now(), location, "test");
        FeedPost feedPost = new FeedPost(3L,
                OwnerType.USER,
                "Nathan Harper",
                "Post title",
                "Post message",
                user,
                null);

        locationRepository.save(location);
        userRepository.save(user);
        Long feedPostId = feedPostRepository.save(feedPost).getId();
        List<FeedPost> databaseFeedPosts = feedPostRepository.findPersonalFeedPosts(1L);
        Assertions.assertEquals(5, databaseFeedPosts.size());
        feedPostRepository.deleteById(feedPostId);
        databaseFeedPosts = feedPostRepository.findPersonalFeedPosts(1L);
        Assertions.assertEquals(4, databaseFeedPosts.size());
    }


    @Test
    void findFlaggedTeamPosts() {
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
        List<FeedPost> databaseFeedPosts = feedPostRepository.findFlaggedTeamFeedPosts(1L);
        int numFlaggedTeamPosts = databaseFeedPosts.size();
        Assertions.assertTrue(1 <= databaseFeedPosts.size());
        feedPostRepository.deleteById(feedPostId);
        databaseFeedPosts = feedPostRepository.findFlaggedTeamFeedPosts(1L);
        Assertions.assertEquals(numFlaggedTeamPosts - 1, databaseFeedPosts.size());
    }


    @Test
    void findFlaggedClubPosts() {
        Location location = new Location("test", "test", "test", "test", "test", "test");
        User user = new User("test", "test", "test", LocalDate.now(), location, "test");
        FeedPost feedPost = new FeedPost(1L,
                OwnerType.CLUB,
                "Nathan Harper",
                "Post title2",
                "Post message2",
                user,
                null);
        feedPost.setFlagged(true);
        locationRepository.save(location);
        userRepository.save(user);
        Long feedPostId = feedPostRepository.save(feedPost).getId();
        List<FeedPost> databaseFeedPosts = feedPostRepository.findFlaggedClubFeedPosts(1L);
        int numFlaggedClubPosts = databaseFeedPosts.size();
        Assertions.assertTrue(1 <= databaseFeedPosts.size());
        feedPostRepository.deleteById(feedPostId);
        databaseFeedPosts = feedPostRepository.findFlaggedClubFeedPosts(1L);
        Assertions.assertEquals(numFlaggedClubPosts - 1, databaseFeedPosts.size());
    }
}

package nz.ac.canterbury.seng302.tab.service;
import nz.ac.canterbury.seng302.tab.entity.FeedPost;
import nz.ac.canterbury.seng302.tab.repository.FeedPostRepository;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.junit.jupiter.api.extension.ExtendWith;
import jakarta.transaction.Transactional;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootTest
@Transactional
@ExtendWith(MockitoExtension.class)
@WithMockUser("morgan.english@hotmail.com")
 class FeedPostServiceTest {

    FeedPostService feedPostService;

    FeedPostRepository mockFeedPostRepository;

    FeedPost mockFeedPost;

    @BeforeEach
    void beforeEach() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("morgan.english@hotmail.com",null));
        mockFeedPostRepository = Mockito.mock(FeedPostRepository.class);
        feedPostService = new FeedPostService(mockFeedPostRepository);

        mockFeedPost = Mockito.mock(FeedPost.class);
    }

    @Test
    void test_savingFeedPost() {
        feedPostService.save(mockFeedPost);
        Mockito.verify(mockFeedPostRepository).save(mockFeedPost);
    }

    @Test
    void test_getFeedPostById() {
        feedPostService.getFeedPostById(1L);
        Mockito.verify(mockFeedPostRepository).findById(1L);
    }

    @Test
    void test_getPersonalFeedPosts() {
        feedPostService.getPersonalFeedPosts(1L);
        Mockito.verify(mockFeedPostRepository).findPersonalFeedPosts(1L);
    }

    @Test
    void test_deleteFeedPost() {
        feedPostService.deleteFeedPost(1L);
        Mockito.verify(mockFeedPostRepository).deleteById(1L);
    }


    @Test
    void test_getFlaggedTeamFeedPostById() {
        feedPostService.getFlaggedTeamFeedPosts(1L);
        Mockito.verify(mockFeedPostRepository).findFlaggedTeamFeedPosts(1L);
    }


    @Test
    void test_getFlaggedClubFeedPostById() {
        feedPostService.getFlaggedClubFeedPosts(1L);
        Mockito.verify(mockFeedPostRepository).findFlaggedClubFeedPosts(1L);
    }
}

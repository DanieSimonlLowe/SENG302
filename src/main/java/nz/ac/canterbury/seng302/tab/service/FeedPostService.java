package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.FeedPost;
import nz.ac.canterbury.seng302.tab.repository.FeedPostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FeedPostService {
    private final FeedPostRepository feedPostRepository;

    /**
     * Constructor for FeedPostService
     * @param feedPostRepository the repository to be used
     */
    public FeedPostService(FeedPostRepository feedPostRepository) {
        this.feedPostRepository = feedPostRepository;
    }

    /**
     * Saves a feed post to the database
     * @param feedPost the feed post
     */
    public void save(FeedPost feedPost) {
        feedPostRepository.save(feedPost);
    }

    /**
     * Gets a feed post from the database by id
     * @param id the id
     * @return a feed post
     */
    public Optional<FeedPost> getFeedPostById(long id) {
        return feedPostRepository.findById(id);
    }

    /**
     * Finds all the feed posts for one specific user's feed, gathering posts from teams and users that they follow
     * @param userId the id of the user
     * @return a list of feed posts
     */
    public List<FeedPost> getPersonalFeedPosts(Long userId) {
        return feedPostRepository.findPersonalFeedPosts(userId);
    }

    /**
     * Finds all the feed posts for one specific club's feed
     * @param clubId the id of the club
     * @return a list of feed posts
     */
    public List<FeedPost> getClubFeedPosts(Long clubId) {
        return feedPostRepository.findClubFeedPosts(clubId);
    }

    /**
     * Finds all the feed posts for one specific team's feed
     * @param teamId the id of the team
     * @return a list of feed posts
     */
    public List<FeedPost> getTeamFeedPosts(Long teamId) {
        return feedPostRepository.findTeamFeedPosts(teamId);
    }


    /**
     * Deletes a feed post by its id
     * @param postId the feed post id to delete
     */
    public void deleteFeedPost(Long postId) {
        feedPostRepository.deleteById(postId);
    }



    /**
     * Finds all the flagged feed posts for one specific club's feed
     * @param clubId the id of the club
     * @return a list of flagged feed posts
     */
    public List<FeedPost> getFlaggedClubFeedPosts(Long clubId) {
        return feedPostRepository.findFlaggedClubFeedPosts(clubId);
    }


    /**
     * Finds all the flagged feed posts for one specific team's feed
     * @param teamId the id of the team
     * @return a list of flagged feed posts
     */
    public List<FeedPost> getFlaggedTeamFeedPosts(Long teamId) {
        return feedPostRepository.findFlaggedTeamFeedPosts(teamId);
    }
}

package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.FeedPost;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface FeedPostRepository extends CrudRepository<FeedPost, Long> {

    /**
     * Finds a FeedPost by its id
     * @param id the id
     * @return a feed post
     */
    Optional<FeedPost> findById(Long id);

    /**
     * Finds all the feed posts for one specific user's feed, gathering posts from teams and users that they follow
     * @param userId the id of the user
     * @return a list of feed posts
     */
    @Query(value = "SELECT * FROM feed_post " +
            "WHERE owner_type = '0'"  +  // OwnerType.USER
            "AND flagged = false " +
            "AND owner_id IN " +
            "(SELECT following_id " +
            "FROM user_following " +
            "WHERE user_id = :userId) " +
            "OR owner_type = '1'" + // OwnerType.TEAM
            "AND flagged = false " +
            "AND owner_id IN " +
            "(SELECT team_id " +
            "FROM team_following " +
            "WHERE user_id = :userId) " +
            "OR owner_type = '1'" + // OwnerType.TEAM
            "AND flagged = false " +
            "AND owner_id IN " +
            "(SELECT team_id " +
            "FROM team_member " +
            "WHERE user_id = :userId) " +
            "OR owner_type = '2'" + // OwnerType.CLUB
            "AND flagged = false " +
            "AND owner_id IN " +
            "(SELECT club_id " +
            "FROM club_teams " +
            "WHERE teams_id IN " +
            "(SELECT team_id " +
            "FROM team_member " +
            "WHERE user_id = :userId)) " +
            "ORDER BY date_time DESC",
            nativeQuery = true)
    List<FeedPost> findPersonalFeedPosts(Long userId);

    /**
     * Finds all the feed posts for one specific club's feed
     * @param clubId the id of the club
     * @return a list of feed posts
     */
    @Query(value = "SELECT * FROM feed_post " +
            "WHERE owner_type = '2'" + // OwnerType.CLUB
            "AND flagged = false " +
            "AND owner_id = :clubId " +
            "ORDER BY date_time DESC",
            nativeQuery = true)
    List<FeedPost> findClubFeedPosts(Long clubId);

    /**
     * Finds all the feed posts for one specific team's feed
     * @param teamId the id of the team
     * @return a list of feed posts
     */
    @Query(value = "SELECT * FROM feed_post " +
            "WHERE owner_type = '1'" + // OwnerType.TEAM
            "AND flagged = false " +
            "AND owner_id = :teamId " +
            "ORDER BY date_time DESC",
            nativeQuery = true)
    List<FeedPost> findTeamFeedPosts(Long teamId);

    /**
     * Deletes a feed post by its id
     * @param postId the feed post id to delete
     */
    @Modifying
    void deleteById(Long postId);


    /**
     * Finds all the flagged feed posts for one specific team's feed for review
     * @param teamId the id of the team
     * @return a list of flagged feed posts
     */
    @Query(value = "SELECT * FROM feed_post " +
            "WHERE owner_type = '1'" + // OwnerType.TEAM
            "AND owner_id = :teamId " +
            "AND flagged = true " +
            "ORDER BY date_time DESC",
            nativeQuery = true)
    List<FeedPost> findFlaggedTeamFeedPosts(Long teamId);

    /**
     * Finds all the flagged feed posts for one specific club's feed for review
     * @param clubId the id of the club
     * @return a list of flagged feed posts
     */
    @Query(value = "SELECT * FROM feed_post " +
            "WHERE owner_type = '2'" + // OwnerType.CLUB
            "AND owner_id = :clubId " +
            "AND flagged = true " +
            "ORDER BY date_time DESC",
            nativeQuery = true)
    List<FeedPost> findFlaggedClubFeedPosts(Long clubId);


}

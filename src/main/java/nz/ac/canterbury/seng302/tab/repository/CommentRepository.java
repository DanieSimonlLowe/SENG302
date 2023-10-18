package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.Comment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * CommentRepository repository accessor using Spring's @link{CrudRepository}.
 * Repository to query data from the database.
 * */
public interface CommentRepository extends CrudRepository<Comment, Long> {
    /**
     * Finds a comment by the given id
     * @param id the id to search with
     * @return the comment with the given id
     */
    Optional<Comment> findById(long id);


    /**
     * Finds all the comments from persistence given a post id
     * @param id the id of the post
     * @return the list of all comments
     */
    @Query(value = "SELECT * FROM comment " +
            "WHERE post_id = :id " +
            "AND parent_comment_id IS null " +
            "AND flagged = false " +
            "ORDER BY date_time ASC",
            nativeQuery = true)
    List<Comment> findAllByPostIdAndParentCommentIdIsNull(Long id);


    /**
     * Finds all replies to a comment given the parent comment id
     * @param id the id of the parent comment
     * @return a list of all replies to a comment
     */
    @Query(value = "SELECT * FROM comment " +
            "WHERE parent_comment_id = :id " +
            "AND flagged = false " +
            "ORDER BY date_time ASC",
            nativeQuery = true)
    List<Comment> findAllByParentCommentId(Long id);


    /**
     * Finds all the flagged comments for one specific team's feed for review
     * @param teamId the id of the team
     * @return a list of flagged comments
     */
    @Query(value = "SELECT comment.* FROM comment " +
            "JOIN feed_post ON comment.post_id = feed_post.id " +
            "WHERE owner_type = '1' " + // OwnerType.TEAM
            "AND owner_id = :teamId " +
            "AND comment.flagged = true " +
            "ORDER BY date_time DESC",
            nativeQuery = true)
    List<Comment> findFlaggedTeamComments(Long teamId);


    /**
     * Finds all the flagged comments for one specific club's feed for review
     * @param clubId the id of the club
     * @return a list of flagged comments
     */
    @Query(value = "SELECT comment.* FROM comment " +
            "JOIN feed_post ON comment.post_id = feed_post.id " +
            "WHERE owner_type = '2' " + // OwnerType.CLUB
            "AND owner_id = :clubId " +
            "AND comment.flagged = true " +
            "ORDER BY date_time DESC",
            nativeQuery = true)
    List<Comment> findFlaggedClubComments(Long clubId);
}

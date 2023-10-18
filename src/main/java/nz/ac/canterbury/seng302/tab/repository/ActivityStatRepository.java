package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.stats.*;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * ActivityStatRepository repository accessor using Spring's @link{CrudRepository}.
 * Repository to query data from the database.
 * */
public interface ActivityStatRepository extends CrudRepository<ActivityStat, Long> {
    ActivityStat findById(long id);


    List<ActivityStat> findAllByActivityId(long activityId);


    /**
     * Finds all facts related to a particular activity
     * @param activityId the id of the activity
     * @return a list of facts
     */
    @Query(value = "SELECT * " +
            "FROM fact_stat " +
            "WHERE activity_id = :activityId " +
            "ORDER BY time"
            , nativeQuery = true)
    List<Fact> findAllFactByActivityId(long activityId);

    /**
     * Deletes a fact from persistence
     * @param factId the fact to delete
     */
    @Modifying
    @Query(value = "DELETE " +
            "FROM fact_stat " +
            "WHERE id = :factId "
            , nativeQuery = true)
    void deleteFact(long factId);

    /**
     * Finds all substitutions related to a particular activity
     * @param activityId the id of the activity
     * @return a list of substitutions
     */
    @Query(value = "SELECT * FROM substituted_stat " +
            "WHERE activity_id = :activityId "
            , nativeQuery = true)
    List<Substituted> findAllSubstitutedByActivityId(long activityId);

    /**
     * Finds all scores related to a particular activity
     * @param activityId the id of the activity
     * @return a list of scores
     */
    @Query(value = "SELECT * FROM individual_score_stat " +
            "WHERE activity_id = :activityId " +
            "ORDER BY score_minute ASC"
            , nativeQuery = true)
    List<IndividualScore> findAllIndividualScoreByActivityId(long activityId);

    /**
     * Finds the games core associated with an activity if it exists
     * @param activityId the id of the activity
     * @return the game score of the activity
     * */
    @Query(value = "SELECT * FROM game_score_stat " +
            "WHERE activity_id = :activityId "
            , nativeQuery = true
    )
    Optional<GameScore> findGameScoreByActivityId(long activityId);


}

package nz.ac.canterbury.seng302.tab.repository;

import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.tab.entity.LineUp;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface LineupRepository extends CrudRepository<LineUp, Long> {
    /**
     * Finds lineup by id
     * @param id the id to search for
     * @return an optional lineup
     */
    Optional<LineUp> findById(Long id);

    /**
     * Finds lineup by activity id
     * @param activityId the activity id to search for
     * @return an optional lineup
     */
    @Query(
            value="SELECT * FROM tab_lineup WHERE activity_id = :activityId",
            nativeQuery=true)
    Optional<LineUp> findByActivityId(Long activityId);

    /**
     * Removes all lineups by activity id
     * @param activityId the activity id to search for
     */
    @Transactional
    void removeAllByActivity_Id(Long activityId);

}

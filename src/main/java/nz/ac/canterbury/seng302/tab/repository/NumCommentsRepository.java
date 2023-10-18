package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.NumComments;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * NumComments repository accessor using Spring's @link{CrudRepository}.
 * Repository to query data from the database.
 * */
public interface NumCommentsRepository extends CrudRepository<NumComments, Long> {

    /**
     * Get all NumComments entities ordered by the number of comments and the last updated date.
     * @param id The ID of the club
     * @return A list of the numComments entities
     */
    List<NumComments> findAllByClubIdOrderByCountDescLastUpdatedAsc(Long id);

    /**
     * A NumComments object for a given user and club
     * @param clubId The id of the club
     * @param userId The id of th user
     * @return An optional NumComments object
     */
    Optional<NumComments> findByClubIdAndUserId(Long clubId, Long userId);
}

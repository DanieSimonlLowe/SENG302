package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.FeedAlerts;
import org.springframework.data.repository.CrudRepository;

/**
 * NumComments repository accessor using Spring's @link{CrudRepository}.
 * Repository to query data from the database.
 * */
public interface UserAlertsRepository extends CrudRepository<FeedAlerts, Long> {

    /**
     * Finds a FeedAlerts object by the user id
     * @param userId the id of the user
     * @return the FeedAlerts object with the given user id
     */
    FeedAlerts findByUserId(Long userId);

}

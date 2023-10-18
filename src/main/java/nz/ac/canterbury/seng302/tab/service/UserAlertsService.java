package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.FeedAlerts;
import nz.ac.canterbury.seng302.tab.repository.UserAlertsRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;


/**
 * Service class for the UserAlertsRepository
 */
@Service
public class UserAlertsService {

    /**
     * The UserAlertsRepository
     */
    private final UserAlertsRepository userAlertsRepository;


    /**
     * Constructor for the UserAlertsService
     * @param userAlertsRepository The UserAlertsRepository
     */
    public UserAlertsService(UserAlertsRepository userAlertsRepository) {
        this.userAlertsRepository = userAlertsRepository;
    }


    /**
     * Saves the UserAlerts
     */
    public void save(FeedAlerts feedAlerts) {
        userAlertsRepository.save(feedAlerts);
    }


    /**
     * Gets all the UserAlerts
     * @return A list of all the UserAlerts
     */
    public Optional<FeedAlerts> getUserAlertsById(long id) {
        return Optional.ofNullable(userAlertsRepository.findByUserId(id));
    }
}

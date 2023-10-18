package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.SportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The service for saving and querying sports to and from the database.
 * 
 * @author  Celia Allen   
 * @version 1.0.0, March 23 
 */
@Service
public class SportService {

    /** The logger */
    final Logger logger = LoggerFactory.getLogger(SportService.class);

    /** The sport repository */
    private final SportRepository sportRepository;

    /**
     * The constructor for the SportService class
     * @param sportRepository the sport repository
     */
    public SportService(SportRepository sportRepository) {
        this.sportRepository = sportRepository;
    }

    /**
     * Saves the sport to the database
     * @param sport the sport to be saved to the database
     */
    public void add(Sport sport) {
        sportRepository.save(sport);
        logger.info("Saved sport to repository.");
    }

    /**
     * Finds all the sports in the database
     * @return all the sports from persistence
     */
    public List<Sport> findAll() {
        return sportRepository.findAll();
    }

    /**
     * Gets a sport from persistence
     * @param id of sport to retrieve
     * @return sport with matching id
     */
    public Sport findSportById(long id) { return sportRepository.findById(id); }

    /**
     * Gets a sport from persistence
     * @param text the name of the sport that is being search for
     * @return the sport that match the search string
     */
    public Sport findSportByName(String text) { return sportRepository.findBySportName(text); }
    
    /**
     * Gets a list of sports that a user has favourited from persistence
     * @param user the user
     * @return a list of sports that the given user has favourited
     */
    public List<Sport> findSportsByUser(User user) { return sportRepository.findByUsers(user); }

    /**
     * Gets a list of sports that a list of users have favourited from persistence
     * @param users the list of users
     * @return a list of sports that the given users have favourited
     */
    public List<Sport> findSportsByUserIn(List<User> users) { return sportRepository.findByUsersIn(users); }

    /**
     * Checks whether a sport with the given name already exists in the database
     * @param name the sport name
     * @return true if sport exists, false if not
     */
    public Boolean existsBySportName(String name) { return sportRepository.existsBySportName(name); }

}









    
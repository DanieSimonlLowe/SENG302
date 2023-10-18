package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * SportRepository repository accessor using Spring's @link{CrudRepository}.
 * Repository to query data from the database
 */
public interface SportRepository extends CrudRepository<Sport, Long> {

    /**
     * Finds all the sports
     * @return a list of all the sports
     */
    List<Sport> findAll();

    /**
     * Finds a sport from persistence with the given id
     * @param id the id of the sport to find
     * @return the sport with the matching id
     */
    Sport findById(long id);

    /**
     * Finds a sport from persistence with the given sport name
     * @param text  the text to search against
     * @return      a list of sports that match the query
     */
    Sport findBySportName(String text);

    /**
     * Finds a list of sports from the given user id
     * @param user  the user
     * @return      a list of sports that the user has favourited
     */
    List<Sport> findByUsers(User user);

    /**
     * Finds a list of sports from the given list of users
     * @param users  the list of users
     * @return       a list of sports that the users have favourited
     */
    List<Sport> findByUsersIn(List<User> users);

    /**
     * Checks whether a sport with the given name already exists in the database
     * @param name the sport name
     * @return true if sport exists, false if not
     */
    boolean existsBySportName(String name);
}


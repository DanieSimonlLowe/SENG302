package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * UserRepository repository accessor using Spring's @link{CrudRepository}.
 * Repository to query data from the database
 */
@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    /**
     * Finds user by their id
     * @param id the id
     * @return a list of the user
     */
    Optional<User> findById(Long id);

    /**
     * Finds user by their email
     * @param email the email
     * @return the user which has the email specified
     */
    User findByEmailIgnoreCase(String email);

    /**
     * Finds all the users
     * @return a list of all the users
     */
    List<User> findAll();

    /**
     * Finds users whose first name match the search query (case-insensitive).
     * @param text the text to search against
     * @return a list of users that match the query
     */
    List<User> findUsersByFirstNameContainsIgnoreCase(String text);

    /**
     * Finds users whose last name match the search query (case-insensitive).
     * @param text the text to search against
     * @return a list of users that match the query
     */
    List<User> findUsersByLastNameContainsIgnoreCase(String text);

    /**
     * Finds a list of users that like the given sport
     * @param sport  the sport 
     * @return       a list of users that like the given sport
     */
    List<User> findByFavSports(Sport sport);


    /**
     * Finds a list of users that like one or more of the given sports
     * @param sports  the list of sports
     * @return        a list of users that like one or more of the given sports
     */
    List<User> findByFavSportsIn(List<Long> sports);

    /**
     * Gets a list of users that are from any of the locations given
     * @param locations The list of locations to filter the users by
     * @return A list of users that match the filters
     */
    @Query(value = "SELECT * FROM tab_users " +
            "JOIN location ON tab_users.location_id = location.id " +
            "WHERE UPPER(location.city) IN :locations " +
            "ORDER BY UPPER(last_name), UPPER(first_name)",
            nativeQuery = true)
    List<User> findByLocationIn(List<String> locations);


    @Query(value = "SELECT * FROM tab_users " +
            "JOIN location ON tab_users.location_id = location.id " +
            "WHERE user_id IN (SELECT sport_fav.user_id FROM sport_fav WHERE sport_id IN :sports GROUP BY user_id) " +
            "AND UPPER(location.city) IN :locations " +
            "ORDER BY UPPER(last_name), UPPER(first_name)",
            nativeQuery = true)
    List<User> findByFavSportsInAndLocation(@Param("locations") List<String> locations, @Param("sports") List<Long> sports);


    /**
     * Finds a list of Users that like noe or more of the given sports and whose names match the search string
     * @param sports  the list of sports to filter by
     * @param keyword the search string to search against
     * @return a list of users whose names match the search string and like one or more of the given sports
     */
    @Query(value= "SELECT * FROM tab_users " +
            "WHERE user_id IN (SELECT sport_fav.user_id FROM sport_fav WHERE sport_id IN :sports GROUP BY user_id) " +
            "AND (UPPER(first_name) LIKE %:keyword% " +
            "OR UPPER(last_name) LIKE %:keyword%) " +
            "ORDER BY UPPER(last_name), UPPER(first_name)",
            nativeQuery = true)
    List<User> findBySportInAndKeyword(@Param("sports") List<Long> sports,
                                       @Param("keyword") String keyword);


    @Query(value= "SELECT * FROM tab_users " +
            "JOIN location ON tab_users.location_id = location.id " +
            "WHERE UPPER(location.city) in :locations " +
            "AND (UPPER(first_name) LIKE %:keyword% " +
            "OR UPPER(last_name) LIKE %:keyword%) " +
            "ORDER BY UPPER(last_name), UPPER(first_name)",


            nativeQuery = true)
    List<User> findByLocationInAndKeyword(@Param("locations") List<String> locations,
                                          @Param("keyword") String keyword);


    @Query(value = "SELECT * FROM tab_users " +
            "JOIN location ON tab_users.location_id = location.id " +
            "WHERE user_id IN (SELECT sport_fav.user_id FROM sport_fav WHERE sport_id IN :sports GROUP BY user_id) " +
            "AND UPPER(location.city) IN :locations " +
            "AND (UPPER(first_name) LIKE %:keyword% " +
            "OR UPPER(last_name) LIKE %:keyword%) " +
            "ORDER BY UPPER(last_name), UPPER(first_name)",
            nativeQuery = true)
    List<User> findByFavSportsInAndLocationAndKeyword(@Param("locations") List<String> locations,
                                                      @Param("sports") List<Long> sports,
                                                      @Param("keyword") String keyword);


    /**
     * Gets all the distinct cities from the repository which a user in the given list is located in
     * @param userIds list of user ids
     * @return distinct cities from the repository which a user in the given list is located in
     */
    @Query(value = "SELECT DISTINCT UPPER(location.city) " +
            "FROM location " +
            "JOIN tab_users ON tab_users.location_id = location.id " +
            "WHERE tab_users.user_id IN :userIds",
            nativeQuery = true)
    List<String> findDistinctCityFromUsers(@Param("userIds") List<Long> userIds);

    User findByUpdateToken(String value);

    /**
     * gets a list of all the users who are a member of a team.
     * @param teamId the id of the team that the members are got from.
     * @return a list of users that are in a team
     * */
    @Query(value = "SELECT tab_users.* " +
            "FROM tab_users JOIN team_member ON tab_users.user_id = team_member.user_id " +
            "WHERE team_member.team_id = :teamId ",
            nativeQuery = true)
    List<User> findUsersWhoAreInTeam(@Param("teamId") long teamId);

    /**
     * Gets a list of users that are friends with the given user
     * @param userId the user
     * @return a list of users that are friends with the given user
     */
    @Query(value = "SELECT u1.user_id " +
            "FROM user_following u1 " +
            "JOIN user_following u2 ON u1.user_id = u2.following_id AND u2.user_id = :userId " +
            "WHERE u1.following_id = :userId",
            nativeQuery = true)
    List<Long> findFriends(@Param("userId") long userId);

    /**
     * Gets a list of all the users who the given user is following
     * @param userId the id of the user
     * @return a list of users that the given user is following
     */
    @Query(value = "SELECT tab_users.* " +
            "FROM tab_users JOIN user_following ON tab_users.user_id = user_following.following_id " +
            "WHERE user_following.user_id = :userId ",
            nativeQuery = true)
    List<User> findFollowedUsersByUserId(@Param("userId") long userId);

    /**
     * Finds all the users with the given ids
     * @param ids must not be {@literal null} nor contain any {@literal null} values.
     * @return a list of users with the given ids
     */
    @Query(value = "SELECT DISTINCT * FROM tab_users WHERE user_id IN :ids",
            nativeQuery = true)
    List<User> findAllById(Iterable<Long> ids);

    /**
     * Counts all the teams that two users are both in
     * @param id1 the id of the first user
     * @param id2 the id of the second user
     * @return the number of teams that the users shear in common
     * */
    @Query(value = "SELECT COUNT(*) " +
            "FROM team_member " +
            "WHERE user_id = :id1 " +
            "AND team_id in ( " +
            "    SELECT team_id " +
            "    FROM team_member " +
            "    WHERE user_id = :id2 " +
            ")",
            nativeQuery = true)
    int countTeamsInCommon(long id1, long id2);

    /**
     * gets if two users have a club in common
     * @param id1 the id of the first user
     * @param id2 the id of the second user
     * @return more than 0 if they have a club in common otherwise is 0
     * */
    @Query(value = "SELECT COUNT(*) " +
            "FROM team_member " +
            "WHERE user_id = :id1 " +
            "AND team_id in ( " +
            "    SELECT team_id " +
            "    FROM club_teams " +
            "    WHERE club_id in ( " +
            "        SELECT club_id " +
            "        FROM club_teams " +
            "        WHERE team_id in ( " +
            "            SELECT team_id " +
            "            FROM team_member " +
            "            WHERE user_id = :id2 " +
            "        ) " +
            "    ) " +
            ")", nativeQuery = true)
    int hasClubInCommon(long id1, long id2);
}

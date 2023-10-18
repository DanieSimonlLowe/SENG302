package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.Formation;
import nz.ac.canterbury.seng302.tab.entity.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * TeamRepository repository accessor using Spring's @link{CrudRepository}.
 * Repository to query data from the database
 *
 */
public interface TeamRepository extends CrudRepository<Team, Long> {

    /**
     * Finds a team by the given id
     * @param id the id to search with
     * @return the team with the given id
     */
    Optional<Team> findById(long id);

    /**
     * Finds all the teams from persistence
     * @return the list of all teams
     */
    List<Team> findAll();

    /**
     * Finds a page of teams 
     * @param pageable the Pageable to find the teams by
     * @return a Page of teams
     */
    Page<Team> findAll(Pageable pageable);

    /**
     * Gets the teams from the repository that have a name or location like the keyword
     *
     * @param keyword to search for
     * @return the resulting teams
     */
    @Query(value = "SELECT * FROM team " +
            "WHERE UPPER(location_string) LIKE %:keyword% " +
            "OR UPPER(name) LIKE %:keyword% " +
            "ORDER BY UPPER(location_string) DESC, UPPER(name)",
            nativeQuery = true)
    List<Team> findByKeyword(@Param("keyword") String keyword);


    /**
     * Gets the teams from the repository whose city matches one of the given cities
     * @param cities list of cities
     * @return the resulting teams
     */
    @Query(value = "SELECT team.id, " +
            "team.location_string, " +
            "team.name, " +
            "team.profile_pic_name, " +
            "team.sport, " +
            "team.location_id, " +
            "team.invitation_token, " +
            "team.date_created " +
            "FROM team " +
            "JOIN location ON team.location_id = location.id " +
            "WHERE UPPER(location.city) IN :cities " +
            "ORDER BY UPPER(location_string), UPPER(name)",
            nativeQuery = true)
    List<Team> findByCity(@Param("cities") List<String> cities);

    /**
     * Gets the teams from the repository whose city matches on of the given cities and the keyword
     * @param cities list of cities
     * @param keyword to search for
     * @return the resulting teams
     */
    @Query(value = "SELECT team.id, " +
            "team.location_string, " +
            "team.name, " +
            "team.profile_pic_name, " +
            "team.sport, " +
            "team.location_id, " +
            "team.invitation_token, " +
            "team.date_created " +
            "FROM team " +
            "JOIN location ON team.location_id = location.id " +
            "WHERE UPPER(location.city) IN :cities " +
            "AND (UPPER(location_string) LIKE %:keyword% " +
            "OR UPPER(name) LIKE %:keyword%) " +
            "ORDER BY UPPER(location_string), UPPER(name)",
            nativeQuery = true)
    List<Team> findByCityAndKeyword(@Param("cities") List<String> cities, @Param("keyword") String keyword);

    /**
     * Gets the teams from the repository that play one of the given sports
     * @param sports list of sport Ids
     * @return the resulting teams
     */
    @Query(value = "SELECT * FROM team " +
            "JOIN tab_sports ON UPPER(team.sport) = UPPER(tab_sports.sport_name) " +
            "WHERE tab_sports.sport_id IN :sports " +
            "ORDER BY UPPER(location_string), UPPER(name)",
            nativeQuery = true)
    List<Team> findBySport(@Param("sports") List<Long> sports);

    /**
     * Gets the teams from the repository that play one of the given sports and matches the keyword
     * @param sports list of sport Ids
     * @param keyword to search for
     * @return the resulting teams
     */
    @Query(value = "SELECT * FROM team " +
            "JOIN tab_sports ON UPPER(team.sport) = UPPER(tab_sports.sport_name) " +
            "WHERE tab_sports.sport_id IN :sports " +
            "AND (UPPER(location_string) LIKE %:keyword% " +
            "OR UPPER(name) LIKE %:keyword%) " +
            "ORDER BY UPPER(location_string), UPPER(name)",
            nativeQuery = true)
    List<Team> findBySportAndKeyword(@Param("sports") List<Long> sports, @Param("keyword") String keyword);

    @Query(value = "SELECT team.* " +
            "FROM team JOIN team_member ON team_member.team_id = team.id " +
            "WHERE team_member.user_id = :userId " +
            "AND team_member.role = 0 " +
            "AND sport LIKE %:keyword%"
            , nativeQuery = true)
    List<Team> findTeamsBySportName(@Param("userId") long userId,@Param("keyword") String keyword);

    /**
     * Gets the teams from the repository that are both located in one of the given cities and play one of the
     * given sports
     * @param sports list of sport Ids
     * @param cities list of cities
     * @return the resulting teams
     */
    @Query(value = "SELECT team.id, " +
            "team.location_string, " +
            "team.name, " +
            "team.profile_pic_name, " +
            "team.sport, " +
            "team.location_id, " +
            "team.invitation_token, " +
            "team.date_created " +
            "FROM team " +
            "JOIN location ON team.location_id = location.id " +
            "JOIN tab_sports ON UPPER(team.sport) = UPPER(tab_sports.sport_name) " +
            "WHERE tab_sports.sport_id IN :sports " +
            "AND UPPER(location.city) IN :cities " +
            "ORDER BY UPPER(location_string), UPPER(name)",
            nativeQuery = true)
    List<Team> findBySportAndCity(@Param("sports") List<Long> sports, @Param("cities") List<String> cities);

    /**
     * Gets the teams from the repository that are both located in one of the given cities and play one of the
     * given sports and match the given keyword
     * @param sports list of sport Ids
     * @param cities list of cities
     * @param keyword to search for
     * @return the resulting teams
     */
    @Query(value = "SELECT team.id, " +
            "team.location_string, " +
            "team.name, " +
            "team.profile_pic_name, " +
            "team.sport, " +
            "team.location_id, " +
            "team.invitation_token, " +
            "team.date_created " +
            "FROM team " +
            "JOIN location ON team.location_id = location.id " +
            "JOIN tab_sports ON UPPER(team.sport) = UPPER(tab_sports.sport_name) " +
            "WHERE tab_sports.sport_id IN :sports " +
            "AND UPPER(location.city) IN :cities " +
            "AND (UPPER(location_string) LIKE %:keyword% " +
            "OR UPPER(name) LIKE %:keyword%) " +
            "ORDER BY UPPER(location_string), UPPER(name)",
            nativeQuery = true)
    List<Team> findBySportAndCityAndKeyword(@Param("sports") List<Long> sports,
                                                @Param("cities") List<String> cities,
                                                @Param("keyword") String keyword);

    /**
     * Gets all the distinct cities from the repository which a team in the given list is located in
     * @param teamIds list of team ids
     * @return distinct cities from the repository which a team in the given list is located in
     */
    @Query(value = "SELECT DISTINCT UPPER(location.city) " +
            "FROM location " +
            "JOIN team ON team.location_id = location.id " +
            "WHERE team.id IN :teamIds",
            nativeQuery = true)
    List<String> findDistinctCityFromTeams(@Param("teamIds") List<Long> teamIds);


    /**
     * gets all the teams that the user has at least one of the roles specified by role in that team.
     * @param userId the id of the user that is being checked.
     * @param roles a list of the ids of the roles that are being checked.
     * @return a list of the teams that the user has at least one of the roles specified by role in that team.
     * */
    @Query(value = "SELECT team.* " +
            "FROM team JOIN team_member ON team_member.team_id = team.id " +
            "WHERE team_member.user_id = :userId " +
            "AND team_member.role IN :roles "
            , nativeQuery = true)
    List<Team> findTeamsThatUserHasRoles(@Param("userId") long userId, @Param("roles") int[] roles);


    /**
     * Gets all the formations belonging to a specific team
     * @param teamId the id of the team to search
     * @return a list of formations belonging to a team
     */
    @Query(value = "SELECT team_formations.formations_id " +
            "FROM team_formations " +
            "WHERE team_formations.team_id = :teamId "
            , nativeQuery = true)
    List<Formation> findFormationsByTeamId(@Param("teamId") long teamId);


    /**
     * Gets all teams that are not used by a club
     * @return a list of teams that are not used by a club.
     * */
    @Query(value = "SELECT team.* FROM team WHERE id NOT IN ( SELECT teams_id FROM club_teams )"
            , nativeQuery = true)
    List<Team> findFreeTeams();


    /**
     * Gets all teams that the user has certain roles in and are not in any team.
     * @param userId the id of the user being checked.
     * @param roles the roles that the user can fall in.
     * @return a list of teams that are not used by a club.
     * */
    @Query(value = "SELECT team.* " +
            "FROM team JOIN team_member ON team_member.team_id = team.id " +
            "WHERE team_member.user_id = :userId " +
            "AND team_member.role IN :roles " +
            "AND team.id NOT IN ( SELECT club_teams.teams_id FROM club_teams )",
            nativeQuery = true
    )
    List<Team> getFreeTeamNamesWhoUserManageOrCoach(@Param("userId") long userId, @Param("roles") int[] roles);

    /**
     * Gets a list of teams that the user is following
     * @param userId the user
     * @return a list of users that are following the given user
     */
    @Query(value = "SELECT team.* " +
            "FROM team " +
            "JOIN team_following ON team.id = team_following.team_id " +
            "WHERE team_following.user_id = :userId",
            nativeQuery = true)
    List<Team> findTeamsByFollowersId(@Param("userId") Long userId);

    /**
     * gets a count of all team members of a team that are also in the same team as the provided user
     * @param teamId the id of the team that is being checked.
     * @param userId the id of the user that is being checked.
     * @return a count of the number of team members of a team that are in a team with the user
     * */
    @Query(value = "SELECT COUNT(*) " +
            "FROM team_member " +
            "WHERE team_id = :teamId " +
            "AND user_id IN ( " +
            "  SELECT user_id " +
            "  FROM team_member " +
            "  WHERE team_id IN ( " +
            "    SELECT team_id " +
            "    FROM team_member " +
            "    WHERE user_id = :userId " +
            ")) ", nativeQuery = true)
    long countUsersInTeamWhoAreInTeamWithUser(@Param("teamId") long teamId, @Param("userId") long userId);
}

package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.Club;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * ClubRepository repository accessor using Spring's @link{CrudRepository}.
 * Repository to query data from the database.
 * */
public interface ClubRepository extends CrudRepository<Club, Long> {

    /**
     * Checks what club (id) a team (id) is in.
     * @param teamId the id of the team
     * @param clubId the id of the club
     * @return a list of teams with the given id.
     */
    @Query(value = "SELECT teams_id " +
            "FROM club_teams " +
            "WHERE club_teams.teams_id = :teamId " +
            "AND club_teams.club_id <> :clubId"
            , nativeQuery = true)
    List<Long> findTeamClubByTeamId(@Param("teamId") long teamId, @Param("clubId") long clubId);

    /**
     * Checks if a given team (id) is already part of another club.
     * @param teamId the id of the team
     * @return a list of teams with the given id.
     */
    @Query(value = "SELECT * " +
            "FROM club_teams " +
            "WHERE club_teams.teams_id = :teamId "
            , nativeQuery = true)
    List<Long> findTeamClubNoClubIdByTeamId(@Param("teamId") long teamId);


    @Query(value = "SELECT * " +
            "FROM club JOIN club_teams " +
            "ON club.id = club_teams.club_id " +
            "WHERE teams_id = :teamId"
            , nativeQuery = true)
    Optional<Club> findClubByTeamId(@Param("teamId") long teamId);

    /**
     * Gets the sport of the first team of a club
     * @param clubId the id of the club
     * @return a string of the club's team's sport.
     */
    @Query(value = "SELECT team.sport " +
            "FROM club_teams " +
            "JOIN team ON club_teams.teams_id = team.id" +
            "WHERE club_teams.club_id = :clubId "
            , nativeQuery = true)
    List<String> findClubTeamSport(@Param("clubId") long clubId);

    Optional<Club> findById(Long id);

    Optional<Club> findClubsByUserId(Long id);

    List<Club> findAll();

    /**
     * Finds a page of clubs
     * @param pageable the Pageable to find the clubs by
     * @return a Page of clubs
     */
    Page<Club> findAll(Pageable pageable);

    /**
     * Gets the clubs from the repository that have a name or location like the keyword
     *
     * @param keyword to search for
     * @return the resulting clubs
     */
    @Query(value = "SELECT * FROM club " +
            "WHERE UPPER(location_string) LIKE %:keyword% " +
            "OR UPPER(name) LIKE %:keyword% ",
            nativeQuery = true)
    List<Club> findByKeyword(@Param("keyword") String keyword);


    /**
     * Gets the clubs from the repository whose city matches one of the given cities
     * @param cities list of cities
     * @return the resulting clubs
     */
    @Query(value = "SELECT club.* " +
            "FROM club " +
            "JOIN location ON club.location_id = location.id " +
            "WHERE UPPER(location.city) IN :cities ",
            nativeQuery = true)
    List<Club> findByCity(@Param("cities") List<String> cities);

    /**
     * Gets the clubs from the repository whose city matches one of the given cities and the keyword is like the name or location
     * @param cities list of cities
     * @param keyword to search for
     * @return the resulting clubs
     */
    @Query(value = "SELECT club.* " +
            "FROM club " +
            "JOIN location ON club.location_id = location.id " +
            "WHERE UPPER(location.city) IN :cities " +
            "AND (UPPER(location_string) LIKE %:keyword% " +
            "OR UPPER(name) LIKE %:keyword%) ",
            nativeQuery = true)
    List<Club> findByCityAndKeyword(@Param("cities") List<String> cities, @Param("keyword") String keyword);

    /**
     * Gets all the distinct cities from the repository which a club in the given list is located in
     * @param clubIds list of team ids
     * @return distinct cities from the repository which a club in the given list is located in
     */
    @Query(value = "SELECT DISTINCT UPPER(location.city) " +
            "FROM location " +
            "JOIN club ON club.location_id = location.id " +
            "WHERE club.id IN :clubIds",
            nativeQuery = true)
    List<String> findDistinctCityFromClubs(@Param("clubIds") List<Long> clubIds);

    /**
     * Gets the clubs from the repository that play one of the given sports
     * @param sports list of sport Ids
     * @return the resulting clubs
     */
    @Query(value = "SELECT DISTINCT club.* FROM club " +
            "JOIN club_teams ON club_teams.club_id = club.id " +
            "JOIN team ON team.id = club_teams.teams_id " +
            "JOIN tab_sports ON UPPER(team.sport) = UPPER(tab_sports.sport_name) " +
            "WHERE tab_sports.sport_id IN :sports ",
            nativeQuery = true)
    List<Club> findBySport(@Param("sports") List<Long> sports);

    /**
     * Gets the clubs from the repository that play one of the given sports and matches the keyword
     * @param sports list of sport Ids
     * @param keyword to search for
     * @return the resulting clubs
     */
    @Query(value = "SELECT DISTINCT club.* FROM club " +
            "JOIN club_teams ON club_teams.club_id = club.id " +
            "JOIN team ON team.id = club_teams.teams_id " +
            "JOIN tab_sports ON UPPER(team.sport) = UPPER(tab_sports.sport_name) " +
            "WHERE tab_sports.sport_id IN :sports " +
            "AND (UPPER(club.location_string) LIKE %:keyword% " +
            "OR UPPER(club.name) LIKE %:keyword%) ",
            nativeQuery = true)
    List<Club> findBySportAndKeyword(@Param("sports") List<Long> sports, @Param("keyword") String keyword);


    /**
     * Gets the clubs from the repository that are both located in one of the given cities and play one of the
     * given sports
     * @param sports list of sport Ids
     * @param cities list of cities
     * @return the resulting clubs
     */
    @Query(value = "SELECT DISTINCT club.* FROM club " +
            "JOIN club_teams ON club_teams.club_id = club.id " +
            "JOIN team ON team.id = club_teams.teams_id " +
            "JOIN tab_sports ON UPPER(team.sport) = UPPER(tab_sports.sport_name) " +
            "JOIN location ON club.location_id = location.id " +
            "WHERE tab_sports.sport_id IN :sports " +
            "AND UPPER(location.city) IN :cities ",
            nativeQuery = true)
    List<Club> findBySportAndCity(@Param("sports") List<Long> sports, @Param("cities") List<String> cities);

    /**
     * Gets the clubs from the repository that are both located in one of the given cities and play one of the
     * given sports and match the given keyword
     * @param sports list of sport Ids
     * @param cities list of cities
     * @param keyword to search for
     * @return the resulting clubs
     */
    @Query(value = "SELECT DISTINCT club.* FROM club " +
            "JOIN club_teams ON club_teams.club_id = club.id " +
            "JOIN team ON team.id = club_teams.teams_id " +
            "JOIN tab_sports ON UPPER(team.sport) = UPPER(tab_sports.sport_name) " +
            "JOIN location ON club.location_id = location.id " +
            "WHERE tab_sports.sport_id IN :sports " +
            "AND UPPER(location.city) IN :cities " +
            "AND (UPPER(club.location_string) LIKE %:keyword% " +
            "OR UPPER(club.name) LIKE %:keyword%) ",
            nativeQuery = true)
    List<Club> findBySportAndCityAndKeyword(@Param("sports") List<Long> sports,
                                            @Param("cities") List<String> cities,
                                            @Param("keyword") String keyword);

    /**
     * Gets the users from the repository that are members of a club
     * @param clubId the id of the club
     * @return the resulting users ids
     */
    @Query(value = "SELECT tab_users.user_id FROM tab_users " +
            "JOIN team_member ON tab_users.user_id = team_member.user_id " +
            "JOIN team ON team_member.team_id = team.id " +
            "JOIN club_teams ON team.id = club_teams.teams_id " +
            "WHERE club_teams.club_id = :clubId ",
            nativeQuery = true)
    List<Long> findUsersInAClub(Long clubId);

    @Query(value = "SELECT team_member.user_id FROM team_member " +
            "WHERE role in :roles " +
            "AND team_member.user_id = :userId " +
            "AND team_member.team_id in ( " +
            "    SELECT club_teams.teams_id FROM club_teams " +
            "    WHERE club_teams.club_id = :clubId " +
            ")",
            nativeQuery = true)
    Optional<Long> findIfTeamMemberHasRole(@Param("roles") Integer[] roles,
                                           @Param("userId") Long userId,
                                           @Param("clubId") Long clubId);

    /**
     * Gets all the clubs from the repository that a user is a member of
     * @param userId the id of the user
     * @return the resulting clubs
     */
    @Query(value = "SELECT * FROM club " +
            "JOIN club_teams ON club.id = club_teams.club_id " +
            "WHERE club_teams.teams_id IN ( " +
            "    SELECT team_member.team_id FROM team_member " +
            "    WHERE team_member.user_id = :userId " +
            ")",
            nativeQuery = true)
    List<Club> findAllClubsByUser (Long userId);
}

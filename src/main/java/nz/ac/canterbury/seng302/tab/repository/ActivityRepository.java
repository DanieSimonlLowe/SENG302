package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.Activity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * ActivityRepository repository accessor using Spring's @link{CrudRepository}.
 * Repository to query data from the database.
 * */
public interface ActivityRepository  extends CrudRepository<Activity, Long>  {

    /**
     * Gets all the team activities that the user is part of the team for
     * @param userId the id of the user that is being checked.
     * @return A list of team activities where the user is part of the team.
     * */
    @Query(value = "SELECT tab_activities.* " +
            "FROM tab_activities JOIN team_member ON team_member.team_id = tab_activities.team_id JOIN team ON team.id = tab_activities.team_id " +
            "WHERE team_member.user_id = :userId "
            , nativeQuery = true)
    List<Activity> findTeamActivitiesThatHasUser(@Param("userId") long userId);

    /**
     * Gets all the personal activities that the user has created
     * @param userId the id of the user that is being checked.
     * @return A list of team activities where the user is part of the team.
     * */
    @Query(value = "SELECT * FROM tab_activities " +
            " WHERE user_id = :userId " +
            " AND team_id IS NULL;"
            , nativeQuery = true)
    List<Activity> findPersonalActivitiesThatHasUser(long userId);

    @Query(value="SELECT * " +
            "FROM tab_activities " +
            "WHERE tab_activities.id = :id " +
            "AND ( (user_id = :user " +
            "AND team_id IS NULL) OR " +
            "team_id IN " +
            "(SELECT team_member.team_id " +
            " FROM team_member " +
            " WHERE team_member.user_id = :user " +
            " AND team_member.role in :roles) )"
            , nativeQuery = true
    )
    List<Activity> findByIdWithTheUserOfRoleOrIsPersonal(long id, long user, int[] roles);


    List<Activity> findAllByTeamId(long teamId);


    /**
     * Checks whether the given user was the person who created the given activity
     * @param id   the id of the activity
     * @param user the id of user to check
     * @return a list of activities
     */
    @Query(value = "SELECT * FROM tab_activities " +
            " WHERE id = :id " +
            " AND user_id = :user "
            , nativeQuery = true)
    Activity findByCreatorUserId(long id, long user);
}

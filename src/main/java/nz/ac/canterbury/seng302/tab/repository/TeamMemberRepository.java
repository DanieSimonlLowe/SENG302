package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.*;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * TeamMemberRepository repository accessor using Spring's @link{CrudRepository}.
 * Repository to query data from the database
 */
public interface TeamMemberRepository extends CrudRepository<TeamMember, TeamMemberId> {

    /**
     * Gets a list of team members from a given team
     * @param team the team to find team members of
     * @return the list of team members
     */
    List<TeamMember> findAllByTeamMemberId_Team(Team team);

    /**
     * Gets a list of team members from a given team that have the given role
     * @param team the team to find team members of
     * @param role the role the team members must have
     * @return the list of team members
     */
    List<TeamMember> findAllByTeamMemberId_TeamAndRole(Team team, Role role);

    /**
     * Gets a list of team members from a given user
     * @param user the user to find team members of
     * @return the list of team members
     */
    List<TeamMember> findAllByTeamMemberId_User(User user);

    /**
     * deletes all teams in database
     * only for testing purposes.
     * */
    void deleteAll();
}

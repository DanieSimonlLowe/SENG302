package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.exceptions.InvalidTeamException;
import nz.ac.canterbury.seng302.tab.exceptions.NotFoundException;
import nz.ac.canterbury.seng302.tab.repository.TeamMemberRepository;
import nz.ac.canterbury.seng302.tab.service.checkers.TeamValidityChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TeamMemberService {
    private final TeamInvitationTokenService teamInvitationTokenService;

    private final UserService userService;

    private final TeamMemberRepository teamMemberRepository;

    private final TeamValidityChecker teamValidityChecker = new TeamValidityChecker(this);

    @Autowired
    public TeamMemberService(TeamInvitationTokenService teamInvitationTokenService,
                             UserService userService,
                             TeamMemberRepository teamMemberRepository) {
        this.teamInvitationTokenService = teamInvitationTokenService;
        this.userService = userService;
        this.teamMemberRepository = teamMemberRepository;
    }

    /**
     * Adds a teamMember to persistence, if the team doesn't exist or the user is already a member a null object is returned
     * @param user object to create team member with
     * @param token the token for the team to join
     * @return the saved teamMember object or null if the team doesn't exist or the user is already a member
     */
    public TeamMember addTeamMember(User user, String token) {
        Optional<Team> team = teamInvitationTokenService.findByToken(token);
        if(team.isPresent() && teamMemberRepository.existsById(new TeamMemberId(user, team.get()))) { return null; }
        return team.map(value -> teamMemberRepository.save(new TeamMember(user, value, Role.MEMBER))).orElse(null);
    }

    /**
     * Changes the role of a given teamMember that is in the database
     * @param teamMember the teamMember to change the role of
     * @param role the role to change the teamMember to
     * @return the updated teamMember object
     */
    public TeamMember changeRole(TeamMember teamMember, Role role) throws NotFoundException, InvalidTeamException {
        Team team = teamMember.getTeamMemberId().getTeam();
        Optional<TeamMember> teamMemberFromDatabase = getTeamMember(teamMember.getTeamMemberId());
        if (teamMemberFromDatabase.isEmpty()) { throw new NotFoundException(); }
        else if (teamMemberFromDatabase.get().getRole() == Role.MANAGER && role != Role.MANAGER && !teamValidityChecker.canRemoveManager(team)) { throw  new InvalidTeamException("At least one manager is required"); }
        else if (teamMemberFromDatabase.get().getRole() != Role.MANAGER && role == Role.MANAGER && !teamValidityChecker.canAddManager(teamMember.getTeamMemberId().getTeam())) { throw new InvalidTeamException("No more than three managers are allowed"); }
        else return (teamMemberRepository.save(new TeamMember(
                teamMember.getTeamMemberId().getUser(),
                teamMember.getTeamMemberId().getTeam(),
                role)));
    }

    /**
     * Gets a teamMember from persistence
     * @param id of team to retrieve
     * @return teamMember with matching id
     */
    public Optional<TeamMember> getTeamMember(TeamMemberId id) { return teamMemberRepository.findById(id); }

    /**
     * Gets all the teamMembers of a particular team from persistence
     * @param team the team to get members from
     * @return list of teamMembers of the team
     */
    public List<TeamMember> getAllTeamMembersFromTeam(Team team) { return teamMemberRepository.findAllByTeamMemberId_Team(team);}

    /**
     * Gets all the managers of a particular team from persistence
     * @param team the team to get the managers from
     * @return list of teamMembers that are managers of the team
     */
    public List<TeamMember> getAllManagersFromTeam(Team team) {
        return teamMemberRepository.findAllByTeamMemberId_TeamAndRole(team, Role.MANAGER);
    }

    /**
     * Gets all the coaches of a particular team from persistence
     * @param team the team to get the coaches from
     * @return list of teamMembers that are coaches of the team
     */
    public List<TeamMember> getAllCoachesFromTeam(Team team) {
        return teamMemberRepository.findAllByTeamMemberId_TeamAndRole(team, Role.COACH);
    }

    /**
     * Gets all the members of a particular team from persistence
     * @param team the team to get the members from
     * @return list of teamMembers that are members of the team
     */
    public List<TeamMember> getAllMembersFromTeam(Team team) {
        return teamMemberRepository.findAllByTeamMemberId_TeamAndRole(team, Role.MEMBER);
    }

    /**
     * Gets the team members from a given list of user ids and a team
     * @param userIds the user ids of team members to get
     * @param team the team to get the team members from
     * @return list of teamMembers that have the given userIds and belong to the given team
     * @throws NotFoundException when the teamMember is not found
     */
    public List<TeamMember> getTeamMembersFromUserIdsAndTeam(List<Long> userIds, Team team) throws NotFoundException {
        List<TeamMember> teamMembers = new ArrayList<>();
        for (Long userId : userIds) {
            User user = userService.getUser(userId).orElseThrow(NotFoundException::new);
            TeamMemberId teamMemberId = new TeamMemberId(user, team);
            TeamMember teamMember = teamMemberRepository.findById(teamMemberId).orElseThrow(NotFoundException::new);
            teamMembers.add(teamMember);
        }
        return teamMembers;
    }

    /**
     * Updates the team member roles of a list of team members
     * @param teamMembers a list of team members whose role to update
     * @param role the role to update the team members to
     * @return the updated list of team members
     * @throws InvalidTeamException if the given list of teamMembers results in an invalid team
     * @throws NotFoundException if a teamMember is not found in the repository
     */
    public List<TeamMember> updateTeamMemberRoles(List<TeamMember> teamMembers, Role role) throws InvalidTeamException, NotFoundException {
        List<TeamMember> updatedTeamMembers = new ArrayList<>();
        if (role == Role.MANAGER) { teamValidityChecker.isValidManagerList(teamMembers); }
        for (TeamMember teamMember : teamMembers) { updatedTeamMembers.add(changeRole(teamMember, role)); }
        return updatedTeamMembers;
    }

    /**
     * Gets a list of team members from a given user
     * @param user the user to find team members of
     * @return the list of team members
     */
    public List<TeamMember> getAllTeamsFromUser(User user) {
        return teamMemberRepository.findAllByTeamMemberId_User(user);
    }

    public String getRoleStringFromRoleNum(int roleNum) {
        return switch (roleNum) {
            case (0) -> "Manager";
            case (1) -> "Coach";
            case (2) -> "Member";
            default -> "None";
        };
    }
}

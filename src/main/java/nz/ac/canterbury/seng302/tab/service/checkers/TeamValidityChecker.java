package nz.ac.canterbury.seng302.tab.service.checkers;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.TeamMember;
import nz.ac.canterbury.seng302.tab.exceptions.InvalidTeamException;
import nz.ac.canterbury.seng302.tab.service.TeamMemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TeamValidityChecker {
    static private int MAX_STR_LEN = 500;

    /**
     * Logger used for debugging
     */
    static final Logger logger = LoggerFactory.getLogger(TeamValidityChecker.class);
    final TeamMemberService memberService;

    public TeamValidityChecker(TeamMemberService memberService) {
        this.memberService = memberService;
    }


    /**
     * @param value the team name being checked.
     * @return if the name is valid or not.
     * */
    public static boolean isValidName(String value) {
        return value.length() < MAX_STR_LEN && value.matches("^(?!\\s)(?=.*[\\p{L}\\d])[\\d\\p{L}.{}\\s]+$");
    }

    /**
     * @param value the team sport being checked.
     * @return if the sport is valid or not.
     * */
    public static boolean isValidSport(String value) {
        return value.length() < MAX_STR_LEN && value.matches("^(?=.*\\p{L})[\\p{L}'\\-\\s]+$");
    }

    /**
     * Checks if the current team can have another manager
     * @param team the current team
     * @return true if manager can be added, false otherwise
     */
    public boolean canAddManager(Team team) {
        List<TeamMember> managers = memberService.getAllManagersFromTeam(team);
        return managers.size() < 3;
    }

    /**
     * Checks if manager can be removed from a team
     * @return true if manager can be removed, false otherwise
     */
    public boolean canRemoveManager(Team team) {
        List<TeamMember> managers = memberService.getAllManagersFromTeam(team);
        return managers.size() != 1;
    }

    /**
     * Checks whether a given list of managers is valid
     * @param managers the list of managers to check
     * @throws InvalidTeamException if the list of managers results in an invalid team
     */
    public void isValidManagerList(List<TeamMember> managers) throws InvalidTeamException {
        if (managers.size() < 1) { throw new InvalidTeamException("At least one manager is required"); }
        else if (managers.size() > 3) { throw new InvalidTeamException("No more than three managers are allowed"); }
    }
}


package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.annotation.Resource;
import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.exceptions.InvalidTeamException;
import nz.ac.canterbury.seng302.tab.exceptions.NotFoundException;
import nz.ac.canterbury.seng302.tab.service.LocationService;
import nz.ac.canterbury.seng302.tab.service.TeamMemberService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
public class EditTeamMemberRolesFeature {

    @Resource
    private TeamMemberService teamMemberService;
    @Resource
    private TeamService teamService;

    @Resource
    private UserService userService;

    @Resource
    private LocationService locationService;
    private Team team;
    private TeamMember morganTeamMember;
    private TeamMember carysTeamMember;
    private TeamMember nadineTeamMember;
    private TeamMember patrickTeamMember;
    private Exception exception;
    private static final String PASSWORD_HASH = "!2a$04$fvrk2c2qoLNtMbGFGsBLBO/BAUvCQxmmHeJyaesK5szZ6oqU0Q6Jq";
    private static final String EMAIL = "morgan.english@hotmail.com";

    @Given("I update the roles of team members")
    public void i_update_the_roles_of_team_members() throws NotFoundException, InvalidTeamException {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(EMAIL, null));
        Location location = locationService.addLocation(new Location("", "", "", "", "Portland", "United States of America"));

        team = teamService.addTeam(new Team("My Team", location, "Basketball"));
        User morgan = userService.getUserByEmail(EMAIL);
        User andre = new User("andre@email.com", "Andre", "Carroll", LocalDate.of(2002, 10, 21), location, PASSWORD_HASH);
        User carys = new User("carys@email.com", "Carys", "Harvey", LocalDate.of(2002, 10, 21), location, PASSWORD_HASH);
        User nadine = new User("nadine@email.com", "Nadine", "Ward", LocalDate.of(2002, 10, 21), location, PASSWORD_HASH);
        User patrick = new User("patrick@email.com", "Patrick", "Harris", LocalDate.of(2002, 10, 21), location, PASSWORD_HASH);

        andre = userService.save(andre);
        carys = userService.save(carys);
        nadine = userService.save(nadine);
        patrick = userService.save(patrick);

        morganTeamMember = teamMemberService.getTeamMember(new TeamMemberId(morgan, team)).orElseThrow(NotFoundException::new);
        TeamMember andreTeamMember = teamMemberService.addTeamMember(andre, team.getTeamToken());
        carysTeamMember = teamMemberService.addTeamMember(carys, team.getTeamToken());
        nadineTeamMember = teamMemberService.addTeamMember(nadine, team.getTeamToken());
        patrickTeamMember = teamMemberService.addTeamMember(patrick, team.getTeamToken());

        teamMemberService.changeRole(andreTeamMember, Role.COACH);
        teamMemberService.changeRole(carysTeamMember, Role.COACH);

        Assertions.assertEquals(5, teamMemberService.getAllTeamMembersFromTeam(team).size());
        Assertions.assertEquals(1, teamMemberService.getAllManagersFromTeam(team).size());
        Assertions.assertEquals(2, teamMemberService.getAllCoachesFromTeam(team).size());
        Assertions.assertEquals(2, teamMemberService.getAllMembersFromTeam(team).size());
    }

    @When("I don't have any managers anymore")
    public void i_don_t_have_any_managers_anymore() {
        exception = Assertions.assertThrows(InvalidTeamException.class, () -> teamMemberService.changeRole(morganTeamMember, Role.MEMBER));
    }

    @Then("An error message tells me that I {string}")
    public void an_error_message_tells_me_that_i(String errorMessage) {
        Assertions.assertEquals(errorMessage, exception.getMessage());
    }

    @Then("The changes are not saved")
    public void the_changes_are_not_saved() {
        List<Long> managerIds = teamMemberService.getAllManagersFromTeam(team).stream().map(manager -> manager.getTeamMemberId().getUser().getId()).toList();
        Assertions.assertTrue(managerIds.contains(morganTeamMember.getTeamMemberId().getUser().getId()));
        Assertions.assertFalse(managerIds.contains(patrickTeamMember.getTeamMemberId().getUser().getId()));
    }


    @When("I have more than three managers for the team")
    public void i_have_more_than_three_managers_for_the_team() throws NotFoundException, InvalidTeamException {
        teamMemberService.changeRole(carysTeamMember, Role.MANAGER);
        teamMemberService.changeRole(nadineTeamMember, Role.MANAGER);
        exception = Assertions.assertThrows(InvalidTeamException.class, () -> teamMemberService.changeRole(patrickTeamMember, Role.MANAGER));
    }
}

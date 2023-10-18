package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.annotation.Resource;
import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.service.*;
import org.junit.jupiter.api.Assertions;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
public class JoinTeamFeature {
    @Resource
    TeamService teamService;

    @Resource
    LocationService locationService;

    @Resource
    TeamMemberService teamMemberService;

    @Resource
    UserService userService;


    private TeamMember teamMember;

    private User user;

    private Team team;

    private Location christchurch;

    private static final String PASSWORD_HASH = "!2a$04$fvrk2c2qoLNtMbGFGsBLBO/BAUvCQxmmHeJyaesK5szZ6oqU0Q6Jq";

    @Before
    public void setup() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("morgan.english@hotmail.com", null));
        christchurch =  new Location("", "", "", "", "Christchurch", "New Zealand");
        Long locationId = locationService.addLocation(christchurch).getId();
        Assertions.assertNotNull(locationService.getLocation(locationId));
    }

    @Given("A user with the first name {string}, last name {string} and email {string} exists")
    public void a_user_with_the_first_name_last_name_and_email_exists(String firstName, String lastName, String email) {
        user = new User(firstName, lastName, email, LocalDate.of(2002,10,21), christchurch, PASSWORD_HASH);
        Long userId = userService.save(user).getId();
        Assertions.assertTrue(userService.getUser(userId).isPresent());
    }

    @Given("There is a team called {string}, with city {string}, country {string} and sport {string}")
    public void there_is_a_team_called_with_city_country_and_sport(String teamName, String city, String country, String sport) {
        Location location = new Location("", "", "", "", city, country);
        team = new Team(teamName, location, sport);
        Long locationId = locationService.addLocation(location).getId();
        Long teamId = teamService.addTeam(team).getId();
        Assertions.assertNotNull(teamService.getTeam(teamId));
        Assertions.assertNotNull(locationService.getLocation(locationId));
    }


    @When("I input an invitation token associated with the team")
    public void i_input_an_invitation_token_associated_with_the_team() {
        teamMember = teamMemberService.addTeamMember(user, team.getTeamToken());
        Assertions.assertNotNull(teamMember.getTeamMemberId());
    }

    @Then("I am a member of the team")
    public void i_am_a_member_of_the_team() {
        boolean found = false;
        for (TeamMember member : teamMemberService.getAllTeamMembersFromTeam(team)) {
            if (member.getTeamMemberId().getUser().getId().equals(user.getId())) {
                found = true;
                Assertions.assertEquals(Role.MEMBER, member.getRole());
                Assertions.assertEquals(teamMember.getTeamMemberId(), member.getTeamMemberId());
            }
        }
        Assertions.assertTrue(found);
    }

    @When("I input an invitation token not associated with any team")
    public void i_input_an_invitation_token_not_associated_with_any_team() {
        teamMember = teamMemberService.addTeamMember(user, "NONEXISTENCE");
        Assertions.assertNull(teamMember);
    }

    @Then("I am a not a member of a team")
    public void i_am_a_not_a_member_of_a_team() {
        Assertions.assertEquals(0, teamMemberService.getAllTeamMembersFromTeam(team).size());
    }
}

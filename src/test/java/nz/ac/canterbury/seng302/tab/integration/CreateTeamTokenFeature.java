package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.annotation.Resource;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.service.LocationService;
import nz.ac.canterbury.seng302.tab.service.TeamInvitationTokenService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CreateTeamTokenFeature {
    @Resource
    private TeamService teamService;
    @Resource
    private LocationService locationService;

    @Resource
    private TeamInvitationTokenService teamInvitationTokenService;

    Team team;
    String teamToken;
    final Location location = new Location("", "", "", "", "Portland", "United States of America");
    final String oldToken = "OLD_TOKEN";

    @Before
    public void setUp() {
        locationService.addLocation(location);
        Assertions.assertNotNull(locationService.getLocation(location.getId()));
    }

    @Given("There is a team called {string} that I manage")
    public void there_is_a_team_called_that_i_manage(String teamName) {
        team = new Team(teamName, location, "SportName");
        teamService.addTeam(team);
        Assertions.assertNotNull(teamService.getTeam(team.getId()));
    }

    @When("I retrieve the token for my team")
    public void i_retrieve_the_token_for_my_team() {
        teamToken = teamService.getTeam(team.getId()).orElseThrow().getTeamToken();
        Assertions.assertNotNull(teamToken);
    }

    @Then("I can see a valid unique token for my team")
    public void i_can_see_a_valid_unique_token_for_my_team() {
        Assertions.assertTrue(teamToken.matches("^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]{12}$"));
    }


    @Given("A team I manage has a token")
    public void a_team_i_manage_has_a_token() {
        team = new Team("My Team", location, "SportName");
        team.setToken(oldToken);
        teamService.addTeam(team);
        Assertions.assertNotNull(teamService.getTeam(team.getId()));
        Assertions.assertEquals(oldToken, teamService.getTeam(team.getId()).orElseThrow().getTeamToken());
    }

    @When("I generate a new token for my team")
    public void i_generate_a_new_token_for_my_team() {
        teamInvitationTokenService.updateTeamToken(team);
        Assertions.assertNotNull(teamInvitationTokenService.findByToken(team.getTeamToken()));
    }

    @Then("The new token doesn't match the old token")
    public void the_new_token_doesnt_match_the_old_token() {
        Assertions.assertNotEquals(oldToken, team.getTeamToken());
    }
}

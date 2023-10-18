package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.service.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
@AutoConfigureMockMvc
@SpringBootTest
public class ViewActivityStatsFeature {
    @Autowired
    private LocationService locationService;
    @Autowired
    private FormationService formationService;
    @Autowired
    private TeamMemberService teamMemberService;
    @Autowired
    private UserService userService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private LineupService lineupService;
    @Autowired
    private LineupPlayerService lineupPlayerService;
    private Location location;
    private Team team;
    private User user1;
    private User user2;
    private User user3;
    private String formationString;
    private Formation formation;
    private Activity activity;
    private LineUp lineup;
    private final String sportString = "rugby";
    private MvcResult response;
    private JSONObject jsonResponse;
    @Autowired
    private MockMvc mockMvc;

    @Given("Tom is logged in")
    public void tom_is_logged_in() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("tom.barthelmeh@hotmail.com", null));
    }

    @Given("I have a team that has a formation {string}")
    public void i_have_a_team_that_has_a_formation(String formationStr) {
        formationString = formationStr;

        location = new Location("address1", "", "suburb", "1234", "city", "country");
        locationService.addLocation(location);
        team = new Team("Test Team Alpha Beta", location, sportString);

        team = teamService.addTeam(team);

        // Populate the team
        user1 = new User("JerrysEmail@email.com", "Gerald", "Jinx Mouse", LocalDate.now(), location, "IL!keCheese123");
        user2 = new User("TomsEmail@email.com", "Thomas", "Jasper Cat, Sr.", LocalDate.now(), location, "IL!keMice123");
        user3 = new User("WileECoyote@LooneyTones.com", "Wile E.", "Coyote", LocalDate.now(), location, "ILikeTheRo@dRunner123");

        user1 = userService.save(user1);
        user2 = userService.save(user2);
        user3 = userService.save(user3);

        teamMemberService.addTeamMember(user1, team.getTeamToken());
        teamMemberService.addTeamMember(user2, team.getTeamToken());
        teamMemberService.addTeamMember(user3, team.getTeamToken());

        // Create the formation
        formation = new Formation(formationStr, "rugby_pitch", team);
        formationService.save(formation);
        team.addFormation(formation);

        Assertions.assertEquals(4, teamMemberService.getAllTeamMembersFromTeam(team).size());
        Assertions.assertEquals(1, teamMemberService.getAllManagersFromTeam(team).size());
        Assertions.assertNotNull(formationService.getFormationsByTeamId(team.getId()));
    }


    @Given("I have an activity with a lineup")
    public void i_have_an_activity_with_a_lineup() {
        // Opposition team
        Team oppo = new Team("Opposition", location, sportString);
        oppo = teamService.addTeam(oppo);

        ActivityType type = ActivityType.GAME;

        // Create a game activity
        activity = new Activity(type, team, oppo, "A friendly game", LocalDateTime.now(), LocalDateTime.now().plusHours(1), user3, location);
        activityService.save(activity);

        // Create the lineup
        lineup = new LineUp(formation, activity);
        lineupService.save(lineup);

        LineupPlayer lineupPlayer1 = new LineupPlayer(user1, lineup, 0);
        LineupPlayer lineupPlayer2 = new LineupPlayer(user2, lineup, 1);
        LineupPlayer lineupPlayer3 = new LineupPlayer(user3, lineup, 2);

        lineupPlayerService.save(lineupPlayer1);
        lineupPlayerService.save(lineupPlayer2);
        lineupPlayerService.save(lineupPlayer3);

        Assertions.assertEquals(1, activityService.getActivitiesByTeamId(team.getId()).size());
        Assertions.assertNotNull(lineupService.getLineupByActivityId(activity.getId()));
    }

    @When("I view the lineup on the activity statistics")
    public void i_view_the_lineup_on_the_activity_statistics() throws Exception {

        response = mockMvc.perform(get("/activities/getLineup/" + activity.getId())).andReturn();
        MockHttpServletResponse result = response.getResponse();
        jsonResponse  = new JSONObject(result.getContentAsString());
        Assertions.assertEquals(200, result.getStatus());
    }

    @Then("I get the lineup")
    public void i_get_the_lineup() throws JSONException {
        Assertions.assertEquals(formationString, jsonResponse.get("formationString"));
    }
}

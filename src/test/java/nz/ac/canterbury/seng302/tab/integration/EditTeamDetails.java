package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.canterbury.seng302.tab.controller.ModerationAPIController;
import nz.ac.canterbury.seng302.tab.controller.TableController;
import nz.ac.canterbury.seng302.tab.controller.TeamFormController;
import nz.ac.canterbury.seng302.tab.controller.TeamMemberController;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.service.*;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.ui.Model;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;


public class EditTeamDetails {

    @Resource
    private TeamService teamService;

    @Resource
    private UserService userService;

    @Resource
    private ActivityService activityService;

    @Resource
    private LocationService locationService;

    @Resource
    private SportService sportService;

    @Resource
    private TableController tableController;

    @Resource
    private ClubService clubService;

    @Resource
    private TeamMemberController teamMemberController;

    @Resource
    private FeedPostService feedPostService;

    @Resource
    private CommentService commentService;

    @Resource
    private ModerationAPIController moderationAPIController;

    private TeamFormController teamFormController;

    private Team team;

    private Location location;


    @Given("I am logged in as Tom")
    public void i_am_logged_in_as_tom() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("tom.barthelmeh@hotmail.com", null));
    }

    @Given("I own a team")
    public void i_own_a_team() {
        location = new Location("", "", "", "", "Christchurch",  "NZ");
        locationService.addLocation(location);
        team = new Team("UnchangedTeamName", location, "UnchangedSportName");
        teamService.addTeam(team);
    }

    @When("I edit the team's details")
    public void i_edit_the_teams_details() {
        teamFormController = new TeamFormController(sportService, teamService, locationService, tableController, clubService, teamMemberController, userService, activityService, feedPostService, commentService, moderationAPIController);
        Model model = Mockito.mock(Model.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        teamFormController.submitForm("CHANGEDNAME", "", "", "", "", "Christchurch", "NZ", "CHANGEDSPORT", team.getId(), model, response);
    }

    @Then("The team's details are updated")
    public void the_teams_details_are_updated() {
        Optional<Team> updatedTeam = teamService.getTeam(team.getId());
        if (updatedTeam.isPresent()) {
            Assertions.assertEquals("CHANGEDNAME", updatedTeam.get().getName());
            Assertions.assertEquals("CHANGEDSPORT", updatedTeam.get().getSport());
        }
    }
}

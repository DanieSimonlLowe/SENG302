package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.tab.controller.ActivityController;
import nz.ac.canterbury.seng302.tab.controller.TableController;
import nz.ac.canterbury.seng302.tab.entity.Formation;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.service.*;
import org.junit.jupiter.api.Assertions;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@Transactional
public class EditLineup {


    @Resource
    private TeamService teamService;

    @Resource
    private UserService userService;

    @Resource
    private ActivityService activityService;

    @Resource
    private LocationService locationService;

    @Resource
    private FormationService formationService;

    @Resource
    private LineupService lineupService;

    @Resource
    private LineupPlayerService lineupPlayerService;

    @Resource
    private FeedPostService feedPostService;

    private ActivityController activityController;

    private String validLineup;

    private Location location;

    private Team team;

    private String players;

    private String substitutions;

    private String formationStr;

    @Given("I am logged in as Tom and I have a team")
    public void i_am_logged_in_as_tom_and_I_have_a_team() {
        activityController = new ActivityController(teamService, userService, activityService, locationService, formationService, lineupService, lineupPlayerService, feedPostService);

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("tom.barthelmeh@hotmail.com", null));
        location = new Location("address1", "", "suburb", "1234", "city", "country");
        locationService.addLocation(location);
        team = new Team("String_name", location, "String_sport");
        teamService.addTeam(team);
    }

    @When("I provide a lineup with the player positions {string} and substitutions {string}")
    public void I_provide_a_lineup_with_the_player_positions_and_substitutions(String players, String substitutions) {
        this.players = players;
        this.substitutions = substitutions;
    }

    @When("I provide a formation string {string}")
    public void I_provide_a_formation_string(String formation) {
        this.formationStr = formation;
    }

    @When("I select a player for two different positions")
    public void i_select_a_player_for_two_different_positions() {
        Formation formation = new Formation(formationStr, "football_pitch", team);
        formationService.save(formation);
        team.addFormation(formation);
        validLineup = activityController.checkLineup(players, substitutions, formation.getId());
    }

    @Then("I am shown an error message saying that a player cannot be in two different positions")
    public void i_am_shown_an_error_message_saying_that_a_player_cannot_be_in_two_different_positions() {
        Assertions.assertEquals("Lineup could not be saved a player cannot be in more than one position.", validLineup);
    }

    @When("I do not fill in all the positions of the formation for the lineup")
    public void i_do_not_fill_in_all_the_positions_of_the_formation_for_the_lineup() {
        Formation formation = new Formation(formationStr, "football_pitch", team);
        formationService.save(formation);
        team.addFormation(formation);
        validLineup = activityController.checkLineup(players, substitutions, formation.getId());
    }

    @Then("I am shown an error message saying that all of the formation positions must be filled")
    public void i_am_shown_an_error_message_saying_that_all_of_the_formation_positions_must_be_filled() {
        Assertions.assertEquals("Lineup could not be saved as not all player slots have been filled.", validLineup);
    }
}

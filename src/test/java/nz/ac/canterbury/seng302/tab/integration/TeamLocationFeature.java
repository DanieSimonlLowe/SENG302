package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.annotation.Resource;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.TokenGenerator;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.*;
import nz.ac.canterbury.seng302.tab.service.checkers.RegistrationChecker;
import org.junit.jupiter.api.Assertions;

import java.time.LocalDate;

public class TeamLocationFeature {

    @Resource
    LocationService locationService;

    @Resource
    UserService userService;

    @Resource
    TeamService teamService;

    private Team team;
    private Long teamId;
    private User user;

    @Given("A team exists")
    public void user_exists() {
        LocalDate date1 = LocalDate.now();
        Location location = new Location("", "", "", "", "test", "test");
        locationService.addLocation(location);
        user = new TokenGenerator("e", "y", "l", date1, location, "pas", "register").generateUser();
        userService.save(user);
        team = new Team("name", location, "sport");
        Long locationId = locationService.addLocation(location).getId();
        teamId = teamService.addTeam(team).getId();
        Assertions.assertNotNull(teamService.getTeam(teamId));
        Assertions.assertNotNull(locationService.getLocation(locationId));
    }

    @When("The team location is edited")
    public void team_edit_location(){
        team.editingTeam(teamId, "name", "", "", "", "", "Auckland", "New Zealand", team.getSport());
    }

    @Then("The city value cannot be null")
    public void team_city_notNull() {
        Assertions.assertFalse(RegistrationChecker.isLocationValid(team.getAddressOne(),team.getAddressTwo(),team.getSuburb(),team.getPostcode(),"",team.getCountry()));
    }

    @Then("The country value cannot be null")
    public void team_country_notNull() {
        Assertions.assertFalse(RegistrationChecker.isLocationValid(team.getAddressOne(),team.getAddressTwo(),team.getSuburb(),team.getPostcode(), team.getCity(), ""));
    }

    @Then("The address values can be null")
    public void team_address_null() {
        Assertions.assertTrue(RegistrationChecker.isLocationValid("","",team.getSuburb(),team.getPostcode(),team.getCity(),team.getCountry()));
    }

    @Then("The suburb can be null")
    public void team_suburb_null() {
        Assertions.assertTrue(RegistrationChecker.isLocationValid(team.getAddressOne(),team.getAddressTwo(),"",team.getPostcode(),team.getCity(),team.getCountry()));
    }

    @Then("The postcode can be null")
    public void team_postcode_null() {
        Assertions.assertTrue(RegistrationChecker.isLocationValid(team.getAddressOne(),team.getAddressTwo(),team.getSuburb(), "",team.getCity(),team.getCountry()));

    }

}

package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.LocationRepository;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@Transactional
public class FollowedTeams {

    @Resource
    private UserRepository userRepository;

    @Resource
    private LocationRepository locationRepository;

    @Resource
    private TeamRepository teamRepository;

    @Resource
    private UserService userService;
    @Resource
    private TeamService teamService;

    final Location location = new Location("", "", "", "", "Portland", "United States of America");
    final User user = new User("email", "firstName", "lastName", LocalDate.now(), location, "passwordHash");

    final Team team = new Team("teamName", location, "sportName");


    @Given("I have a user Sam")
    public void i_have_a_user_sam() {
        locationRepository.save(location);
        userRepository.save(user);
        teamRepository.save(team);
    }

    @When("I follow a team")
    public void i_follow_a_team() {
        user.followTeam(team);
        userService.save(user);
    }

    @Then("The team is added to my list of followed teams")
    public void the_team_is_added_to_my_list_of_followed_teams() {
        List<Team> followedTeams = teamService.getFollowedTeams(user);
        Assertions.assertTrue(1 <= followedTeams.size());
        Assertions.assertEquals(team.getId(), followedTeams.get(0).getId());
    }
}

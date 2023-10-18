package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.annotation.Resource;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.LocationRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
public class FollowProfiles {
    @Resource
    private UserService userService;

    @Resource
    private UserRepository userRepository;

    @Resource
    private LocationRepository locationRepository;

    Location location = new Location("", "", "", "", "test", "test");

    private final User tom = new User("tom@mail.com", "Tom", "Barthelmeh", LocalDate.now(), location, "password");

    private final User nathan = new User("nathan@mail.com", "Nathan", "Harper", LocalDate.now(), location, "password");

    @Given("I have a user Tom and a user Nathan")
    public void i_have_a_user_tom_and_a_user_nathan() {
        locationRepository.save(location);
        userRepository.save(tom);
        userRepository.save(nathan);
    }

    @When("Tom follows Nathan")
    public void tom_follows_nathan() {
        tom.follow(nathan);
        assert (tom.isFollowing(nathan));
    }

    @When("Nathan follows Tom")
    public void nathan_follows_tom() {
        nathan.follow(tom);
        assert (nathan.isFollowing(tom));
    }

    @Then("Tom and Nathan are friends")
    public void tom_and_nathan_are_friends() {
        assert (userService.areFriends(tom, nathan));
    }
}

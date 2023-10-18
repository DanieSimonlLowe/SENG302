package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.annotation.Resource;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.TokenGenerator;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.LocationService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;


@SpringBootTest
public class UserSearchByNameFeature {

    @Resource
    UserService userService;

    @Resource
    LocationService locationService;
    User user;


    @Given("A given user exists")
    public void userExists() {
        LocalDate date1 = LocalDate.now();
        Location location = new Location("", "", "", "", "test", "test");
        locationService.addLocation(location);
        user = new TokenGenerator("e", "n", "l", date1, location, "pas", "register").generateUser();
        userService.save(user);
    }

    @When("The user's name has changed")
    public void nameChanged() {
        user.setFirstName("dum");
        userService.save(user);
    }

    @Then("I can find user by new name")
    public void canFindByNewName() {
        List<User> userList = userService.findByFirstOrLastName("dum");
        boolean hasUser = false;
        for (User user1 : userList) {
            if (Objects.equals(user1.getId(), user.getId())) {
                hasUser = true;
                break;
            }
        }
        Assertions.assertTrue(hasUser);
    }
}

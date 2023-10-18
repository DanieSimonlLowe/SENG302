package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.annotation.Resource;
import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.service.*;
import nz.ac.canterbury.seng302.tab.service.checkers.RegistrationChecker;
import org.junit.jupiter.api.Assertions;

import java.time.LocalDate;

public class UserLocationFeature {

    @Resource
    LocationService locationService;

    @Resource
    UserService userService;


    private User user;




    @Given("A user exists")
    public void user_exists() {
        LocalDate date1 = LocalDate.now();
        Location location = new Location("", "", "", "", "test", "test");
        locationService.addLocation(location);
        user = new TokenGenerator("e", "y", "l", date1, location, "pas", "register").generateUser();
        userService.save(user);
    }

    @When("The user location is edited")
    public void user_edit_location() {
        user.editLocation("","","", "", "Auckland", "New Zealand");
        userService.save(user);
    }

    @Then("The user city value cannot be null")
    public void user_city_notNull() {
        Assertions.assertFalse(RegistrationChecker.isLocationValid(user.getAddressOne(), user.getAddressTwo(), user.getSuburb(), user.getPostcode(), "", user.getCountry()));
    }

    @Then("The user country value cannot be null")
    public void user_country_notNull() {
        Assertions.assertFalse(RegistrationChecker.isLocationValid(user.getAddressOne(), user.getAddressTwo(), user.getSuburb(), user.getPostcode(), user.getCity(), ""));
    }

    @Then ("The user address values can be null")
    public void user_address_null() {
        Assertions.assertTrue(RegistrationChecker.isLocationValid("", "", user.getSuburb(), user.getPostcode(), user.getCity(), user.getCountry()));

    }

    @Then("The user suburb can be null")
    public void user_suburb_null() {
        Assertions.assertTrue(RegistrationChecker.isLocationValid(user.getAddressOne(), user.getAddressTwo(), "", user.getPostcode(), user.getCity(), user.getCountry()));
    }

    @Then("The user postcode can be null")
    public void user_postcode_null() {
        Assertions.assertTrue(RegistrationChecker.isLocationValid(user.getAddressOne(), user.getAddressTwo(), user.getSuburb(), "", user.getCity(), user.getCountry()));
    }

}

package nz.ac.canterbury.seng302.tab.end2end;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class SportsInterestsFeature {

    @When("I add my sport {string}")
    public void i_add_my_sport(String string) {
        PlaywrightBrowser.page.locator("#favSport").fill(string);
        PlaywrightBrowser.page.locator("#saveSport").click();
    }

    @Then("I can see the sport on the page")
    public void i_can_see_the_sport_on_the_page() {
        List<String> sports = List.of(PlaywrightBrowser.page.locator("#favSportsList").inputValue().split(","));
        Assertions.assertTrue(sports.contains(("Karate")));
    }

    @When("I add my sport {string} and {string}")
    public void i_add_my_sport_and(String string, String string2) {
        PlaywrightBrowser.page.locator("#favSport").fill(string);
        PlaywrightBrowser.page.locator("#saveSport").click();
        PlaywrightBrowser.page.locator("#favSport").fill(string2);
        PlaywrightBrowser.page.locator("#saveSport").click();
    }

    @Then("I can see the sports on the page")
    public void i_can_see_the_sports_on_the_page() {
        List<String> sports = List.of(PlaywrightBrowser.page.locator("#favSportsList").inputValue().split(","));
        Assertions.assertTrue(sports.contains(("Karate")));
        Assertions.assertTrue(sports.contains(("Sport")));
    }

    @Given("There is a sport {string} on my page already")
    public void there_is_a_sport_on_my_page_already(String string) {
        PlaywrightBrowser.page.locator("#favSport").fill(string);
        PlaywrightBrowser.page.locator("#saveSport").click();
    }

    @When("I delete the sport {string}")
    public void i_delete_the_sport(String string) {
        PlaywrightBrowser.page.locator("#delete" + string).click();
    }

    @Then("The sport {string} is not there")
    public void the_sport_is_not_there(String string) {
        Assertions.assertFalse(PlaywrightBrowser.page.locator("#favSport").inputValue().contains((string)));
    }

    @When("I add the {string}")
    public void i_add_the(String string) {
        PlaywrightBrowser.page.locator("#favSport").fill(string);
        PlaywrightBrowser.page.locator("#saveSport").click();
    }

    @Then("an error {string} is shown")
    public void an_error_is_shown(String string) {
        Assertions.assertEquals(string, PlaywrightBrowser.page.locator("#invalidSport").textContent());
    }
}

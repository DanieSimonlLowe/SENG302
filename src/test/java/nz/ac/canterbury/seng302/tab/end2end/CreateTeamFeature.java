package nz.ac.canterbury.seng302.tab.end2end;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class CreateTeamFeature {

    @When("I enter a name {string}")
    public void i_enter_a_name(String teamName) {
        PlaywrightBrowser.page.locator("#teamName").fill(teamName);
    }

    @When("I enter an address1 {string}")
    public void i_enter_an_address1(String address1) {
        PlaywrightBrowser.page.locator("#form-address1").fill(address1);
    }

    @When("I enter an address2 {string}")
    public void i_enter_an_address2(String address2) {
        PlaywrightBrowser.page.locator("#form-address2").fill(address2);
    }

    @When("I enter a suburb {string}")
    public void i_enter_a_suburb(String suburb) {
        PlaywrightBrowser.page.locator("#form-suburb").fill(suburb);
    }

    @When("I enter a postcode {string}")
    public void i_enter_a_postcode(String postcode) {
        PlaywrightBrowser.page.locator("#form-postcode").fill(postcode);
    }

    @When("I enter a city {string}")
    public void i_enter_a_city(String city) {
        PlaywrightBrowser.page.locator("#form-city").fill(city);
    }

    @When("I enter a country {string}")
    public void i_enter_a_country(String country) {
        PlaywrightBrowser.page.locator("#form-country").fill(country);
    }

    @When("I enter a sport {string}")
    public void i_enter_a_sport(String sport) {
        PlaywrightBrowser.page.locator("#sport").fill(sport);
    }

    @Then("I am viewing my team profile")
    public void i_am_viewing_my_team_profile() {
        Assertions.assertTrue(PlaywrightBrowser.page.url().contains("/teamProfile"));
    }

    @Then("I am still on the page {string}")
    public void i_am_still_on_the_page(String page) {
        Assertions.assertTrue(PlaywrightBrowser.page.url().contains(page));
    }

}

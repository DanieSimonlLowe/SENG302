package nz.ac.canterbury.seng302.tab.end2end;

import com.microsoft.playwright.options.AriaRole;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class ViewTeamProfileFeature {
    private final String teamName = "New Team";
    private final String address1 = "802 Clarence St";
    private final String suburb = "Upper Riccarton";
    private final String postcode = "8041";
    private final String city = "Christchurch";
    private final String country = "New Zealand";
    private final String sport = "Basketball";

    @Given("I have a team in the database")
    public void i_have_a_team_in_the_database() {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/form");
        PlaywrightBrowser.page.locator("#teamName").fill(teamName);
        PlaywrightBrowser.page.locator("#form-address1").fill(address1);
        PlaywrightBrowser.page.locator("#form-suburb").fill(suburb);
        PlaywrightBrowser.page.locator("#form-postcode").fill(postcode);
        PlaywrightBrowser.page.locator("#form-city").fill(city);
        PlaywrightBrowser.page.locator("#form-country").fill(country);
        PlaywrightBrowser.page.locator("#sport").fill(sport);
        PlaywrightBrowser.page.locator("#submit").click();
    }

    @Given("I am on my team details page")
    public void i_am_on_my_team_details_page() {
        Assertions.assertTrue(PlaywrightBrowser.page.url().contains(PlaywrightBrowser.baseUrl + "/teamProfile"));
    }

    @When("I see all the details")
    public void i_see_all_the_details() {
        PlaywrightBrowser.page.waitForTimeout(50L);
        String name = PlaywrightBrowser.page.locator("#teamName").innerText();



        Assertions.assertEquals(teamName,name);

        String locationString = PlaywrightBrowser.page.locator("#locationString").innerText();
        Assertions.assertTrue(locationString.toLowerCase().contains(address1.toLowerCase()), "dose not contain address");
        Assertions.assertTrue(locationString.toLowerCase().contains(suburb.toLowerCase()) , "dose not contain suburb");
        Assertions.assertTrue(locationString.toLowerCase().contains(postcode.toLowerCase()) , "dose not contain postcode");
        Assertions.assertTrue(locationString.toLowerCase().contains(city.toLowerCase()), "dose not contain city");
        Assertions.assertTrue(locationString.toLowerCase().contains(country.toLowerCase()), "dose not contain country");

        String sportString = PlaywrightBrowser.page.locator("#sport").innerText();
        Assertions.assertEquals(sport,sportString);

    }

    @Then("I cannot edit any of the details that are shown to me")
    public void i_cannot_edit_any_of_the_details_that_are_shown_to_me() {
        Assertions.assertFalse(PlaywrightBrowser.page.getByRole(AriaRole.FORM).isVisible());
    }

}

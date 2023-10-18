package nz.ac.canterbury.seng302.tab.end2end;

import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class ViewUserProfileFeature {

    @When("I add a favourite sport {string}")
    public void i_add_a_favourite_sport(String favSport) {
        // Am on the profile page
        // Click the edit button
        PlaywrightBrowser.page.locator("#edit-btn").click();
        // Add a sport and save it
        PlaywrightBrowser.page.locator("#favSport").fill(favSport);
        PlaywrightBrowser.page.locator("#saveSport").click();
    }

    @Then("I can see the sport {string} on my profile")
    public void I_can_see_the_sport_on_my_profile(String checkedSport) {
        PlaywrightBrowser.page.locator("#sportsDisplay :text('"+checkedSport+"')").isVisible();
    }

    @Then("I can see all my details")
    public void i_can_see_all_my_details() {
        // Check that the date of birth is visible
        PlaywrightBrowser.page.locator("#dateOfBirthDisplay").isVisible();
        // Check that the email is visible
        PlaywrightBrowser.page.locator("#emailDisplay").isVisible();
        // Check that name is visible
        PlaywrightBrowser.page.locator("#fName").isVisible();

    }
}

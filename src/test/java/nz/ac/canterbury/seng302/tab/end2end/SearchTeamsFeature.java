package nz.ac.canterbury.seng302.tab.end2end;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class SearchTeamsFeature {

    @Given("I enter a search string {string}")
    public void i_enter_a_search_string(String searchQuery) {
        PlaywrightBrowser.page.waitForSelector("#search");
        PlaywrightBrowser.page.fill("#search", searchQuery);
    }

    @When("I hit the search button")
    public void i_hit_the_search_button() {
        PlaywrightBrowser.page.click("#searchButton");
    }

    @Then("The team {string} is shown to me in the list of results")
    public void the_team_is_shown_to_me_in_the_list_of_results(String teamName) {
        PlaywrightBrowser.page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        Assertions.assertTrue(PlaywrightBrowser.page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(teamName)).isVisible());
    }

    @Then("An error message tells me that my query is too short")
    public void an_error_message_tells_me_that_my_query_is_too_short() {
        PlaywrightBrowser.page.waitForSelector("#errorLabel");
        Assertions.assertTrue(PlaywrightBrowser.page.isVisible("#errorLabel"));
    }
}

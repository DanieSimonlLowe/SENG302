package nz.ac.canterbury.seng302.tab.end2end;

import com.microsoft.playwright.options.LoadState;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import static nz.ac.canterbury.seng302.tab.end2end.PlaywrightBrowser.page;

public class JoinTeamFeature {
    @Given("I am logged in as Natalie")
    public void login() {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl+"/login");
        PlaywrightBrowser.page.locator("#email").fill("n.weston@hotmail.com");
        PlaywrightBrowser.page.locator("#password").fill("Password1!");
        PlaywrightBrowser.page.locator("#submit").click();
        Assertions.assertTrue(PlaywrightBrowser.page.url().contains("/profile"));
    }

    @Given("I am on {string}")
    public void i_am_on(String page) {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + page);
    }

    @When("I hit the button to choose to join a new team")
    public void i_hit_the_button_to_choose_to_join_a_new_team() {
        PlaywrightBrowser.page.locator("#teamDropdownMenu").click();
        PlaywrightBrowser.page.locator("#joinTeam").click();
    }

    @Then("I am on the join a team page")
    public void i_am_on_the_join_a_team_page() {

        System.out.println(page.url());
        Assertions.assertTrue(PlaywrightBrowser.page.url().contains("/joinTeam"));
    }

    @When("I input an invitation token that is associated to a team in the system")
    public void i_input_an_invitation_token_that_is_associated_to_a_team_in_the_system() {
        PlaywrightBrowser.page.locator("#tokenInput").fill("DataBaseTestToken!123");
        PlaywrightBrowser.page.locator("#submit").click();
    }


    @Then("I am added as a member to this team")
    public void i_am_added_as_a_member_to_this_team() {
        PlaywrightBrowser.page.locator("#myProfile").click();
        String innerHTML = PlaywrightBrowser.page.locator("#teamList").innerHTML();
        Assertions.assertTrue(innerHTML.contains("Test Team"),"Can't find Test Team on profile.");
    }

    @When("I input an invitation token that is not associated to a team in the system")
    public void i_input_an_invitation_token_that_is_not_associated_to_a_team_in_the_system() {
        PlaywrightBrowser.page.locator("#tokenInput").fill("NotDataBaseTestToken!123");
        PlaywrightBrowser.page.locator("#submit").click();
    }


    @Then("an error message tells me the token is invalid")
    public void an_error_message_tells_me_the_token_is_invalid() {
        Assertions.assertFalse(PlaywrightBrowser.page.locator("#error").innerText().isEmpty());
    }

    @Given("I have joined a new team")
    public void i_have_joined_a_new_team() {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/joinTeam");
        PlaywrightBrowser.page.locator("#tokenInput").fill("NotDataBaseTestToken!123");
        PlaywrightBrowser.page.locator("#submit").click();
    }

    @When("I click on a dedicated UI element to see the teams I am a member of")
    public void i_click_on_a_dedicated_ui_element_to_see_the_teams_i_am_a_member_of() {
        PlaywrightBrowser.page.locator("#teamDropdownMenu").click();
        PlaywrightBrowser.page.locator("#myTeams").click();
    }

    @Then("I see the new team I just joined")
    public void i_see_the_new_team_i_just_joined() {
        PlaywrightBrowser.page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        Assertions.assertTrue(PlaywrightBrowser.page.getByText("Test Team").isVisible());
    }
}
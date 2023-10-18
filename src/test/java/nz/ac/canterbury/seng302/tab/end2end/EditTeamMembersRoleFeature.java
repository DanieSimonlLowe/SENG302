package nz.ac.canterbury.seng302.tab.end2end;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class EditTeamMembersRoleFeature {

    @Given("I am in the team profile page of a team I manage")
    public void i_see_all_team_members_with_their_roles() {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl+"/login");
        PlaywrightBrowser.page.locator("#email").fill("tom.barthelmeh@hotmail.com");
        PlaywrightBrowser.page.locator("#password").fill("Password1!");
        PlaywrightBrowser.page.locator("#submit").click();
        Assertions.assertTrue(PlaywrightBrowser.page.url().contains("/profile"));

        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl+"/teamProfile?id=1");
        Assertions.assertTrue(PlaywrightBrowser.page.url().contains("/teamProfile"));
    }

    @When("I click on a UI element to edit the members role")
    public void i_click_on_a_ui_element_to_edit_the_members_role() {
        PlaywrightBrowser.page.locator("#members-tab").click();
    }

    @Then("I see the list of members and their roles in the team")
    public void i_see_the_list_of_members_and_their_roles_in_the_team() {
        Assertions.assertTrue(PlaywrightBrowser.page.locator("#members").isVisible());
    }

    @Then("Their roles can be one of manger, coach, or member")
    public void their_roles_can_be_one_of_manager_coach_or_member() {
        Assertions.assertTrue(PlaywrightBrowser.page.locator("#managersList").isVisible());
        Assertions.assertTrue(PlaywrightBrowser.page.locator("#coachesList").isVisible());
        Assertions.assertTrue(PlaywrightBrowser.page.locator("#membersList").isVisible());
    }
}

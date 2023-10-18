package nz.ac.canterbury.seng302.tab.end2end;

import com.microsoft.playwright.options.LoadState;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import static nz.ac.canterbury.seng302.tab.end2end.PlaywrightBrowser.*;

public class CreateClubFeature {

    @Given("I am on the clubs edit page")
    public void I_am_on_the_clubs_edit_page() {
        PlaywrightBrowser.page.navigate(baseUrl+"/clubEdit?clubId=1");
    }

    @Given("I am on the create club page")
    public void i_am_on_the_create_club_page() {
        PlaywrightBrowser.page.navigate(baseUrl+"/clubForm");
    }

    @Given("I have entered valid club details")
    public void I_have_entered_valid_club_details() {
        PlaywrightBrowser.page.locator("#clubName").fill("name");
        PlaywrightBrowser.page.locator(".clubTeam").nth(0).check();
        PlaywrightBrowser.page.locator("#nextButton").click();
    }

    @Given("I am on the edit club page")
    public void i_am_on_the_edit_club_page() {
        PlaywrightBrowser.page.navigate(baseUrl+"/clubEdit?clubId=1");
    }

    @Given("I am on the club page")
    public void i_am_on_the_club_page() {
        PlaywrightBrowser.page.navigate(baseUrl+"/clubProfile?clubId=1");
    }

    @Given("I am on my clubs page")
    public void i_am_on_my_clubs_page() {
        PlaywrightBrowser.page.navigate(baseUrl+"/clubProfile?clubId=1");
    }

    @When("I change the name to {string}")
    public void i_change_the_name_to(String name) {
        PlaywrightBrowser.page.locator("#clubName").fill(name);
    }

    @When("I confirm the new changes")
    public void i_confirm_the_new_changes() {
        PlaywrightBrowser.page.locator("#nextButton").click();
        PlaywrightBrowser.page.locator("#nextButton").click();
//        PlaywrightBrowser.page.locator("#nextButton").click();
        PlaywrightBrowser.page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        Assertions.assertTrue(PlaywrightBrowser.page.url().contains("/clubProfile"));
    }

    @When("I click on the edit button")
    public void i_click_on_the_edit_button() {
        PlaywrightBrowser.page.locator("#edit-btn").click();
    }

    @When("I am anywhere on the system")
    public void when_i_am_anywhere_on_the_system() {
        PlaywrightBrowser.page.locator("#teamDropdownMenu").click();
        PlaywrightBrowser.page.locator("#createTeam").click();
    }

    @When("I enter valid name and team and location")
    public void i_enter_valid_name_and_team_and_location() {
        PlaywrightBrowser.page.locator("#clubName").fill("name");
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        PlaywrightBrowser.page.locator(".clubTeam").nth(0).check();
        PlaywrightBrowser.page.locator("#nextButton").click();
        PlaywrightBrowser.page.locator("#form-address1").fill("15 Hanrahan St");
        PlaywrightBrowser.page.locator("#form-postcode").fill("8041");
        PlaywrightBrowser.page.locator("#form-city").fill("Christchurch");
        PlaywrightBrowser.page.locator("#form-country").fill("New Zealand");
    }

    @When("I confirm the new club")
    public void i_confirm_the_new_club() {
        PlaywrightBrowser.page.locator("#nextButton").click();
    }

    @When("I click the corresponding UI element to create a club")
    public void i_click_th_corresponding_ui_element_to_create_a_club() {
        PlaywrightBrowser.page.locator("#clubsDropDownMenu").click();
        PlaywrightBrowser.page.locator("#clubForm").click();
    }

    @When("I enter invalid information {string}")
    public void I_enter_invalid_information(String name) {
        PlaywrightBrowser.page.locator("#clubName").fill(name);
    }

    @When("I enter an invalid location {string} {string} {string} {string}")
    public void I_enter_an_invalid_location(String city, String country, String address, String postcode) {
        PlaywrightBrowser.page.locator("#form-address1").fill(address);
        PlaywrightBrowser.page.locator("#form-postcode").fill(postcode);
        PlaywrightBrowser.page.locator("#form-city").fill(city);
        PlaywrightBrowser.page.locator("#form-country").fill(country);
    }


    @When("I confirm the changes")
    public void I_confirm_the_changes() {
        PlaywrightBrowser.page.locator("#nextButton").click();
    }

    @Then("I am shown a form where I can create a club")
    public void i_am_shown_a_form_where_i_can_create_a_club() {
        Assertions.assertTrue(PlaywrightBrowser.page.url().contains(baseUrl+"/clubForm"),PlaywrightBrowser.page.url());
    }

    @Then("An formation error message is displayed")
    public void An_error_message_is_displayed() {
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        String nameError = PlaywrightBrowser.page.locator("#clubNameError").innerText();
        String teamError = PlaywrightBrowser.page.locator("#clubTeamError").innerText();
        Assertions.assertEquals("Club name must only include letters, hyphens and apostrophes.",nameError);
        Assertions.assertEquals("A team is required.",teamError);

    }

    @Then("A club is created into the system and I see the details of the club")
    public void a_club_is_created_into_the_system_and_i_see_the_details_of_the_club() {
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        Assertions.assertTrue(PlaywrightBrowser.page.url().contains(baseUrl+"/clubProfile?clubId="),PlaywrightBrowser.page.url());
    }

    @Then("I see a form with the clubs information pre-populated")
    public void i_see_a_form_with_the_clubs_information_pre_populated() {
        Assertions.assertTrue(PlaywrightBrowser.page.url().contains(baseUrl+"/clubEdit?clubId=1"),PlaywrightBrowser.page.url());
    }

    @Then("The club page has been changed to {string} to reflect the new details")
    public void the_club_page_has_been_changed_to_reflect_the_new_details(String clubName) {
        Assertions.assertTrue(PlaywrightBrowser.page.url().contains("/clubProfile"));
        Assertions.assertEquals(clubName,page.locator("#clubName").innerText());
    }

    @Then("I don't see teams that are all ready in a club")
    public void iDonTSeeTeamsThatAreAllReadyInAClub() {
        Assertions.assertTrue(1 <= page.locator(".clubTeam").count());
    }

    @Then("An error message is displayed")
    public void an_error_message_is_displayed() {
        String nameError = PlaywrightBrowser.page.locator("#clubNameError").innerText();
        Assertions.assertEquals("Club name must only include letters, hyphens and apostrophes.",nameError);
    }
}

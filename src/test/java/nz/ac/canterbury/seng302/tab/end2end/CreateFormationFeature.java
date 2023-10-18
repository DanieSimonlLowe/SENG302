package nz.ac.canterbury.seng302.tab.end2end;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.LoadState;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static nz.ac.canterbury.seng302.tab.end2end.PlaywrightBrowser.baseUrl;
import static nz.ac.canterbury.seng302.tab.end2end.PlaywrightBrowser.page;

public class CreateFormationFeature {
    String input;

    @Given("I am on my team's profile")
    public void I_am_on_my_teams_profile() {
        page.navigate(baseUrl+"/teamProfile?id=2");
    }



    @Given("I am on a formation creation page")
    public void I_am_on_a_formation_creation_page() {
        page.navigate(baseUrl+"/formations?team=2");
    }

    @Given("I have a formation")
    public void i_have_a_formation() {
        page.navigate(baseUrl+"/formations?team=1");
        page.locator("#formationNumInput").fill("1-2-3");
        page.locator("#formation_submit").click();
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
    }

    @When("I enter in the formation {string}")
    public void i_enter_in_the_formation(String formationInput) {
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        page.locator("#formationNumInput").fill(formationInput);
    }

    @When("I click on a UI element to see all the team's formations")
    public void i_click_on_a_ui_element_to_see_all_the_team_s_formations() {
        page.locator("#formations-tab").click();
    }

    @When("I click the submit formation button")
    public void i_click_the_submit_formation_button() {
        page.locator("#formation_submit").click();
    }

    @When("I click on a UI element to create a new line-up with the sport {string}")
    public void I_click_on_a_UI_element_to_create_a_new_line_up(String sport_value) {
        PlaywrightBrowser.page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        PlaywrightBrowser.page.locator("#formationImage").selectOption(sport_value);
    }



    @Then("A formation error message is displayed")
    public void an_error_message_is_displayed() {
        assertThat(page.locator("#formationNumInput")).hasClass(Pattern.compile("is-invalid"));
    }

    @Then("I see a graphical representation of a sport pitch corresponding to the sport {string} of that team")
    public void I_see_a_graphical_representation_of_a_sport_pitch(String sport) {
        PlaywrightBrowser.page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        Locator locator = page.locator("#formationContainer");
        String backgroundImg = (String) locator.evaluate("element => getComputedStyle(element).backgroundImage");
        Assertions.assertTrue(backgroundImg.contains(sport + ".png"));
    }


    @When("I set up the number of players per sector to {string}")
    public void iSetUpTheNumberOfPlayersPerSectorTo(String input) {
        this.input = input;
        PlaywrightBrowser.page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        PlaywrightBrowser.page.locator("#formationNumInput").fill(input);
    }

    @Then("I see icons of players organised as described by the pattern on the graphical pitch")
    public void I_see_correct_display() {
        PlaywrightBrowser.page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        int totalImgs = Stream.of (input.split("-")).mapToInt(Integer::parseInt).sum();
        Integer imgNum = page.locator("img").count();
        Assertions.assertEquals(totalImgs+2, imgNum);
        Locator html = PlaywrightBrowser.page.locator("#formationDisplay");
        String[] inputSub = input.split("-");
        Assertions.assertNotNull(html.locator("div").nth(inputSub.length));
        for (int i = 0; i<inputSub.length; i++) {
            Assertions.assertNotNull(html.locator("div").nth(i).locator("img").nth(Integer.parseInt(inputSub[i])));
        }
    }

    @When("I put player {int} to position {int} and {int}")
    public void I_put_player_player_to_position_num1_and_num2(int player, int pos1, int pos2) {
        PlaywrightBrowser.page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        Locator pos = page.locator("#position_"+pos1+"_"+pos2);
        page.locator("#player_"+player).dragTo(pos);
    }

    @Then("The image at {int} {int} is player {int} image")
    public void the_image_has_changed_for_the_formation(int pos1, int pos2, int player) {
        PlaywrightBrowser.page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        String playerSrc = page.locator("#player_"+player).getAttribute("src");
        String formSrc = page.locator("#position_"+pos1+"_"+pos2).getAttribute("src");
        Assertions.assertEquals(playerSrc,formSrc);
    }


    @When("I click the formation save button")
    public void iClickTheFormationSaveButton() {
        page.locator("#formation_submit").click();
    }


    @Then("I am redirected to my team's page")
    public void iAmRedirectedToMyTeamsPage() {
        PlaywrightBrowser.page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        Assertions.assertTrue(PlaywrightBrowser.page.url().contains("/teamProfile"));
    }


    @Then("A Formation creation failed message is shown")
    public void aFormationCreationFailedMessageIsShown() {
        PlaywrightBrowser.page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        Assertions.assertTrue(PlaywrightBrowser.page.url().contains("/formations"));

        String value = PlaywrightBrowser.page.locator("#formation_message").innerText();
        Assertions.assertFalse(value.isEmpty());
        Assertions.assertNotEquals("Formation was saved successfully.",value);
    }


    @Then("A Formation creation form is not sent")
    public void aFormationCreationFromIsNotSent() {
        PlaywrightBrowser.page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        Assertions.assertTrue(PlaywrightBrowser.page.url().contains("/formations"));
    }

    @Given("I am on toms teams profile")
    public void iAmOnTomSTeamSProfile() {
        page.navigate(baseUrl+"/teamProfile?id=2");
    }



    @Then("I see a list of all formations for that team")
    public void iSeeAListOfAllFormationsForThatTeam() {
        Locator locator = page.locator("#formations");
        Assertions.assertTrue(locator.isVisible());
        String innerHtml = locator.innerHTML();
        // Can't assume that all the formations will always be there e.g. if the database changes
        // Check that there is at least one formation
        Assertions.assertTrue(innerHtml.contains("formationCard1"));

    }
}

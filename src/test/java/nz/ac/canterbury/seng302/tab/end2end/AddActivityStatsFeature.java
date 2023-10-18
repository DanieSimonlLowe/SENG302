package nz.ac.canterbury.seng302.tab.end2end;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import com.microsoft.playwright.assertions.*;
import org.junit.jupiter.api.Assertions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddActivityStatsFeature {
    private String activityDescription;
    @Given("I create an activity with description {string}")
    public void i_create_an_activity_with_description(String desc) {
        activityDescription = desc;

        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/activityForm");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

        Date now = new Date();
        Date oneHour;
        Date twoHours;
        // Use a calendar to add hours to a date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.HOUR_OF_DAY, 1); // 1 hour

        oneHour = calendar.getTime();
        calendar.add(Calendar.HOUR_OF_DAY, 2);
        twoHours = calendar.getTime();

        // Creating the activity by entering valid information
        PlaywrightBrowser.page.selectOption("#actType","game");
        PlaywrightBrowser.page.selectOption("#actTeam","Test Team");
        PlaywrightBrowser.page.selectOption("#actOpposition", "Dolphins");
        PlaywrightBrowser.page.locator("#actDesc").fill(desc);
        PlaywrightBrowser.page.locator("#actStart").fill(dateFormat.format(oneHour));
        PlaywrightBrowser.page.locator("#actEnd").fill(dateFormat.format(twoHours));
        PlaywrightBrowser.page.locator("#nextButton").click();
        PlaywrightBrowser.page.locator("#form-address1").fill("5 Ilam Rd");
        PlaywrightBrowser.page.locator("#form-postcode").fill("8041");
        PlaywrightBrowser.page.locator("#form-city").fill("Christchurch");
        PlaywrightBrowser.page.locator("#form-country").fill("New Zealand");

        // Create the activity
        PlaywrightBrowser.page.locator("#nextButton").click();
        PlaywrightBrowser.page.locator("#nextButton").click();
    }

    @Given("I am on the view activities page")
    public void i_am_on_the_view_activities_page() {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/viewActivities");
    }

    @When("I click on the activity card")
    public void i_click_on_the_activity_card_with_description() {
        Locator activity = PlaywrightBrowser.page.locator("#calendar").getByText(activityDescription).first();
        activity.click();
        activity.click();
        activity.click();
    }

    @When("I click on the stats button")
    public void i_click_on_the_stats_button() {
        var locator = PlaywrightBrowser.page.locator(".activityDetails");
        Locator detailsDiv = null;
        for (int i = 0; i < locator.count(); i++) {
            var element = locator.nth(i);
            if (element.isVisible()) {
                detailsDiv = element;
                break;
            }
        }
        assert detailsDiv != null;
        detailsDiv.getByText("Stats").click();
    }

    @Then("The activity statistics popup opens")
    public void the_activity_statistics_popup_opens() {
        PlaywrightAssertions.assertThat(PlaywrightBrowser.page.locator("#modal")).isVisible();
    }

    @Given("I have an activity of type {string}")
    public void i_have_an_activity_of_type(String activityType) {
        activityDescription = "Test";
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/activityForm");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

        Date now = new Date();
        Date oneHour;
        Date twoHours;
        // Use a calendar to add hours to a date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.HOUR_OF_DAY, 1); // 1 hour

        oneHour = calendar.getTime();
        calendar.add(Calendar.HOUR_OF_DAY, 2);
        twoHours = calendar.getTime();

        // Creating the activity by entering valid information
        PlaywrightBrowser.page.selectOption("#actType",activityType);
        PlaywrightBrowser.page.selectOption("#actTeam","Test Team");
        if (activityType.equals("game") || activityType.equals("friendly")) {
            PlaywrightBrowser.page.selectOption("#actOpposition", "Dolphins");
        }
        PlaywrightBrowser.page.locator("#actDesc").fill(activityDescription);
        PlaywrightBrowser.page.locator("#actStart").fill(dateFormat.format(oneHour));
        PlaywrightBrowser.page.locator("#actEnd").fill(dateFormat.format(twoHours));
        PlaywrightBrowser.page.locator("#nextButton").click();
        PlaywrightBrowser.page.locator("#form-address1").fill("5 Ilam Rd");
        PlaywrightBrowser.page.locator("#form-postcode").fill("8041");
        PlaywrightBrowser.page.locator("#form-city").fill("Christchurch");
        PlaywrightBrowser.page.locator("#form-country").fill("New Zealand");

        // Create the activity
        PlaywrightBrowser.page.locator("#nextButton").click();

        if (activityType.equals("game") || activityType.equals("friendly")) {
            // No lineup
            PlaywrightBrowser.page.locator("#nextButton").click();
        }
    }

    @When("I select the substitution tab")
    public void i_select_the_substitution_tab() {
        PlaywrightBrowser.page.getByRole(AriaRole.TAB, new Page.GetByRoleOptions().setName("substitution")).click();

    }

    @Then("I can record which player was substituted off of the field")
    public void i_can_record_which_player_was_substituted_off_of_the_field() {
        PlaywrightAssertions.assertThat(PlaywrightBrowser.page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("offPlayerDropdownMenuButton"))).isVisible();
    }

    @Then("I can record which player was substituted on to the field")
    public void i_can_record_which_player_was_substituted_on_to_the_field() {
        PlaywrightAssertions.assertThat(PlaywrightBrowser.page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("onPlayerDropdownMenuButton"))).isVisible();
    }

    @Then("I can record when the players were swapped")
    public void i_can_record_when_the_players_were_swapped() {
        PlaywrightAssertions.assertThat(PlaywrightBrowser.page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("substitutionTimeInput"))).isVisible();
    }

    @When("I select the facts tab")
    public void i_open_the_facts_tab() {
        PlaywrightBrowser.page.getByRole(AriaRole.TAB, new Page.GetByRoleOptions().setName("facts")).click();
    }

    @Then("I can record facts about that activity with a description and optional time")
    public void activity_fact_description_time() {
        PlaywrightBrowser.page.locator("#factDesc").fill("Fact Description");
        PlaywrightBrowser.page.locator("#factTime").fill("00:00:00");
        PlaywrightBrowser.page.locator("#submit").click();
    }

    @When("I click on the score page")
    public void iClickOnTheScorePage() {
        PlaywrightBrowser.page.locator("#scoringButton").click();
    }

    @When("I set it's game scores to {string} {string}")
    public void iSetItsGameScoresTo(String score1, String score2) {
        PlaywrightBrowser.page.locator("#scoreInputTeam1").fill(score1);
        PlaywrightBrowser.page.locator("#scoreInputTeam2").fill(score2);
    }


    @Then("The game scores are {string} {string}")
    public void theGameScoresAreScoreScore(String score1, String score2) {
        Assertions.assertEquals(score1,PlaywrightBrowser.page.locator("#scoreInputTeam1").inputValue());
        Assertions.assertEquals(score2,PlaywrightBrowser.page.locator("#scoreInputTeam2").inputValue());
    }

    @Then("I see a game score error of {string} {string}")
    public void iSeeAGameScoreError(String error1,String error2) {
        if (error1.length() > 1) {
            Assertions.assertTrue(PlaywrightBrowser.page.locator("#errorTextTeam1").isVisible(), "error 1 not visible");
            Assertions.assertEquals(error1,PlaywrightBrowser.page.locator("#errorTextTeam1").innerText());
        }
        if (error2.length() > 1) {
            Assertions.assertTrue(PlaywrightBrowser.page.locator("#errorTextTeam2").isVisible(), "error 2 not visible");
            Assertions.assertEquals(error2,PlaywrightBrowser.page.locator("#errorTextTeam2").innerText());
        }
    }
}

package nz.ac.canterbury.seng302.tab.end2end;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.LoadState;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static nz.ac.canterbury.seng302.tab.end2end.PlaywrightBrowser.baseUrl;
import static nz.ac.canterbury.seng302.tab.end2end.PlaywrightBrowser.page;

public class ViewTeamActivitiesFeature {

    int teamId;

    @Given("I have a personal activity")
    public void i_have_a_personal_activity() {
        page.navigate(PlaywrightBrowser.baseUrl+"/activityForm");
        page.locator("#actType").selectOption("other");
        page.locator("#actDesc").fill("test personal activity");
        page.locator("#actTeam").selectOption("0");
        page.locator("#actStart").fill("2002-08-19T00:00");
        page.locator("#actEnd").fill("2002-08-19T01:00");
        page.locator("#nextButton").click();
        page.locator("#form-address1").fill("1000 Test Place");
        page.locator("#form-suburb").fill("Testurbia");
        page.locator("#form-postcode").fill("TEST");
        page.locator("#form-city").fill("Test Town");
        page.locator("#form-country").fill("United Tests of Testia");
        page.locator("#nextButton").click();
        Assertions.assertTrue(page.url().contains("/viewActivities"));
    }

    @Given("I am the owner of a team that has multiple different activities")
    public void i_am_the_owner_of_a_team_that_has_multiple_different_activities() {
        page.navigate(PlaywrightBrowser.baseUrl+"/form");
        page.locator("#teamName").fill("Testing team");
        PlaywrightBrowser.page.locator("#form-city").fill("Test Town");
        PlaywrightBrowser.page.locator("#form-country").fill("United Tests of Testia");
        PlaywrightBrowser.page.locator("#sport").fill("Test Sport");
        page.locator("#submit").click();
        Assertions.assertTrue(page.url().contains("/teamProfile"));

        Pattern pattern = Pattern.compile("\\bid=(\\d+)\\b");
        Matcher matcher = pattern.matcher(page.url());
        if (matcher.find()) {
            teamId = Integer.parseInt(matcher.group(1));
        }
        Assertions.assertNotEquals(-1,teamId, "failed to generate a team");
        Assertions.assertTrue(teamId > -1);
        for (int i = 0; i < 9; i++) {
            page.navigate(PlaywrightBrowser.baseUrl+"/activityForm");
            page.locator("#actType").selectOption("other");
            page.locator("#actTeam").selectOption(String.valueOf(teamId));
            page.locator("#actOpposition").selectOption("0");
            page.locator("#actDesc").fill("test personal activity " + i);
            page.locator("#actStart").fill("2024-08-19T0" + i + ":00");
            page.locator("#actEnd").fill("2024-08-19T0" + (i + 1) + ":00");
            page.locator("#nextButton").click();
            page.locator("#form-address1").fill("1000 Test Place");
            page.locator("#form-suburb").fill("Testurbia");
            page.locator("#form-postcode").fill("1TEST1");
            page.locator("#form-city").fill("Test Town");
            page.locator("#form-country").fill("United Tests of Testia");
            page.locator("#nextButton").click();
            Assertions.assertTrue(page.url().contains("/viewActivities"));
        }

    }

    @Given("I am on my team’s profile")
    public void i_am_on_my_teams_profile() {
        page.navigate(baseUrl + "/teamProfile?id=" + teamId);
        Assertions.assertTrue(page.url().contains("/teamProfile?id="));
    }

    @When("I click on a UI element to see all the team’s activities")
    public void i_click_on_a_ui_element_to_see_all_the_teams_activities() {
        page.locator("#teamActivities").click();
        Assertions.assertTrue(page.url().contains("/viewActivities"));
    }

    @Then("I see a list of all activities for that team")
    public void i_see_a_list_of_all_activities_for_that_team() {
        PlaywrightBrowser.page.locator("#calendar").isVisible();

    }

    @When("I see the list of all team activities")
    public void i_see_the_list_of_all_team_activities() {
        page.navigate(baseUrl + "/teamProfile?id=" + teamId);
        Assertions.assertTrue(page.url().contains("/teamProfile?id="));
        page.locator("#teamActivities").click();
        Assertions.assertTrue(page.url().contains("/viewActivities"));
        PlaywrightBrowser.page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        Assertions.assertEquals(0, page.getByText("OTHER").count()%9);
    }

    @Then("these activities are sorted by start time in ascending order")
    public void these_activities_are_sorted_by_start_time_in_ascending_order() {
        Pattern pattern = Pattern.compile("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}$");
        List<Locator> activityCards = page.getByText(pattern).all();
        String prevStartTime = "0001-01-01 00:00";
        for (Locator activityCard : activityCards) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            LocalDateTime dateTime1 = LocalDateTime.parse(activityCard.textContent(), formatter);
            LocalDateTime dateTime2 = LocalDateTime.parse(prevStartTime, formatter);

            Assertions.assertTrue(dateTime1.isAfter(dateTime2));
        }
    }
}

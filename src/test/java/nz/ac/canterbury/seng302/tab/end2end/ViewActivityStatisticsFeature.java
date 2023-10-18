package nz.ac.canterbury.seng302.tab.end2end;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static nz.ac.canterbury.seng302.tab.end2end.PlaywrightBrowser.page;

public class ViewActivityStatisticsFeature {
    static private int count = 0;

    @Given("I have an activity of type {string} with a line-up")
    public void i_have_an_activity_of_type_with_a_line_up(String activityType) {
        count += 1;
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/activityForm");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

        Date now = new Date();
        Date oneHour;
        Date twoHours;
        // Use a calendar to add hours to a date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DATE, count); // 1 hour

        oneHour = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        twoHours = calendar.getTime();

        // Creating the activity by entering valid information
        PlaywrightBrowser.page.selectOption("#actType",activityType);
        PlaywrightBrowser.page.selectOption("#actTeam","Dolphins");
        if (activityType.equals("game") || activityType.equals("friendly")) {
            PlaywrightBrowser.page.selectOption("#actOpposition", "Heat");
        }
        PlaywrightBrowser.page.locator("#actDesc").fill("Lineup Test" + count);
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
            PlaywrightBrowser.page.locator("#formationCard1").click();
            PlaywrightBrowser.page.locator("#player_1").dragTo(PlaywrightBrowser.page.locator("#position_0_0"));
            PlaywrightBrowser.page.locator("#player_3").dragTo(PlaywrightBrowser.page.locator("#position_1_0"));
            PlaywrightBrowser.page.locator("#player_15").dragTo(PlaywrightBrowser.page.locator("#position_0_1"));
            PlaywrightBrowser.page.locator("#nextButton").click();
        }
    }

    @Given("I add a score to the activity")
    public void i_add_a_score_to_the_activity() {
        PlaywrightBrowser.page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        Locator activity = PlaywrightBrowser.page.locator("#calendar").getByText("Lineup Test").first();
        activity.click();
        activity.click();
        activity.click();

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
        assertThat(PlaywrightBrowser.page.locator("#modal")).isVisible();
        PlaywrightBrowser.page.locator("#scoringButton").click();

        page.locator("#scoreInputTeam1").fill("2");
        page.locator("#scoreInputTeam2").fill("2");

        PlaywrightBrowser.page.locator("#scoreTime").fill("1");
        PlaywrightBrowser.page.locator("#scorePoints").fill("2");
        PlaywrightBrowser.page.locator("#addScoreBtn").click();
    }

    @When("I click a player's icon on the line-up")
    public void i_click_a_player_s_icon_on_the_line_up() {
        PlaywrightBrowser.page.getByRole(AriaRole.TAB, new Page.GetByRoleOptions().setName("lineup")).click();
        PlaywrightBrowser.page.locator("#position_0_0").click();
    }

    @Then("I can see the time when the player scored next to their icon on the line-up")
    public void i_can_see_the_time_when_the_player_scored_next_to_their_icon_on_the_line_up() {
        assertThat(PlaywrightBrowser.page.getByText("Scored 2pts at 1'min")).isVisible();
    }

    @Given("I am viewing {string} activity on {string}")
    public void iAmViewingTestActivity(String name, String date) {
        while (!page.locator("#calendardate").innerText().equalsIgnoreCase(date)) {
            page.locator("#calendarNextBtn").click();
        }
        Locator activity = PlaywrightBrowser.page.locator("#calendar").getByText(name).first();
        activity.click();
        activity.click();
        activity.click();

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

    @When("I click the user at position {int}, {int}")
    public void iClickTheUserAtPosition(int arg0, int arg1) {
        page.locator("#position_"+arg0+"_"+arg1).click();
    }

    @Then("I see the that {string} was substituted for {string} at {int}")
    public void iSeeTheThatWasSubstitutedForAt(String user1, String user2, int time) {
        String text = page.locator(".popover").innerText();
        Assertions.assertTrue(text.contains("Substituted " + user1 + " to " + user2 + " at " + time+"min"),text);
    }

    @When("I click on the lineup")
    public void iClickOnTheLineup() {
        page.locator("#lineupTabButton").click();
    }

    @When("I click on the facts")
    public void iClickOnTheFacts() {
        page.locator("#factLink").click();
    }

    @Then("the facts are sorted by their time in ascending order")
    public void theFactsAreSorted() {
        Locator locator = page.locator("#activityFactsTableBody > tr");
        Assertions.assertEquals(3,locator.count());
        Assertions.assertEquals("test2\t3'\tDelete", locator.nth(0).innerText());
        Assertions.assertEquals("test\t5'\tDelete", locator.nth(1).innerText());
        Assertions.assertEquals("test3\t6'\tDelete", locator.nth(2).innerText());
    }
}

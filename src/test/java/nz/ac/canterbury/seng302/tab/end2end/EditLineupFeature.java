package nz.ac.canterbury.seng302.tab.end2end;

import com.microsoft.playwright.Locator;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import static nz.ac.canterbury.seng302.tab.end2end.PlaywrightBrowser.baseUrl;
import static nz.ac.canterbury.seng302.tab.end2end.PlaywrightBrowser.page;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class EditLineupFeature {
    @Given("I have a team with a formation")
    public void i_have_a_team_with_a_formation() {
        page.navigate(baseUrl+"/teamProfile?id=2");
        page.locator("#formations-tab").click();
        Locator locator = page.locator("#formations");
        Assertions.assertTrue(locator.isVisible());
        String innerHtml = locator.innerHTML();
        Assertions.assertTrue(innerHtml.contains("formationCard1"));
    }
    @Given("I have an activity with type {string}")
    public void i_have_an_activity_of_type(String type) {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/activityForm");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

        Date now = new Date();
        String timestamp = "" + now.getTime();
        String activityDescription = timestamp + " Test Activity";
        Date startTime;
        Date endTime;
        // Use a calendar to add hours to a date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.HOUR_OF_DAY, 1); // 1 hour

        startTime = calendar.getTime();
        calendar.add(Calendar.HOUR_OF_DAY, 2);
        endTime = calendar.getTime();

        // Creating the activity by entering valid information
        PlaywrightBrowser.page.selectOption("#actType",type);
        PlaywrightBrowser.page.selectOption("#actTeam","Dolphins");
        if (type.equals("game") || type.equals("friendly")) {
            PlaywrightBrowser.page.selectOption("#actOpposition", "Test Team");
        }
        PlaywrightBrowser.page.locator("#actDesc").fill(activityDescription);
        PlaywrightBrowser.page.locator("#actStart").fill(dateFormat.format(startTime));
        PlaywrightBrowser.page.locator("#actEnd").fill(dateFormat.format(endTime));
        PlaywrightBrowser.page.locator("#nextButton").click();
        PlaywrightBrowser.page.locator("#form-address1").fill("5 Ilam Rd");
        PlaywrightBrowser.page.locator("#form-postcode").fill("8041");
        PlaywrightBrowser.page.locator("#form-city").fill("Christchurch");
        PlaywrightBrowser.page.locator("#form-country").fill("New Zealand");

        // Create the activity
        PlaywrightBrowser.page.locator("#nextButton").click();
    }

    @When("I am on the edit activity page of that activity")
    public void i_am_on_the_edit_activity_page_of_that_activity() {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/editActivity?id=1");
        Assertions.assertTrue(page.locator("#activityForm").isVisible());
    }

    @When("I navigate to the lineup tab")
    public void i_navigate_to_the_lineup_tab() {
        PlaywrightBrowser.page.locator("#lineupTabButton").click();
        Assertions.assertTrue(page.locator("#lineupArea").isVisible());
    }

    @Then("I can add a line-up from the list of existing formations for that team")
    public void i_can_add_a_line_up_from_the_list_of_existing_formations_for_that_team() {
        PlaywrightBrowser.page.locator("#formationCard1").click();
        assertThat(page.locator("#formationCard1")).hasClass(Pattern.compile("border-success"));
    }

    @Given("I have made changes to the activity")
    public void iHaveMadeChangesToTheActivity() {
        page.locator("#actDesc").fill("New Text This Should not change stuff.");
    }

    @Then("The current changes to the formation are ignored")
    public void TheCurrentChangesToTheFormationAreIgnored() {
        page.navigate( baseUrl+ "/editActivity?id=1");
        String value = page.locator("#actDesc").inputValue();
        Assertions.assertEquals("test",value);
    }
}

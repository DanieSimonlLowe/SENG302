package nz.ac.canterbury.seng302.tab.end2end;

import com.microsoft.playwright.assertions.PlaywrightAssertions;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import static nz.ac.canterbury.seng302.tab.end2end.PlaywrightBrowser.baseUrl;
import static nz.ac.canterbury.seng302.tab.end2end.PlaywrightBrowser.page;

public class CreateActivityFeature {
    @Given("I am at {string} on the system")
    public void iAmAtUrlOnTheSystem(String url) {
        page.navigate(baseUrl+url);
    }

    @When("I click on a UI element to create an activity")
    public void iClickOnAUIElementToCreateAnActivity() {
        page.locator("#activitiesDropdownMenu").click();
        page.locator("#createActivity").click();
    }

    @Then("I see a form to create an activity")
    public void iSeeAFormToCreateAnActivity() {
        Assertions.assertEquals("http://"+baseUrl+"/activityForm", page.url());
    }

    @Given("I am on the create activity page")
    public void iAmOnTheCreateActivityPage() {
        PlaywrightBrowser.page.navigate("http://"+baseUrl + "/activityForm");
        Assertions.assertTrue(page.url().contains("/activityForm"));
    }

    @Given("I enter values for the team it relates to \\(optional), the activity {string}, a short description {string} and the activity start {string} and end time {string}")
    public void iEnterValidValuesForTheTeamItRelatesToOptionalTheActivityAShortDescriptionAndTheActivityStartAndEndTime(String type, String desc, String start, String end) {
        PlaywrightBrowser.page.selectOption("#actType", type);
        PlaywrightBrowser.page.selectOption("#actTeam","None");
        PlaywrightBrowser.page.locator("#actDesc").fill(desc);
        PlaywrightBrowser.page.locator("#actStart").fill(start);
        PlaywrightBrowser.page.locator("#actEnd").fill(end);
        PlaywrightBrowser.page.locator("#nextButton").click();
    }

    @Given("I enter values for the location")
    public void iEnterValuesForTheLocation() {
        PlaywrightBrowser.page.locator("#form-address1").fill("helsinki");
        PlaywrightBrowser.page.locator("#form-postcode").fill("1234");
        PlaywrightBrowser.page.locator("#form-city").fill("helsinki");
        PlaywrightBrowser.page.locator("#form-country").fill("finland");
    }

    @When("I hit the create activity button")
    public void iHitTheCreateActivityButton() {
        page.locator("#nextButton").click();
    }


    @Then("an activity is created into the system and I see the details of the activity")
    public void anActivityIsCreatedIntoTheSystemAndISeeTheDetailsOfTheActivity() {
        Assertions.assertTrue(page.url().contains("/viewActivities"));
    }

    @When("I am asked to select the team the activity relates to")
    public void iAmAskedToSelectTheTeamTheActivityRelatesTo() {
        Assertions.assertEquals(baseUrl + "/activityTeamSelect", page.url());
    }


    @Then("I can select from: game, friendly, training, competition, other.")
    public void iCanSelectFromGameFriendlyTrainingCompetitionOther() {
        String[] values = {"game", "friendly", "training", "competition", "other"};
        for (String value : values) {
            PlaywrightBrowser.page.selectOption("#actType", value);
            PlaywrightAssertions.assertThat(PlaywrightBrowser.page.getByLabel("Activity Type")).hasValue(value);
        }
    }

    @Given("I select {string} as the activity type")
    public void iSelectTypeAsTheActivityType(String select) {
        page.locator("#actSel").selectOption(select);
    }



    @Then("an error message tells me I must select an activity type")
    public void anErrorMessageTellsMeIMustSelectAnActivityType() {
        PlaywrightAssertions.assertThat(PlaywrightBrowser.page.getByText("Please select an option for type")).isVisible();

    }


    @Then("an error message tells me the start and end time are compulsory")
    public void anErrorMessageTellsMeTheStartAndEndTimeAreCompulsory() {
        Assertions.assertTrue(page.url().contains("/activityForm"));
    }


    @Then("an error message tells me the end date is before the start date")
    public void anErrorMessageTellsMeTheEndDateIsBeforeTheStartDate() {
        PlaywrightAssertions.assertThat(PlaywrightBrowser.page.getByText("The end time of an activity cannot")).isVisible();
    }

    @Then("an error message tells me the dates are prior the team’s creation date and the team’s creation date is shown")
    public void anErrorMessageTellsMeTheDatesArePriorTheTeamSCreationDateAndTheTeamSCreationDateIsShown() {
        Assertions.assertTrue(page.url().contains("/activityForm"));
    }

    @Then("an error message tells me I must select a team")
    public void anErrorMessageTellsMeIMustSelectATeam() {
        PlaywrightAssertions.assertThat(PlaywrightBrowser.page.getByText("A team must be selected for this activity type.")).isVisible();
    }

    @Then("an error message tells me I must have a valid description")
    public void anErrorMessageTellsMeIMustHaveAValidDescription() {
        PlaywrightAssertions.assertThat(PlaywrightBrowser.page.locator("#actDesc")).isVisible();

    }
}

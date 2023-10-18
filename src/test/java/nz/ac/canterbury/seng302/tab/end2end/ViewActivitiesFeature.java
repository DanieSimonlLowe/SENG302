package nz.ac.canterbury.seng302.tab.end2end;

import com.microsoft.playwright.Locator;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static nz.ac.canterbury.seng302.tab.end2end.PlaywrightBrowser.page;

public class ViewActivitiesFeature {
//    @Given("I have created {int} activities")
//    public void i_have_created_an_activity(int numTeams) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
//        LocalDateTime startTime = LocalDateTime.now();
//        LocalDateTime endTime = LocalDateTime.now().plusHours(1);
//        String formattedStartTime = startTime.format(formatter);
//        String formattedEndTime = endTime.format(formatter);
//        for (int i = 0; i < numTeams; i++) {
//            page.navigate(PlaywrightBrowser.baseUrl+"/activityForm");
//            page.locator("#actType").selectOption("game");
//            page.locator("#actDesc").fill("test" + i);
//            page.locator("#actStart").fill(formattedStartTime);
//            page.locator("#actEnd").fill(formattedEndTime);
//            page.selectOption("#actTeam","Dolphins");
//            page.selectOption("#actOpposition","Dolphins");
//            page.locator("#nextButton").click();
//            page.locator("#form-address1").fill("5 Ilam Rd");
//            page.locator("#form-postcode").fill("8041");
//            page.locator("#form-city").fill("Christchurch");
//            page.locator("#form-country").fill("New Zealand");
//            page.locator("#nextButton").click();
//            page.locator("#nextButton").click();
//        }
//    }
//
//    @When("I click on an activity with {string}")
//    public void iClickOnAActivityWithName(String name) {
//        page.navigate(PlaywrightBrowser.baseUrl+"/viewActivities");
//        var locator = page.locator(".toastui-calendar-weekday-event");
//        for (int i = 0; i < locator.count(); i++) {
//            var element = locator.nth(i);
//            if (element.innerText().contains(name)) {
//                //Assertions.assertEquals("element.innerText()",element.innerHTML());
//                element.click();
//                element.click();
//                element.click();
//                return;
//            }
//        }
////        Assertions.fail(String.valueOf(locator.count()));
//
//    }
//
//    @Then("I see an activity card with {string}")
//    public void iSeeAnActivityCardWithName(String name) {
//        var locator = page.locator(".activityDetails");
//        int count = locator.count();
//        boolean hasVisible = false;
//        for (int i = 0; i < count; i++) {
//            var element = locator.nth(i);
//            if (element.innerText().contains(name)) {
//                Assertions.assertTrue(element.isVisible());
//                hasVisible = true;
//            } else {
//                Assertions.assertFalse(element.isVisible());
//            }
//        }
//        Assertions.assertNotEquals(0,count);
//        Assertions.assertTrue(hasVisible);
//    }
//
//    @When("I click on the activity team {int}")
//    public void iClickOnTheActivityTeam(int id) {
//        page.locator("#teamButton_"+id).click();
//    }
//
//    @Then("I am redirected to team profile {int}")
//    public void iAmRedirectedToTeamProfileTeam(int teamId) {
//        Assertions.assertEquals("http://" + PlaywrightBrowser.baseUrl + "/teamProfile?id=" + teamId, PlaywrightBrowser.page.url());
//    }

}

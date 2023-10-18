package nz.ac.canterbury.seng302.tab.end2end;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class FollowProfilesFeature {

    GeneralFunctions generalFunctions = new GeneralFunctions();

    @When("I hit the follow button")
    public void iHitTheFollowButton() {
        if (!PlaywrightBrowser.page.locator("#friendsDisplay").isVisible()) {
                PlaywrightBrowser.page.locator("#userFollowBtn").click();

        }
    }

    @Then("I am following the user")
    public void iAmFollowingTheUser() {
        Assertions.assertEquals(0,PlaywrightBrowser.page.locator("#userFollowBtn").count());
    }

    @When("I hit the following button")
    public void iHitTheFollowingButton() {
        PlaywrightBrowser.page.locator("#following-tab").click();
        Assertions.assertEquals("true", PlaywrightBrowser.page.locator("#following-tab").getAttribute("aria-selected"));
    }

    @Then("I see a list of user profiles I follow")
    public void iSeeAListOfUserProfilesIFollow() {
        Assertions.assertTrue(1 <= PlaywrightBrowser.page.locator("#followingTableBody").locator("tr").count());
    }

    @Given("I am following a user")
    public void doAC2() {

        generalFunctions.gotoPage("/otherProfile?email=morgan.english8@hotmail.com&prevPage=1&search=");
    }

    @When("I hit the unfollow button")
    public void iHitTheUnfollowButton() {
        if (!PlaywrightBrowser.page.locator("#friendsDisplay").isVisible()) {

            PlaywrightBrowser.page.locator("#userUnFollowBtn").click();
        }
    }

    @Then("I am not following the user")
    public void iAmNotFollowingTheUser() {
        Assertions.assertEquals(0,PlaywrightBrowser.page.locator("#userUnFollowBtn").count());
    }
}

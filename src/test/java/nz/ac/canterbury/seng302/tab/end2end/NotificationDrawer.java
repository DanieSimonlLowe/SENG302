package nz.ac.canterbury.seng302.tab.end2end;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class NotificationDrawer {

    @When("I click on the notification drawer")
    public void i_click_on_alerts() {
        PlaywrightBrowser.page.locator("#bellSelector").click();
    }

    @Then("I can see a notification drawer")
    public void i_see_alerts() {
        Assertions.assertTrue(PlaywrightBrowser.page.locator("#notificationDropdownArea").isVisible());
    }
}

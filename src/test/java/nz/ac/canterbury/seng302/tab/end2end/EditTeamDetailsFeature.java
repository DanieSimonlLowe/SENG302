package nz.ac.canterbury.seng302.tab.end2end;

import com.microsoft.playwright.options.LoadState;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class EditTeamDetailsFeature {
    @When("I enter a team name {string}")
    public void iEnterATeamName(String arg0) {
        PlaywrightBrowser.page.locator("#teamName").fill(arg0);
    }

    @And("Team name is {string}")
    public void teamNameIs(String arg0) {
        PlaywrightBrowser.page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        String text = PlaywrightBrowser.page.locator("#teamName").innerText();
        Assertions.assertEquals(arg0,text);
    }

    @When("I click on edit profile button")
    public void iClickOnEditProfileButton() {
        PlaywrightBrowser.page.locator("#edit-btn").click();
    }



}

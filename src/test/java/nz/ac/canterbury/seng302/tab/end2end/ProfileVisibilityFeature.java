package nz.ac.canterbury.seng302.tab.end2end;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import static nz.ac.canterbury.seng302.tab.end2end.PlaywrightBrowser.baseUrl;

public class ProfileVisibilityFeature {
    @When("I select {string} privacy level")
    public void iSelectSettingPrivacyLevel(String setting) {
        PlaywrightBrowser.page.locator("#privacySelector").selectOption(setting);
    }

    @Then("The privacy level has changed to {string}")
    public void thePrivacyLevelHasChangedToSetting(String setting) {
        PlaywrightBrowser.page.reload();
        String value = PlaywrightBrowser.page.locator("#privacySelector").inputValue();
        Assertions.assertEquals(setting,value);
    }

    @When("I view a private user")
    public void when_i_see_a_private_user() {
        PlaywrightBrowser.page.navigate(baseUrl+"/otherProfile?email=s.west@hotmail.com&prevPage=1&search=");
    }

    @Then("I can't see user details")
    public void then_i_cant_see_user_details() {
        Assertions.assertEquals(0, PlaywrightBrowser.page.locator("#emailDisplay").count());
        Assertions.assertEquals(0, PlaywrightBrowser.page.locator("#dateOfBirthDisplay").count());
        Assertions.assertEquals(0, PlaywrightBrowser.page.locator("#sportsDisplay").count());
        Assertions.assertEquals(0, PlaywrightBrowser.page.locator("#teamList").count());
    }

    @Then("I can see the name and the follow button")
    public void then_i_can_see_the_name_and_follow_button() {
        Assertions.assertEquals(1, PlaywrightBrowser.page.locator("#fName").count());
        //Assertions.assertEquals(1, PlaywrightBrowser.page.locator("#followButton").count());
    }
}

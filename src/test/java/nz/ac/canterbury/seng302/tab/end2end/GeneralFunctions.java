package nz.ac.canterbury.seng302.tab.end2end;

import com.microsoft.playwright.options.LoadState;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class GeneralFunctions {
    @Given("I am logged in")
    public void login() {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl+"/login");
        PlaywrightBrowser.page.locator("#email").fill("morgan.english@hotmail.com");
        PlaywrightBrowser.page.locator("#password").fill("Password1!");
        PlaywrightBrowser.page.locator("#submit").click();
        Assertions.assertTrue(PlaywrightBrowser.page.url().contains("/profile"));
    }

    @Given("I am not logged in")
    public void i_am_not_logged_in() {
        Assertions.assertTrue(PlaywrightBrowser.page.locator("#login").isVisible());
        Assertions.assertFalse(PlaywrightBrowser.page.locator("#myProfile").isVisible());
    }

    @Given("I am on page {string}")
    public void gotoPage(String page) {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl+page);
        PlaywrightBrowser.page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        Assertions.assertTrue(PlaywrightBrowser.page.url().contains(page));
    }

    @Given("I navigate to page {string}")
    public void i_navigate_to_page(String page) {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl+page);
    }

    @When("I click the submit button")
    public void i_click_the_submit_button(){
        PlaywrightBrowser.page.locator("#submit").click();
    }

    @When("I click the navbar element with id {string}")
    public  void i_click_the_navbar_element_with_id(String id) {
        Assertions.assertTrue(PlaywrightBrowser.page.locator(id).isVisible());
        PlaywrightBrowser.page.locator(id).click();
    }

    @Given("I am logged in as Tom")
    public void iAmLoggedInAsTom() {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl+"/login");
        PlaywrightBrowser.page.locator("#email").fill("tom.barthelmeh@hotmail.com");
        PlaywrightBrowser.page.locator("#password").fill("Password1!");
        PlaywrightBrowser.page.locator("#submit").click();
        Assertions.assertTrue(PlaywrightBrowser.page.url().contains("/profile"));
    }

    @Given("I am logged out")
    public void iAmLoggedOut() {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl+"/logout");
    }
}


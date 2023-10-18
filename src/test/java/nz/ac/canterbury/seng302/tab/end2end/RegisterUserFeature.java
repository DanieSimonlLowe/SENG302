package nz.ac.canterbury.seng302.tab.end2end;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.LoadState;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class RegisterUserFeature {

    @Given("I connect to the system's main URL")
    public void i_connect_to_systems_main_url() {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl);
    }

    @Given("I am on the registration page")
    public void i_am_on_the_registration_page() {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/register");
        Assertions.assertEquals("http://" + PlaywrightBrowser.baseUrl + "/register", PlaywrightBrowser.page.url());
    }

    @When("I hit the button to register")
    public void i_hit_the_register_button() {
        PlaywrightBrowser.page.locator("#sign-up").click();
    }

    @When("I enter information {string} {string} {string} {string} {string} {string} {string} {string}")
    public void i_enter_information(String firstName, String lastName, String email, String city, String country, String dateOfBirth, String password, String confirmPassword) {
        PlaywrightBrowser.page.locator("#firstName").fill(firstName);
        PlaywrightBrowser.page.locator("#lastName").fill(lastName);
        String uniqueEmail = System.currentTimeMillis() + email;
        PlaywrightBrowser.page.locator("#email").fill(uniqueEmail);
        PlaywrightBrowser.page.locator("#DOB").fill(dateOfBirth);
        PlaywrightBrowser.page.locator("#password").fill(password);
        PlaywrightBrowser.page.locator("#confirmPassword").fill(confirmPassword);

        PlaywrightBrowser.page.locator("#form-city").fill(city);
        PlaywrightBrowser.page.locator("#form-country").fill(country);
    }

    @When("I click the register button")
    public void i_click_the_register_button() {
        PlaywrightBrowser.page.locator("#register-button").click();
    }



    @Then("I see a registration form")
    public void i_see_a_registration_form() {
        PlaywrightBrowser.page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        Assertions.assertEquals("http://" + PlaywrightBrowser.baseUrl + "/register", PlaywrightBrowser.page.url());
    }

    @Then("I am redirected to the login page with an email confirmation")
    public void i_am_redirected_to_the_login_page_with_an_email_confirmation() {
        PlaywrightBrowser.page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        Assertions.assertEquals("http://" + PlaywrightBrowser.baseUrl + "/login", PlaywrightBrowser.page.url());
        Assertions.assertNotEquals("", PlaywrightBrowser.page.locator("#login-info").textContent());
    }

    @Then("I stay on the registration page")
    public void i_stay_on_the_registration_page() {
        Assertions.assertEquals("http://" + PlaywrightBrowser.baseUrl + "/register", PlaywrightBrowser.page.url());
    }

    @Then("The error field {string} displays the text {string}")
    public void the_error_field_displays_the_text(String field, String message) {
        //PlaywrightBrowser.page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        Locator locator = PlaywrightBrowser.page.locator("#" + field);
        Assertions.assertNotNull(locator,"Failed to locate.");
        Assertions.assertEquals(message, locator.textContent());
    }


    @When("I hit the cancel button")
    public void iHitTheCancelButton() {
        PlaywrightBrowser.page.locator("#cancel-btn").click();
    }


    @Then("I am redirected to {string}")
    public void iAmRedirectedTo(String arg0) {
        Assertions.assertEquals("http://" + PlaywrightBrowser.baseUrl + arg0, PlaywrightBrowser.page.url());
    }

    @When("I enter information {string} {string} {string} {string} {string} {string} {string} {string} {string} {string} {string}")
    public void i_enter_information(String firstName, String lastName, String email, String city, String country, String dateOfBirth, String password, String confirmPassword, String address1, String postcode, String suburb) {
        PlaywrightBrowser.page.locator("#firstName").fill(firstName);
        PlaywrightBrowser.page.locator("#lastName").fill(lastName);
        String uniqueEmail = System.currentTimeMillis() + email;
        PlaywrightBrowser.page.locator("#email").fill(uniqueEmail);
        PlaywrightBrowser.page.locator("#DOB").fill(dateOfBirth);
        PlaywrightBrowser.page.locator("#password").fill(password);
        PlaywrightBrowser.page.locator("#confirmPassword").fill(confirmPassword);

        PlaywrightBrowser.page.locator("#form-address1").fill(address1);
        PlaywrightBrowser.page.locator("#form-postcode").fill(postcode);
        PlaywrightBrowser.page.locator("#form-suburb").fill(suburb);
        PlaywrightBrowser.page.locator("#form-city").fill(city);
        PlaywrightBrowser.page.locator("#form-country").fill(country);
    }
}

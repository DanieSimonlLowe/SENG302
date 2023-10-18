package nz.ac.canterbury.seng302.tab.end2end;

import com.microsoft.playwright.assertions.PlaywrightAssertions;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import static nz.ac.canterbury.seng302.tab.end2end.PlaywrightBrowser.page;

public class EditUserProfile {
    @When("I change my first name to {string}")
    public void i_change_my_first_name_to(String name) {
        PlaywrightBrowser.page.locator("#fName").fill(name);
    }

    @When("I enter incorrect {string} for First Name and Last Name")
    public void i_enter_incorrect_for_first_name_and_last_name(String name) {
        PlaywrightBrowser.page.locator("#fName").fill(name);
        PlaywrightBrowser.page.locator("#lName").fill(name);
        page.locator("#save-button").click();
    }

    @When("I enter incorrect {string} for email")
    public void i_enter_incorrect_for_email(String email) {
        PlaywrightBrowser.page.locator("#email").fill(email);
        page.locator("#save-button").click();
    }

    @When("I enter incorrect details for DOB")
    public void i_enter_incorrect_details_for_DOB() {
        PlaywrightBrowser.page.locator("#DOB").fill("2023-02-09");
        page.locator("#save-button").click();
    }

    @When("I enter a email that already exists")
    public void i_enter_a_email_that_already_exists() {
        PlaywrightBrowser.page.locator("#email").fill("notReal@uclive.ac.nz");
        page.locator("#save-button").click();
    }

    @Then("an error message tells me that the email is already in use")
    public void an_error_message_tells_me_that_the_email_is_already_in_use() {
        PlaywrightAssertions.assertThat(PlaywrightBrowser.page.getByText("Email is already in use")).isVisible();
    }

    @Then("an error message tells me that the DOB is invalid")
    public void an_error_message_tells_me_that_the_DOB_is_invalid() {
        PlaywrightAssertions.assertThat(PlaywrightBrowser.page.getByText("Enter a valid DOB. Must be 13 years or older")).isVisible();
    }

    @Then("an error message tells me that the email is invalid")
    public void an_error_message_tells_me_that_the_email_is_invalid() {
        PlaywrightAssertions.assertThat(PlaywrightBrowser.page.getByText("Invalid email")).isVisible();
    }

    @Then("an error message tells me I must have a valid first name and last name")
    public void an_error_message_tells_me_i_must_have_a_valid_first_name_and_last_name(){
        PlaywrightAssertions.assertThat(PlaywrightBrowser.page.getByText("First name is not valid")).isVisible();
        PlaywrightAssertions.assertThat(PlaywrightBrowser.page.getByText("Last name is not valid")).isVisible();
    }

    @Then("My name is stays the same")
    public void myNameIsStaysTheSame() {
        String text = PlaywrightBrowser.page.locator("#fName").innerText();
        Assertions.assertEquals("Morgan English", text);
    }

    @Then("The edit fields have correct values")
    public void theEditFieldsHaveCorrectValues() {
        String fName = PlaywrightBrowser.page.locator("#fName").inputValue();
        Assertions.assertEquals("Morgan",fName);

        String lName = PlaywrightBrowser.page.locator("#lName").inputValue();
        Assertions.assertEquals("English",lName);

        String email = PlaywrightBrowser.page.locator("#email").inputValue();
        Assertions.assertEquals("morgan.english@hotmail.com",email);

        String DOT = PlaywrightBrowser.page.locator("#DOB").inputValue();
        Assertions.assertEquals("2004-02-09",DOT);

        String location = PlaywrightBrowser.page.locator("#form-address1").inputValue();
        Assertions.assertEquals("",location);
        String city = PlaywrightBrowser.page.locator("#form-city").inputValue();
        Assertions.assertEquals("Christchurch",city);
        String country = PlaywrightBrowser.page.locator("#form-country").inputValue();
        Assertions.assertEquals("New Zealand",country);

        String favSports = PlaywrightBrowser.page.locator("#favSportsDisplay").innerHTML();
        Assertions.assertEquals("",favSports);
    }
}

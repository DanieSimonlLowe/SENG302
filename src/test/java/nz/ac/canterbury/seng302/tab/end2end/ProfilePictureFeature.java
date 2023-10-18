package nz.ac.canterbury.seng302.tab.end2end;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProfilePictureFeature {
    private static final String userDir = System.getProperty("user.dir");

    @When("I choose a new profile picture {string}")
    public void I_choose_a_new_profile_picture(String file) {
        Path path = Paths.get(userDir + File.separator + "public" + File.separator + "testFiles" + File.separator + file);
        PlaywrightBrowser.page.locator("#file").setInputFiles(path);
    }

    @Then("an error message tells me the file is too big")
    public void anErrorMessageTellsMeTheFileIsTooBig() {
        Assertions.assertFalse(PlaywrightBrowser.page.locator("#fileSizeErrorDiv").isHidden());
    }

    @Then("The profile image displays correctly")
    public void theProfileImageDisplaysCorrectly() {
        Locator img = PlaywrightBrowser.page.locator("#ProfileImage0");
        String link = img.getAttribute("src");
        Assertions.assertEquals(Path.of("profilePics/userProfile-8.png"),Path.of(link));
    }

    @When("I click my profile picture")
    public void i_click_my_profile_picture() {
        PlaywrightBrowser.page.locator("#profilePicActivator").click();
    }

    @Then("a file picker is shown")
    public void a_file_picker_is_shown() {
        Page page = PlaywrightBrowser.page;
        AtomicBoolean isButtonClicked = new AtomicBoolean(false);

        page.exposeBinding("trackButtonClick", (source, args1) -> {
            isButtonClicked.set(true);
            return null;
        });
        page.evaluate("document.querySelector('#file').addEventListener('click', () => window.trackButtonClick());");
        assert isButtonClicked.get();
    }

    @Then("an error message tells me the file is not of an acceptable format")
    public void an_error_message_tells_me_the_file_is_not_of_an_acceptable_format() {
        Assertions.assertFalse(PlaywrightBrowser.page.locator("#errorDiv").isHidden());
    }
}

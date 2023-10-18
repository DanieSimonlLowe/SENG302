package nz.ac.canterbury.seng302.tab.end2end;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.tab.entity.Team;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static nz.ac.canterbury.seng302.tab.end2end.PlaywrightBrowser.baseUrl;
import static nz.ac.canterbury.seng302.tab.end2end.PlaywrightBrowser.page;

public class FollowingTeams {



    @Given("I am following a team")
    public void i_am_following_a_team() {
        PlaywrightBrowser.page.navigate(baseUrl+"/teamProfile?id=13");
        if (!PlaywrightBrowser.page.locator("#friendsDisplay").isVisible()) {
            PlaywrightBrowser.page.locator("#teamFollowBtn").click();
        }
    }

    @When("I open my profile page")
    public void i_open_my_profile_page() {
        PlaywrightBrowser.page.navigate(baseUrl+"/profile");
        page.locator("#following-tab").click();
    }

    @Then("I can see the team I am following")
    public void i_can_see_the_team_I_am_following() {
        String followedTeams = page.locator("#followingTeamTableBody").innerHTML();
        Pattern pattern = Pattern.compile("<tr>");
        Matcher matcher = pattern.matcher(followedTeams);
        int numRows = 0;
        while (matcher.find()) {
            numRows++;
        }
        Assertions.assertTrue(1 <= numRows);
    }
}

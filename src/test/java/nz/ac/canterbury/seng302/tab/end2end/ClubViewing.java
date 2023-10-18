package nz.ac.canterbury.seng302.tab.end2end;

import com.microsoft.playwright.Locator;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.Arrays;

import static nz.ac.canterbury.seng302.tab.end2end.PlaywrightBrowser.baseUrl;
import static nz.ac.canterbury.seng302.tab.end2end.PlaywrightBrowser.page;

public class ClubViewing {
    String oldSearch;

    @Given("I am searching for a team")
    public void i_am_searching_for_a_team() {
        PlaywrightBrowser.page.navigate(baseUrl + "/allTeams");
    }

    @When("I see a team {int}")
    public void i_see_a_team_that_belongs_to_a_club(int number) {
        Assertions.assertTrue(PlaywrightBrowser.page.locator("#team_"+number+"_name").isVisible());
    }

    @Given("There is a UI element that displays the team {int} is a part of {string}")
    public void there_is_a_ui_element_that_displays_what_club_the_team_is_a_part_of(int id, String clubName) {
        Locator locator = PlaywrightBrowser.page.locator("#team_"+id+"_club");
        if (clubName.equals("")) {
            Assertions.assertFalse(locator.isVisible());
        } else {
            Assertions.assertEquals("Member of " + clubName, locator.innerText());
            Assertions.assertTrue(locator.isVisible());
        }
    }

    @When("I am on the profile page for a team with a club")
    public void i_am_on_the_profile_page_for_a_team_with_a_club() {
        PlaywrightBrowser.page.navigate(baseUrl + "/teamProfile?id=7");
    }


    @When("I enter a clubs search string of {string}")
    public void iEnterAClubsSearchStringOfSearch(String search) {
        oldSearch = search;
        page.locator("#clubSearch").fill(search);
    }

    @And("I click the clubs search submit button")
    public void iClickTheClubsSearchSubmitButton() {
        page.locator("#searchButton").click();
    }

    @Then("I see the name of the club")
    public void i_see_the_name_of_the_club() {
        Assertions.assertTrue(page.locator("#clubName").isVisible());
    }

    @Then("There is a link to the club")
    public void there_is_a_link_to_the_club() {
        page.locator("#clubName").click();
        Assertions.assertTrue(page.url().contains("/clubProfile?clubId=4"));
    }

    @Then("The only the clubs {string} are shown")
    public void theOnlyTheClubsAreShown(String clubStr) {
        String[] clubs = clubStr.split(",");

        page.waitForSelector(".card-title");
        Locator locator = page.locator(".card-title");

        Assertions.assertEquals(clubs.length,locator.count());

        for (int i = 0; i<locator.count(); i++) {
            String text = locator.nth(i).innerText();
            Assertions.assertTrue(text.contains(clubs[i]),text + "\n" + clubs[i]);
        }
    }

    @When("I select cities {string}")
    public void iSelectCitiesCities(String citiesStr) {
        if (citiesStr.length() == 0) {
            return;
        }
        page.locator("#cityDropdown").click();

        String[]  cities = citiesStr.split(",");

        for (String city : cities) {
            page.getByLabel(city).click();
        }
        page.locator("#cityDropdown").click();
    }


    @When("I select sports {string}")
    public void iSelectSports(String citiesStr) {
        if (citiesStr.length() == 0) {
            return;
        }
        page.locator("#sportDropdown").click();

        String[]  cities = citiesStr.split(",");

        for (String city : cities) {
            page.getByLabel(city).click();
        }
        page.locator("#sportDropdown").click();
    }

    @When("I click the filter button")
    public void iClickTheFilterButton() {
        page.locator("#filterButton").click();
    }

    @When("I click the clubs tab")
    public void iClickTheClubsTab() {
        page.locator("#clubsTab").click();
    }

    @When("I click the teams tab")
    public void iClickTheTeamsTab() {
        page.locator("#teamsTab").click();
    }

    @Given("I am on the profile club page for {int}")
    public void iAmOnTheProfileClubPageForId(int id) {
        page.navigate(baseUrl+"/clubProfile?clubId=" + id);
    }

    @Then("The teams {string} are displayed in the clubs profile")
    public void theTeamsStringAreDisplayedInTheClubsProfile(String teamsStr) {
        String[] teams = teamsStr.split(",");
        Locator locator = page.locator(".clubNameSpan");
        int count = locator.count();

        Assertions.assertEquals(teams.length,count);

        for (int i = 0; i < count; i++) {
            String text = locator.nth(i).innerText();
            Assertions.assertTrue(Arrays.asList(teams).contains(text), text);
        }
    }
}

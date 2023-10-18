package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.service.sorters.TeamSorter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
public class TeamSorterTest {
    Team team1;
    Team team2;
    ArrayList<Team> teams;

    @BeforeEach
    void setup() {
        team1 = new Team(
                "TrailBlazers",
                new Location("", "", "", "", "Portland", "United States of America"),
                "Basketball"
        );
        team2 = new Team(
                "Heat",
                new Location("", "", "", "", "Miami", "United States of America"),
                "Basketball"
        );
        teams = new ArrayList<>();
        teams.add(team1);
        teams.add(team2);
    }

    @Test
    void alphabetical_not_match_locations_test() {
        TeamSorter.sort(teams);
        Assertions.assertSame(team2, teams.get(0));
    }

    @Test
    void alphabetical_not_match_locations_same_letter_test() {
        team1.setName("AB");
        team2.setName("AC");
        TeamSorter.sort(teams);
        Assertions.assertSame(team1, teams.get(0));
    }

    @Test
    void alphabetical_match_locations_test() {
        team1.setName("A");
        team2.setName("B");
        Team team3 = new Team("C",
                new Location("", "", "", "", "Portland", "United States of America"),
                "Basketball");
        teams.add(team3);
        TeamSorter.sort(teams);
        Assertions.assertSame(team1, teams.get(0));
        Assertions.assertSame(team3, teams.get(1));
    }
}

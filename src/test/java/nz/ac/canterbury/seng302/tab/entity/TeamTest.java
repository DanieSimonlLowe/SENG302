package nz.ac.canterbury.seng302.tab.entity;

import nz.ac.canterbury.seng302.tab.service.TeamService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

public class TeamTest {
    Location location;
    Team team;
    @BeforeEach
    void setup() {
        location = Mockito.mock(Location.class);
        team = new Team("team",location,"sport");
    }

    @Test
    void suggestionMetric_max_test() {
        User user = Mockito.mock(User.class);
        Location location1 = Mockito.mock(Location.class);
        Mockito.when(user.getLocation()).thenReturn(location1);
        Mockito.when(location1.isSameCity(location)).thenReturn(true);

        ArrayList<Sport> sports = new ArrayList<>();
        Sport sport = Mockito.mock(Sport.class);
        Mockito.when(sport.getSportName()).thenReturn("sport");
        sports.add(sport);
        Mockito.when(user.getSports()).thenReturn(sports);

        TeamService teamService = Mockito.mock(TeamService.class);
        Mockito.when(teamService.getPercentOfUsersWhoShareTeam(user,team)).thenReturn(1.0f);

        Assertions.assertEquals(250,team.getSuggestionMetric(user,teamService),0.01);
    }


    @Test
    void suggestionMetric_sameCountry_noSport_test() {
        User user = Mockito.mock(User.class);
        Location location1 = Mockito.mock(Location.class);
        Mockito.when(user.getLocation()).thenReturn(location1);
        Mockito.when(location1.isSameCity(location)).thenReturn(false);
        Mockito.when(location1.getCountry()).thenReturn("cont");
        Mockito.when(location.getCountry()).thenReturn("cont");

        ArrayList<Sport> sports = new ArrayList<>();
        Mockito.when(user.getSports()).thenReturn(sports);

        TeamService teamService = Mockito.mock(TeamService.class);
        Mockito.when(teamService.getPercentOfUsersWhoShareTeam(user,team)).thenReturn(0.5f);

        Assertions.assertEquals(60,team.getSuggestionMetric(user,teamService),0.01);
    }

    @Test
    void suggestionMetric_min_test() {
        User user = Mockito.mock(User.class);
        Location location1 = Mockito.mock(Location.class);
        Mockito.when(user.getLocation()).thenReturn(location1);
        Mockito.when(location1.isSameCity(location)).thenReturn(false);
        Mockito.when(location1.getCountry()).thenReturn("cont");
        Mockito.when(location.getCountry()).thenReturn("con");

        ArrayList<Sport> sports = new ArrayList<>();
        Mockito.when(user.getSports()).thenReturn(sports);

        TeamService teamService = Mockito.mock(TeamService.class);
        Mockito.when(teamService.getPercentOfUsersWhoShareTeam(user,team)).thenReturn(0.0f);

        Assertions.assertEquals(0,team.getSuggestionMetric(user,teamService),0.01);
    }
}

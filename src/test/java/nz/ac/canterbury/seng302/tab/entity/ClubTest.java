package nz.ac.canterbury.seng302.tab.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

class ClubTest {

    Location location;
    List<Team> teams;

    User user;

    Club club;

    @BeforeEach
    void setup() {
        location = Mockito.mock(Location.class);
        teams = Mockito.mock(List.class);
        user = Mockito.mock(User.class);
        Mockito.when(location.toString()).thenReturn("loc");
        club = new Club(user,"name",location,teams);
    }

    @Test
    void create_test() {
        Mockito.when(user.getId()).thenReturn(100L);

        Assertions.assertEquals(teams,club.getTeams());
        Assertions.assertEquals(100L,club.getManager());
        Assertions.assertEquals(location,club.getLocation());
        Assertions.assertEquals("name",club.getName());
        Assertions.assertEquals("images/default-img.png",club.getProfilePicName());
    }

    @Test
    void getSport() {
        Mockito.when(teams.isEmpty()).thenReturn(false);
        Team team = Mockito.mock(Team.class);
        Mockito.when(team.getSport()).thenReturn("sport");
        Mockito.when(teams.get(0)).thenReturn(team);

        Assertions.assertEquals("sport",club.getSport());

    }


}

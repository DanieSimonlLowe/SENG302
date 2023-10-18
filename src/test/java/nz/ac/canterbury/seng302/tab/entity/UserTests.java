package nz.ac.canterbury.seng302.tab.entity;

import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Stream;

public class UserTests {
    @Test
    public void createUserTest() {
        LocalDate date1 = LocalDate.now();
        Location location = new Location("", "", "", "", "test", "test");
        User user = new TokenGenerator("e", "n", "l", date1, location, "pas", "register").generateUser();
        Assertions.assertEquals("e", user.getEmail());
        Assertions.assertEquals("n", user.getFirstName());
        Assertions.assertEquals("l", user.getLastName());
        Assertions.assertEquals(date1, user.getDate());
        Assertions.assertSame(location, user.getLocation());
        Assertions.assertTrue(user.samePassword("pas"));

        LocalDate date2 = LocalDate.now().minusMonths(4);
        location = new Location("", "", "", "", "test2", "test2");
        user = new TokenGenerator("e1", "n1", "l1", date2, location, "pas1", "register").generateUser();
        Assertions.assertEquals("e1", user.getEmail());
        Assertions.assertEquals("n1", user.getFirstName());
        Assertions.assertEquals("l1", user.getLastName());
        Assertions.assertEquals(date2, user.getDate());
        Assertions.assertEquals(location, user.getLocation());
        Assertions.assertTrue(user.samePassword("pas1"));
    }

    @Test
    public void diffPasswordTest() {
        Location location = new Location("", "", "", "", "test", "test");
        User user = new TokenGenerator("e", "n", "l", LocalDate.now(), location, "password", "register").generateUser();

        Assertions.assertTrue(user.samePassword("password"));
        Assertions.assertFalse(user.samePassword("passwor"));
        Assertions.assertFalse(user.samePassword("passwordd"));
        Assertions.assertFalse(user.samePassword("passworb"));
        Assertions.assertFalse(user.samePassword("passwor12"));
    }

    @Test
    public void samePasswordTest() {
        Location location = new Location("", "", "", "", "test", "test");
        User user1 = new TokenGenerator("e", "n", "l", LocalDate.now(), location, "blob", "register").generateUser();

        for (int i = 0; i < 10; i++) {
            User user2 = new TokenGenerator("e", "n", "l", LocalDate.now(), location, "blob", "register").generateUser();
            Assertions.assertNotEquals(user1.getPasswordHash(), user2.getPasswordHash());
        }
    }

    @Test
    public void defaultActiveTest() {
        Location location = new Location("", "", "", "", "test", "test");
        User user1 = new TokenGenerator("e", "n", "l", LocalDate.now(), location, "blob","register").generateUser();
        Assertions.assertTrue(user1.getIsActive());
    }

    @Test void canDeactivateTest() {
        Location location = new Location("", "", "", "", "test", "test");
        User user1 = new TokenGenerator("e", "n", "l", LocalDate.now(), location, "blob","register").generateUser();
        user1.deactivate();
        Assertions.assertFalse(user1.getIsActive());
    }

    @Test void canReactivateTest() {
        Location location = new Location("", "", "", "", "test", "test");
        User user1 = new TokenGenerator("e", "n", "l", LocalDate.now(), location, "blob","register").generateUser();
        user1.deactivate();
        user1.activate();
        Assertions.assertTrue(user1.getIsActive());
    }

    @Test
    void getPrivacyType_default() {
        Location location = new Location("", "", "", "", "test", "test");
        User user1 = new TokenGenerator("e", "n", "l", LocalDate.now(), location, "blob","register").generateUser();
        Assertions.assertEquals(PrivacyType.PRIVATE,user1.getPrivacyType());
    }

    private static Stream<Arguments> allPrivacyTypes() {
        return Stream.of(
                Arguments.of(PrivacyType.FREINDS_ONLY),
                Arguments.of(PrivacyType.PUBLIC),
                Arguments.of(PrivacyType.PRIVATE)
        );
    }

    @Test
    void checkViewability_privateUser() {
        Location location = new Location("", "", "", "", "test", "test");
        User user1 = new TokenGenerator("e", "n", "l", LocalDate.now(), location, "blob","register").generateUser();
        Assertions.assertFalse(user1.isViewable(false));
    }

    @Test
    void checkViewability_publicUser() {
        Location location = new Location("", "", "", "", "test", "test");
        User user1 = new TokenGenerator("e", "n", "l", LocalDate.now(), location, "blob","register").generateUser();
        user1.setPrivacyType(PrivacyType.PUBLIC);
        Assertions.assertTrue(user1.isViewable(false));
    }

    @Test
    void checkViewability_friendsOnly_isFriend() {
        Location location = new Location("", "", "", "", "test", "test");
        User user1 = new TokenGenerator("e", "n", "l", LocalDate.now(), location, "blob","register").generateUser();
        user1.setPrivacyType(PrivacyType.FREINDS_ONLY);
        Assertions.assertTrue(user1.isViewable(true));
    }

    @Test
    void checkViewability_friendsOnly_isNotFriend() {
        Location location = new Location("", "", "", "", "test", "test");
        User user1 = new TokenGenerator("e", "n", "l", LocalDate.now(), location, "blob","register").generateUser();
        user1.setPrivacyType(PrivacyType.FREINDS_ONLY);
        Assertions.assertFalse(user1.isViewable(false));
    }

    @Test
    void follow_sameUser() {
        Location location = new Location("", "", "", "", "test", "test");
        User user1 = new TokenGenerator("e", "n", "l", LocalDate.now(), location, "blob","register").generateUser();
        User user2 = Mockito.mock(User.class);
        User user3 = Mockito.mock(User.class);
        Mockito.when(user2.getId()).thenReturn(1L);
        Mockito.when(user3.getId()).thenReturn(1L);

        Assertions.assertFalse(user1.isFollowing(user3));
        user1.follow(user2);
        Assertions.assertTrue(user1.isFollowing(user3));
    }

    @Test
    void unfollow_givenUser() {
        Location location = new Location("", "", "", "", "test", "test");
        User user1 = new TokenGenerator("e", "n", "l", LocalDate.now(), location, "blob","register").generateUser();
        User user2 = Mockito.mock(User.class);

        Mockito.when(user2.getId()).thenReturn(1L);

        user1.follow(user2);

        Assertions.assertTrue(user1.isFollowing(user2));
        user1.unfollow(user2);

        Assertions.assertFalse(user1.isFollowing(user2));
    }

    @Test
    void unfollow_notAffectOther() {
        Location location = new Location("", "", "", "", "test", "test");
        User user1 = new TokenGenerator("e", "n", "l", LocalDate.now(), location, "blob","register").generateUser();
        User user2 = Mockito.mock(User.class);
        User user3 = Mockito.mock(User.class);

        Mockito.when(user2.getId()).thenReturn(1L);
        Mockito.when(user3.getId()).thenReturn(2L);

        user1.follow(user2);
        user1.follow(user3);

        Assertions.assertTrue(user1.isFollowing(user2));
        Assertions.assertTrue(user1.isFollowing(user3));
        user1.unfollow(user2);

        Assertions.assertFalse(user1.isFollowing(user2));
        Assertions.assertTrue(user1.isFollowing(user3));
    }

    @Test
    void follow_diffUser() {
        Location location = new Location("", "", "", "", "test", "test");
        User user1 = new TokenGenerator("e", "n", "l", LocalDate.now(), location, "blob","register").generateUser();
        User user2 = Mockito.mock(User.class);
        User user3 = Mockito.mock(User.class);
        Mockito.when(user2.getId()).thenReturn(1L);
        Mockito.when(user3.getId()).thenReturn(2L);

        user1.follow(user2);
        Assertions.assertFalse(user1.isFollowing(user3));
    }

    @ParameterizedTest
    @MethodSource("allPrivacyTypes")
    void getPrivacyType(PrivacyType privacyType) {
        Location location = new Location("", "", "", "", "test", "test");
        User user1 = new TokenGenerator("e", "n", "l", LocalDate.now(), location, "blob","register").generateUser();
        user1.setPrivacyType(privacyType);
        Assertions.assertEquals(privacyType,user1.getPrivacyType());
    }

    @Test
    void getSuggestionMetric_noSport_test() {
        Location location1 = Mockito.mock(Location.class);
        Location location2 = Mockito.mock(Location.class);
        Mockito.when(location1.isSameCity(location2)).thenReturn(true);

        User user1 = Mockito.mock(User.class);
        Mockito.when(user1.getSports()).thenReturn(new ArrayList<>());
        Mockito.when(user1.getLocation()).thenReturn(location1);

        User user = new User("email","fn","ln",LocalDate.EPOCH,location2,"");

        UserService userService = Mockito.mock(UserService.class);
        Mockito.when(userService.countSameTeamMemberShip(user,user1)).thenReturn(7);
        Mockito.when(userService.hasSameClubMemberShip(user,user1)).thenReturn(true);

        Assertions.assertEquals(60,user.getSuggestionMetric(user1,userService));
    }


    @Test
    void getSuggestionMetric_zero_test() {
        Location location1 = Mockito.mock(Location.class);
        Location location2 = Mockito.mock(Location.class);
        Mockito.when(location1.isSameCity(location2)).thenReturn(false);

        User user1 = Mockito.mock(User.class);
        Mockito.when(user1.getSports()).thenReturn(new ArrayList<>());
        Mockito.when(user1.getLocation()).thenReturn(location1);

        User user = new User("email","fn","ln",LocalDate.EPOCH,location2,"");

        UserService userService = Mockito.mock(UserService.class);
        Mockito.when(userService.countSameTeamMemberShip(user,user1)).thenReturn(0);
        Mockito.when(userService.hasSameClubMemberShip(user,user1)).thenReturn(false);

        Assertions.assertEquals(0,user.getSuggestionMetric(user1,userService));
    }

    @Test
    void getSuggestionMetric_hasSport_test() {
        Location location1 = Mockito.mock(Location.class);
        Location location2 = Mockito.mock(Location.class);
        Mockito.when(location1.isSameCity(location2)).thenReturn(false);

        Sport sport1 = Mockito.mock(Sport.class);
        Mockito.when(sport1.getId()).thenReturn(1L);
        Sport sport2 = Mockito.mock(Sport.class);
        Mockito.when(sport2.getId()).thenReturn(2L);
        Sport sport3 = Mockito.mock(Sport.class);
        Mockito.when(sport3.getId()).thenReturn(3L);
        Sport sport1Copy = Mockito.mock(Sport.class);
        Mockito.when(sport1Copy.getId()).thenReturn(1L);
        Sport sport4 = Mockito.mock(Sport.class);
        Mockito.when(sport4.getId()).thenReturn(4L);

        ArrayList<Sport> sports = new ArrayList<>();
        sports.add(sport1);
        sports.add(sport2);
        sports.add(sport3);


        User user1 = Mockito.mock(User.class);
        Mockito.when(user1.getSports()).thenReturn(sports);
        Mockito.when(user1.getLocation()).thenReturn(location1);

        User user = new User("email","fn","ln",LocalDate.EPOCH,location2,"");
        ArrayList<Sport> sports2 = new ArrayList<>();
        sports2.add(sport3);
        sports2.add(sport1Copy);
        sports2.add(sport4);
        user.setSports(sports2);

        UserService userService = Mockito.mock(UserService.class);
        Mockito.when(userService.countSameTeamMemberShip(user,user1)).thenReturn(0);
        Mockito.when(userService.hasSameClubMemberShip(user,user1)).thenReturn(false);

        Assertions.assertEquals(20,user.getSuggestionMetric(user1,userService));
    }
}

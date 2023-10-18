package nz.ac.canterbury.seng302.tab.service;


import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.AuthorityRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Transactional
@WithAnonymousUser
public class UserServiceTest {
    UserService userService;
    User newUser;
    User newUser2;
    @Mock
    UserRepository repository;
    @Mock
    AuthorityRepository authorityRepository;



    @BeforeEach
    void setup() {
        LocalDate date = LocalDate.now();
        Location testLocation = new Location("", "", "Ilam", "", "Christchurch", "New Zealand");
        newUser = new User("newUser@email.com", "testFirstName", "testLastName", date, testLocation, "testHash");
        newUser2 = new User("newUser2@email.com", "firstName", "lastName", date, testLocation, "testHash");
        userService = new UserService(repository, authorityRepository);
    }

    @Test
    void addingNewUser_validNewUser_addSuccessfully() {
        Mockito.when(repository.findByEmailIgnoreCase("newUser@email.com")).thenReturn(newUser);
        Assertions.assertNotNull(userService.getUserByEmail("newUser@email.com"));
    }

    @Test
    void searchingByName_searchByExistingFirstName_userFound() {
        List<User> list1 = new ArrayList<>();
        list1.add(newUser);
        Mockito.when(repository.findUsersByFirstNameContainsIgnoreCase("testFirstName")).thenReturn(list1);
        Assertions.assertEquals(1, userService.findByFirstOrLastName("testFirstName").size());
    }

    @Test
    void searchingByName_searchByExistingLastName_userFound() {
        List<User> list1 = new ArrayList<>();
        list1.add(newUser);
        Mockito.when(repository.findUsersByLastNameContainsIgnoreCase("testLastName")).thenReturn(list1);
        Assertions.assertEquals(1, userService.findByFirstOrLastName("testLastName").size());
    }

    @Test
    void searchingByName_searchByNonExistentName_noUserFound() {
        Assertions.assertEquals(0, userService.findByFirstOrLastName("haha").size());
    }

    @Test
    void searchingByName_searchByDuplicateName_multipleFound() {
        List<User> list1 = new ArrayList<>();
        list1.add(newUser);
        list1.add(newUser2);
        Mockito.when(repository.findUsersByFirstNameContainsIgnoreCase("first")).thenReturn(list1);
        Assertions.assertEquals(2, userService.findByFirstOrLastName("first").size());
    }

    @Test
    void searchingByEmail_searchByUniqueEmail_oneFound() {
        Mockito.when(repository.findByEmailIgnoreCase("thisisanemail@email.com")).thenReturn(newUser);
        Assertions.assertEquals(userService.getUserByEmail("thisisanemail@email.com"), newUser);
    }

    @Test
    void searchingByEmail_dontFindDeactivateUser() {
        newUser.deactivate();
        Mockito.when(repository.findByEmailIgnoreCase("thisisanemail@email.com")).thenReturn(newUser);
        Assertions.assertNull(userService.getUserByEmail("thisisanemail@email.com"));
    }

    @Test
    void searchingByEmail_findReactivatedUser() {
        newUser.deactivate();
        newUser.activate();
        Mockito.when(repository.findByEmailIgnoreCase("thisisanemail@email.com")).thenReturn(newUser);
        Assertions.assertEquals(userService.getUserByEmail("thisisanemail@email.com"), newUser);
    }

    @Test
    void findUsersWhoAreInTeam_test() {
        Team team = Mockito.mock(Team.class);
        Mockito.when(team.getId()).thenReturn(0L);
        Mockito.when(repository.findUsersWhoAreInTeam(0)).thenReturn(new ArrayList<>());

        Assertions.assertEquals(0,userService.findUsersWhoAreInTeam(team).size());
    }

    @Test
    void findFollowedUsersByUserId() {
        Mockito.when(repository.findFollowedUsersByUserId(0L)).thenReturn(new ArrayList<>());
        Assertions.assertEquals(0,userService.findFollowedUsersByUserId(0L).size());
    }

    @Test
    void getFriends_noFriends() {
        User user = Mockito.mock(User.class);
        Mockito.when(user.getId()).thenReturn(0L);
        Mockito.when(repository.findFriends(user.getId())).thenReturn(new ArrayList<>());
        Assertions.assertEquals(0,userService.getFriends(user).size());
    }

    @Test
    void getFriends_hasFriends() {
        User user = Mockito.mock(User.class);
        Mockito.when(user.getId()).thenReturn(0L);
        List<Long> friends = new ArrayList<>();
        friends.add(1L);
        Mockito.when(repository.findFriends(user.getId())).thenReturn(friends);
        Mockito.when(repository.findById(1L)).thenReturn(java.util.Optional.of(user));
        Assertions.assertEquals(1,userService.getFriends(user).size());
    }

    @Test
    void areFriends_areFriends() {
        User user1 = Mockito.mock(User.class);
        User user2 = Mockito.mock(User.class);
        Mockito.when(user1.getId()).thenReturn(0L);
        Mockito.when(user2.getId()).thenReturn(1L);
        List<Long> friends = new ArrayList<>();
        friends.add(1L);
        Mockito.when(repository.findFriends(user1.getId())).thenReturn(friends);
        Mockito.when(repository.findById(1L)).thenReturn(java.util.Optional.of(user2));
        Assertions.assertTrue(userService.areFriends(user1,user2));
    }

    @Test
    void areFriends_areNotFriends() {
        User user1 = Mockito.mock(User.class);
        User user2 = Mockito.mock(User.class);
        Mockito.when(user1.getId()).thenReturn(0L);
        Mockito.when(user2.getId()).thenReturn(1L);
        List<Long> friends = new ArrayList<>();
        Mockito.when(repository.findFriends(user1.getId())).thenReturn(friends);
        Mockito.when(repository.findById(1L)).thenReturn(java.util.Optional.of(user2));
        Assertions.assertFalse(userService.areFriends(user1,user2));
    }

    @Test
    void findUsersFromUserIds() {
        User user1 = Mockito.mock(User.class);
        User user2 = Mockito.mock(User.class);
        Mockito.when(user1.getId()).thenReturn(0L);
        Mockito.when(user2.getId()).thenReturn(1L);
        List<Long> ids = new ArrayList<>();
        ids.add(0L);
        ids.add(1L);
        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        Mockito.when(repository.findAllById(ids)).thenReturn(users);
        Assertions.assertEquals(2,userService.findUserFromId(ids).size());
    }

    @Test
    void countSameTeamMemberShip_test() {
        User user1 = Mockito.mock(User.class);
        User user2 = Mockito.mock(User.class);
        Mockito.when(user1.getId()).thenReturn(0L);
        Mockito.when(user2.getId()).thenReturn(1L);
        Mockito.when(repository.countTeamsInCommon(0L,1L)).thenReturn(1541);
        Assertions.assertEquals(1541,userService.countSameTeamMemberShip(user1,user2));
    }

    @Test
    void hasSameClubMemberShip_true_test() {
        User user1 = Mockito.mock(User.class);
        User user2 = Mockito.mock(User.class);
        Mockito.when(user1.getId()).thenReturn(0L);
        Mockito.when(user2.getId()).thenReturn(1L);
        Mockito.when(repository.hasClubInCommon(0L,1L)).thenReturn(1541);
        Assertions.assertTrue(userService.hasSameClubMemberShip(user1, user2));
    }

    @Test
    void hasSameClubMemberShip_false_test() {
        User user1 = Mockito.mock(User.class);
        User user2 = Mockito.mock(User.class);
        Mockito.when(user1.getId()).thenReturn(0L);
        Mockito.when(user2.getId()).thenReturn(1L);
        Mockito.when(repository.hasClubInCommon(0L,1L)).thenReturn(0);
        Assertions.assertFalse(userService.hasSameClubMemberShip(user1, user2));
    }


    @Test
    void getRecommendedUsers_onlyThreeUsers() {
        User user = Mockito.mock(User.class);
        Mockito.when(user.getId()).thenReturn(0L);
        User user1 = Mockito.mock(User.class);
        Mockito.when(user1.getSuggestionMetric(user,userService)).thenReturn(10.0F);
        Mockito.when(user.getId()).thenReturn(1L);
        User user2 = Mockito.mock(User.class);
        Mockito.when(user2.getSuggestionMetric(user,userService)).thenReturn(1.0F);
        Mockito.when(user.getId()).thenReturn(2L);
        User user3 = Mockito.mock(User.class);
        Mockito.when(user3.getSuggestionMetric(user,userService)).thenReturn(8.0F);
        Mockito.when(user.getId()).thenReturn(3L);

        ArrayList<User> users = new ArrayList<>();
        users.add(user);
        users.add(user1);
        users.add(user2);
        users.add(user3);
        Mockito.when(repository.findAll()).thenReturn(users);

        User[] out = userService.getRecommendedUsers(user);
        Assertions.assertEquals(3,out.length);
        Assertions.assertEquals(user1,out[0]);
        Assertions.assertEquals(user2,out[1]);
        Assertions.assertEquals(user3,out[2]);
    }


    @Test
    void getRecommendedUsers_firstThreeBest() {
        User user = Mockito.mock(User.class);
        Mockito.when(user.getId()).thenReturn(0L);
        User user1 = Mockito.mock(User.class);
        Mockito.when(user1.getSuggestionMetric(user,userService)).thenReturn(10.0F);
        Mockito.when(user.getId()).thenReturn(1L);
        User user2 = Mockito.mock(User.class);
        Mockito.when(user2.getSuggestionMetric(user,userService)).thenReturn(10.0F);
        Mockito.when(user.getId()).thenReturn(2L);
        User user3 = Mockito.mock(User.class);
        Mockito.when(user3.getSuggestionMetric(user,userService)).thenReturn(80.0F);
        Mockito.when(user.getId()).thenReturn(3L);

        User user4 = Mockito.mock(User.class);
        Mockito.when(user4.getSuggestionMetric(user,userService)).thenReturn(7.0F);
        Mockito.when(user.getId()).thenReturn(1L);
        User user5 = Mockito.mock(User.class);
        Mockito.when(user5.getSuggestionMetric(user,userService)).thenReturn(5.0F);
        Mockito.when(user.getId()).thenReturn(2L);
        User user6 = Mockito.mock(User.class);
        Mockito.when(user6.getSuggestionMetric(user,userService)).thenReturn(2.0F);
        Mockito.when(user.getId()).thenReturn(3L);

        ArrayList<User> users = new ArrayList<>();
        users.add(user);
        users.add(user1);
        users.add(user2);
        users.add(user3);
        users.add(user4);
        users.add(user5);
        users.add(user6);
        Mockito.when(repository.findAll()).thenReturn(users);

        User[] out = userService.getRecommendedUsers(user);
        Assertions.assertEquals(3,out.length);
        Assertions.assertEquals(user1,out[0]);
        Assertions.assertEquals(user2,out[1]);
        Assertions.assertEquals(user3,out[2]);
    }

    boolean arrayContainsUser(User user, User[] users) {
        for (User value : users) {
            if (value == user) {
                return true;
            }
        }
        return false;
    }

    @Test
    void getRecommendedUsers_lastThreeBest() {
        User user = Mockito.mock(User.class);
        Mockito.when(user.getId()).thenReturn(0L);
        User user1 = Mockito.mock(User.class);
        Mockito.when(user1.getSuggestionMetric(user,userService)).thenReturn(1.0F);
        Mockito.when(user.getId()).thenReturn(1L);
        User user2 = Mockito.mock(User.class);
        Mockito.when(user2.getSuggestionMetric(user,userService)).thenReturn(2.0F);
        Mockito.when(user.getId()).thenReturn(2L);
        User user3 = Mockito.mock(User.class);
        Mockito.when(user3.getSuggestionMetric(user,userService)).thenReturn(8.0F);
        Mockito.when(user.getId()).thenReturn(3L);

        User user4 = Mockito.mock(User.class);
        Mockito.when(user4.getSuggestionMetric(user,userService)).thenReturn(70.0F);
        Mockito.when(user.getId()).thenReturn(1L);
        User user5 = Mockito.mock(User.class);
        Mockito.when(user5.getSuggestionMetric(user,userService)).thenReturn(50.0F);
        Mockito.when(user.getId()).thenReturn(2L);
        User user6 = Mockito.mock(User.class);
        Mockito.when(user6.getSuggestionMetric(user,userService)).thenReturn(20.0F);
        Mockito.when(user.getId()).thenReturn(3L);

        ArrayList<User> users = new ArrayList<>();
        users.add(user);
        users.add(user1);
        users.add(user2);
        users.add(user3);
        users.add(user4);
        users.add(user5);
        users.add(user6);
        Mockito.when(repository.findAll()).thenReturn(users);

        User[] out = userService.getRecommendedUsers(user);
        Assertions.assertEquals(3,out.length);
        Assertions.assertTrue(arrayContainsUser(user4,out),"at pos 0");
        Assertions.assertTrue(arrayContainsUser(user5,out),"at pos 1");
        Assertions.assertTrue(arrayContainsUser(user6,out),"at pos 2");
    }


    @Test
    void getRecommendedUsers_mixBest() {
        User user = Mockito.mock(User.class);
        Mockito.when(user.getId()).thenReturn(0L);
        User user1 = Mockito.mock(User.class);
        Mockito.when(user1.getSuggestionMetric(user,userService)).thenReturn(1.0F);
        Mockito.when(user.getId()).thenReturn(1L);
        User user2 = Mockito.mock(User.class);
        Mockito.when(user2.getSuggestionMetric(user,userService)).thenReturn(200.0F);
        Mockito.when(user.getId()).thenReturn(2L);
        User user3 = Mockito.mock(User.class);
        Mockito.when(user3.getSuggestionMetric(user,userService)).thenReturn(0.0F);
        Mockito.when(user.getId()).thenReturn(3L);

        User user4 = Mockito.mock(User.class);
        Mockito.when(user4.getSuggestionMetric(user,userService)).thenReturn(70.0F);
        Mockito.when(user.getId()).thenReturn(1L);
        User user5 = Mockito.mock(User.class);
        Mockito.when(user5.getSuggestionMetric(user,userService)).thenReturn(50.0F);
        Mockito.when(user.getId()).thenReturn(2L);
        User user6 = Mockito.mock(User.class);
        Mockito.when(user6.getSuggestionMetric(user,userService)).thenReturn(20.0F);
        Mockito.when(user.getId()).thenReturn(3L);

        ArrayList<User> users = new ArrayList<>();
        users.add(user);
        users.add(user1);
        users.add(user2);
        users.add(user3);
        users.add(user4);
        users.add(user5);
        users.add(user6);
        Mockito.when(repository.findAll()).thenReturn(users);

        User[] out = userService.getRecommendedUsers(user);
        Assertions.assertEquals(3,out.length);
        Assertions.assertTrue(arrayContainsUser(user2,out),"at pos 0");
        Assertions.assertTrue(arrayContainsUser(user4,out),"at pos 1");
        Assertions.assertTrue(arrayContainsUser(user5,out),"at pos 2");
    }
}
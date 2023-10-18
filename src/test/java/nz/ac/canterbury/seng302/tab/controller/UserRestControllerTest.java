package nz.ac.canterbury.seng302.tab.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.entity.json.ResponseFollowingObject;
import nz.ac.canterbury.seng302.tab.entity.json.ResponseFollowingTeamsObject;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
@WithAnonymousUser
public class UserRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    /**
     * The UserService for database logic
     */
    @MockBean
    private UserService userService;

    /**
     * The TeamService for database logic
     */
    @MockBean
    private TeamService teamService;

    /**
     * The UserRestController for handling requests
     */
    private UserRestController controller;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        controller = new UserRestController(userService, teamService);
    }

    @Test
    void testGetFollowing() throws JsonProcessingException {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn("email");
        User user = Mockito.mock(User.class);
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        List<User> followedUsers = new ArrayList<>();
        User user1 = Mockito.mock(User.class);
        User user2 = Mockito.mock(User.class);
        followedUsers.add(user1);
        followedUsers.add(user2);
        Mockito.when(userService.findFollowedUsersByUserId(user.getId())).thenReturn(followedUsers);

        List<ResponseFollowingObject> response = followedUsers.stream().map(followedUser -> new ResponseFollowingObject(followedUser.getId(), followedUser.getFirstName(), followedUser.getLastName(), followedUser.getEmail(), userService.areFriends(user, user),followedUser.getProfilePicName())).toList();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(response);

        ResponseEntity<String> responseEntity = controller.getFollowing();

        Assertions.assertEquals(jsonString, responseEntity.getBody());
    }


    @Test
    void testGetTeamsFollowing() throws JsonProcessingException {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn("email");
        User user = Mockito.mock(User.class);
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);

        List<Team> followedTeams = new ArrayList<>();
        Team team1 = Mockito.mock(Team.class);
        Team team2 = Mockito.mock(Team.class);
        followedTeams.add(team1);
        followedTeams.add(team2);
        Mockito.when(teamService.getFollowedTeams(user)).thenReturn(followedTeams);

        List<ResponseFollowingTeamsObject> response = followedTeams.stream().map(team -> new ResponseFollowingTeamsObject(team.getId(), team.getName(), team.getProfilePicName())).toList();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(response);

        ResponseEntity<String> responseEntity = controller.getFollowingTeams();

        Assertions.assertEquals(jsonString, responseEntity.getBody());
    }

    @Test
    void testGetFollowingNotLoggedIn() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(null);

        ResponseEntity<String> responseEntity = controller.getFollowing();

        Assertions.assertEquals("No user logged in", responseEntity.getBody());
    }

    @Test
    void testGetFollowingError() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn("email");
        User user = Mockito.mock(User.class);
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(userService.findFollowedUsersByUserId(user.getId())).thenThrow(new RuntimeException());

        ResponseEntity<String> responseEntity = controller.getFollowing();

        Assertions.assertEquals("Error getting following: null", responseEntity.getBody());
    }

    @Test
    void testUnfollow() throws JsonProcessingException {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn("email");

        User user = Mockito.mock(User.class);
        User other = Mockito.mock(User.class);
        List<User> ret = new ArrayList<User>();
        ret.add(other);

        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(userService.getUsersByFirstNameContains("Morgan")).thenReturn(ret);
        Mockito.when(userService.getUsersByLastNameContains("English")).thenReturn(ret);
        Mockito.when(user.isFollowing(other)).thenReturn(true);

        String body = "{\"fName\":\"Morgan\",\"lName\":\"English\"}";

        ResponseEntity<String> responseEntity = controller.unfollow(body);

        Assertions.assertEquals("OK", responseEntity.getBody());

    }

    @Test
    void testUnfollowusernotExist() throws JsonProcessingException {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn("email");

        User user = Mockito.mock(User.class);
        List<User> ret = new ArrayList<User>();

        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(userService.getUsersByFirstNameContains("Morgan")).thenReturn(ret);
        Mockito.when(userService.getUsersByLastNameContains("English")).thenReturn(ret);

        String body = "{\"fName\":\"Morgan\",\"lName\":\"English\"}";

        ResponseEntity<String> responseEntity = controller.unfollow(body);

        Assertions.assertEquals("Error getting following: EMPTY USER", responseEntity.getBody());

    }

    @Test
    void testUnfollowusernotFollowed() throws JsonProcessingException {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn("email");

        User user = Mockito.mock(User.class);
        User other = Mockito.mock(User.class);
        List<User> ret = new ArrayList<User>();
        ret.add(other);

        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(userService.getUsersByFirstNameContains("Morgan")).thenReturn(ret);
        Mockito.when(userService.getUsersByLastNameContains("English")).thenReturn(ret);
        Mockito.when(user.isFollowing(other)).thenReturn(false);

        String body = "{\"fName\":\"Morgan\",\"lName\":\"English\"}";

        ResponseEntity<String> responseEntity = controller.unfollow(body);

        Assertions.assertEquals("Error getting following: USER NOT FOLLOWED", responseEntity.getBody());

    }
}

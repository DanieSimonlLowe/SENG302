package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.service.*;
import nz.ac.canterbury.seng302.tab.service.email.EmailService;
import nz.ac.canterbury.seng302.tab.service.email.PasswordEmailHandler;
import nz.ac.canterbury.seng302.tab.service.email.RegistrationEmailHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@WithAnonymousUser
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    private Model model;

    /**
     * The UserService for database logic
     */
    @MockBean
    private UserService userService;

    /**
     * The LocationService for database logic
     */
    @MockBean
    private LocationService locationService;

    /**
     * The SportService for database logic
     */
    @MockBean
    private SportService sportService;

    /**
     * The handler for the registration emails
     */
    @MockBean
    private RegistrationEmailHandler emailHandler;

    /**
     * The handler for the reset password emails
     */
    @MockBean
    private PasswordEmailHandler passwordEmailHandler;

    /**
     * The service for registration tokens
     */
    @MockBean
    private TokenService registrationTokenService;

    /**
     * The service for emails
     */
    @MockBean
    private EmailService emailService;


    /**
     * The TableController for common table methods
     */
    @MockBean
    private TableController tableController;

    /**
     * The service for team members
     */
    @MockBean
    private TeamMemberService teamMemberService;

    /**
     * The service for teams
     */
    @MockBean
    private TeamService teamService;

    @MockBean
    private FeedPostService feedPostService;

    private UserController controller;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        this.model = mock(Model.class);
        controller = new UserController(userService, sportService, emailHandler, passwordEmailHandler, registrationTokenService, emailService, tableController, locationService, teamMemberService, teamService, feedPostService);
    }

    @Test
    void whenInvalidRegisterName_thenError() throws Exception {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("firstName", "456789");
        requestParams.add("lastName", "#%#%^#");
        requestParams.add("email", "j.newportemail.com");
        requestParams.add("password", "P");
        requestParams.add("confirmPassword", "P");
        requestParams.add("DOB", "2020-12-12");
        requestParams.add("city", "Chr!!");
        requestParams.add("country", "New P0st");

        mockMvc.perform(post("/register")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .params(requestParams))
                .andExpect(view().name("registration"));
    }

    @Test
    void whenValidRegisterName_thenPass() throws Exception {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("firstName", "TestName");
        requestParams.add("lastName", "LastName");
        requestParams.add("email", "j.newport@email.com");
        requestParams.add("DOB", "2015-12-31");
        requestParams.add("password", "Password1!");
        requestParams.add("confirmPassword", "Password1!");
        requestParams.add("city", "Christchurch");
        requestParams.add("country", "New Zealand");


        mockMvc.perform(post("/register")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .params(requestParams))
                .andExpect(status().isOk());
    }

    @Test
    void whenInvalidRegisterName_redirectPage() {
        RedirectAttributes ra = mock(RedirectAttributes.class);
        Model model = mock(Model.class);

        String result = controller.createUser(ra, "4567890", "#%#%^#", "j.newport@email.com", "passWord!1", "passWord!1", "2003,03,18", "19 Ricc Rd", "Chch NZ", "Riccarton", "1041", "Chch", "NZ", model);
        assertEquals("registration", result);
    }

    @Test
    void whenInvalidRegisterPassword_thenError() throws Exception {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("firstName", "James");
        requestParams.add("lastName", "Newport");
        requestParams.add("email", "j.newport@email.com");
        requestParams.add("password", "Passw!");
        requestParams.add("confirmPassword", "Passwor");
        requestParams.add("DOB", "02/02/2005");
        requestParams.add("location", "");
        requestParams.add("location1", "19 Riccarton Road");
        requestParams.add("location2", "Christchurch, NZ");
        requestParams.add("suburb", "");

        mockMvc.perform(post("/register")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .content(model.toString())
                        .params(requestParams))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void whenInvalidRegisterPassword_redirectPage() {
        RedirectAttributes ra = mock(RedirectAttributes.class);
        Model model = mock(Model.class);

        String result = controller.createUser(ra, "John", "Doe", "j.newport@email.com", "passW1", "pass!", "2003,03,18", "19 Ricc Rd", "Chch NZ", "Riccarton", "1041", "Chch", "NZ", model);
        assertEquals("registration", result);
    }

    @Test
    void whenInvalidRegisterEmail_thenError() throws Exception {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("firstName", "John");
        requestParams.add("lastName", "Doe");
        requestParams.add("email", "bad_email");
        requestParams.add("password", "passWord1!");
        requestParams.add("confirmPassword", "passWord1!");
        requestParams.add("DOB", "02/02/2003");
        requestParams.add("location", "");
        requestParams.add("location1", "19 Riccarton Road");
        requestParams.add("location2", "Christchurch, NZ");
        requestParams.add("suburb", "");

        mockMvc.perform(post("/register")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .params(requestParams))
                .andExpect(status().is4xxClientError());

    }

    @Test
    void whenInvalidRegisterEmail_redirectPage() {
        RedirectAttributes ra = mock(RedirectAttributes.class);
        Model model = mock(Model.class);

        String result = controller.createUser(ra, "John", "Doe", "this is an invalid email", "passWord!1", "passWord!1", "2003,03,18", "19 Ricc Rd", "Chch NZ", "Riccarton", "1041", "Chch", "NZ", model);
        assertEquals("registration", result);
    }

    @Test
    void whenInvalidRegisterBirthdate_thenError() throws Exception {
        LocalDate today = LocalDate.now();
        String date = UserController.getDateDisplay(today);

        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("firstName", "John");
        requestParams.add("lastName", "Doe");
        requestParams.add("email", "bad_email");
        requestParams.add("password", "passWord1!");
        requestParams.add("confirmPassword", "passWord1!");
        requestParams.add("DOB", date);
        requestParams.add("location", "");
        requestParams.add("location1", "19 Riccarton Road");
        requestParams.add("location2", "Christchurch, NZ");
        requestParams.add("suburb", "");

        mockMvc.perform(post("/register")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .params(requestParams))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void whenInvalidRegisterBirthdate_redirectPage() {
        RedirectAttributes ra = mock(RedirectAttributes.class);
        Model model = mock(Model.class);

        LocalDate today = LocalDate.now();

        String result = controller.createUser(ra, "John", "Doe", "j.newport@email.com", "passWord!1", "passWord!1", String.valueOf(today), "19 Ricc Rd", "Chch NZ", "Riccarton", "1041", "Chch", "NZ", model);
        assertEquals("registration", result);
    }

    @Test
    void whenInvalidRegisterLocation_thenError() throws Exception {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("firstName", "James");
        requestParams.add("lastName", "Newport");
        requestParams.add("email", "j.newport@email.com");
        requestParams.add("password", "Password1!");
        requestParams.add("confirmPassword", "Password1!");
        requestParams.add("DOB", "02/02/2005");
        requestParams.add("location", "");
        requestParams.add("location1", "");
        requestParams.add("location2", "Christchurch, NZ");
        requestParams.add("suburb", "");

        mockMvc.perform(post("/register")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .params(requestParams))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void whenInvalidRegisterLocation_redirectPage() {
        RedirectAttributes ra = mock(RedirectAttributes.class);
        Model model = mock(Model.class);

        String result = controller.createUser(ra, "John", "Doe", "JohnDoe@email.com", "passWord!1", "passWord!1", "2002-03-18", "", "", "", "", "", "", model);
        assertEquals("registration", result);
    }

    @Test
    void whenValidRegister_thenRedirect() {
        RedirectAttributes ra = mock(RedirectAttributes.class);
        Model model = mock(Model.class);

        String result = controller.createUser(ra, "John", "Doe", "yeyect@gmail.com", "passWord!1", "passWord!1", "2002-03-18", "19 Ricc Rd", "25 Chch", "Riccarton", "1041", "Chch", "NZ", model);
        assertEquals("redirect:/login", result);
    }

    @Test
    void whenValidLogin_thenRedirectsToProfilePage() throws Exception {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("email", "morgan.english1@hotmail.com");
        requestParams.add("password", "$2a$04$fvrk2c2qoLNtMbGFGsBLBO/BAUvCQxmmHeJyaesK5szZ6oqU0Q6Jq");
        mockMvc.perform(post("/login")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .params(requestParams))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void whenInvalidOtherProfile_thenError() throws Exception {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();

        mockMvc.perform(post("/otherProfile")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .params(requestParams))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void whenInvalidOtherProfileEmail_thenError() throws Exception {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("email", "morgan.jjjjjjjjjj@hotmail.com");

        mockMvc.perform(post("/otherProfile")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .params(requestParams))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void whenInvalidConfirmUser_thenError() throws Exception {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();

        mockMvc.perform(post("/confirmUser")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .params(requestParams))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void whenInvalidUpdateName_thenError() throws Exception {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("fName", "456789");
        requestParams.add("lName", "#%#%^#");

        mockMvc.perform(post("/edit")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .params(requestParams))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void whenInvalidUpdateName_redirectPage() {
        Model model = mock(Model.class);
        Location location = new Location("", "", "", "", "a", "b");

        emailHandler.setUpRegistration("yeyect@gmail.com", "John", "Doe", LocalDate.of(2003, 03, 18), location, "pas");
        String result = controller.editProfile("yeyect@gmail.com", "89083", "Doe", "2002-03-18", emptyList(), true, "", "", "", "", "", "Chirstchurch", "NZ", model);

        assertEquals("editor", result);
    }

    @Test
    void whenInvalidUpdateEmail_thenError() throws Exception {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("email", "not a real email");

        mockMvc.perform(post("/edit")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .params(requestParams))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void whenInvalidUpdateEmail_redirectPage() {
        Model model = mock(Model.class);
        Location location = new Location("", "", "", "", "a", "b");

        emailHandler.setUpRegistration("yeyect@gmail.com", "John", "Doe", LocalDate.of(2003, 03, 18), location, "pas");
        String result = controller.editProfile("invalid email", "John", "Doe", "2002-03-18", emptyList(), true, "", "", "", "", "", "Chirstchurch", "NZ", model);

        assertEquals("editor", result);
    }

    @Test
    void whenInvalidUpdateDOB_thenError() throws Exception {
        LocalDate today = LocalDate.now();
        String date = UserController.getDateDisplay(today);

        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("DOB", date);

        mockMvc.perform(post("/edit")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .params(requestParams))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void whenInvalidUpdateDOB_redirectPage() {
        Model model = mock(Model.class);
        Location location = new Location("", "", "", "", "a", "b");

        emailHandler.setUpRegistration("yeyect@gmail.com", "John", "Doe", LocalDate.of(2003, 03, 18), location, "pas");
        String result = controller.editProfile("yeyect@gmail.com", "John", "Doe", String.valueOf(LocalDate.now()), emptyList(), true, "", "", "", "", "", "Chirstchurch", "NZ", model);

        assertEquals("editor", result);
    }

    @Test
    void whenInvalidUpdateLocation_thenError() throws Exception {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("location", "");
        requestParams.add("address1", "");
        requestParams.add("address2", "Christchurch, NZ");
        requestParams.add("suburb", "");

        mockMvc.perform(post("/edit")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .params(requestParams))
                .andExpect(status().is4xxClientError());
    }


    @Test
    void whenInvalidUpdateLocation_redirectPage() {
        Model model = mock(Model.class);
        Location location = new Location("", "", "", "", "a", "b");

        emailHandler.setUpRegistration("yeyect@gmail.com", "John", "Doe", LocalDate.of(2003, 03, 18), location, "pas");
        String result = controller.editProfile("invalid email", "John", "Doe", "2002-03-18", emptyList(), true, "", "", "", "4322", "", "21432", "N@#%#", model);

        assertEquals("editor", result);
    }

    @Test
    void getRegister_test() {
        Model model = mock(Model.class);

        String result = controller.register();
        Assertions.assertEquals("registration", result);
    }


    @Test
    void getLogin_hasError_test() {
        Model model = mock(Model.class);
        BadCredentialsException badCredentialsException = Mockito.mock(BadCredentialsException.class);

        String result = controller.getTemplate(badCredentialsException, model);

        Mockito.verify(model, Mockito.times(1)).addAttribute("errorMessage", "Email address is unknown or the password is invalid");
        Mockito.verify(model, Mockito.times(1)).addAttribute("isError", true);

        Assertions.assertEquals("login", result);
    }

    @Test
    void getLogin_noError_test() {

        String result = controller.getTemplate(null, model);

        Mockito.verify(model, Mockito.never()).addAttribute(Mockito.any(), Mockito.any());

        Assertions.assertEquals("login", result);
    }

    @Test
    void logout_test() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);


        String result = controller.logout(request, response);

        Assertions.assertEquals("redirect:/login", result);
    }

    @Test
    void profileView_noTeams_noSports_test() {
        Model model = Mockito.mock(Model.class);


        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn("email");
        User user = Mockito.mock(User.class);
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        User[] follows = new ArrayList<User>().toArray(new User[0]);
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);

        Mockito.when(user.getEmail()).thenReturn("e");
        Mockito.when(user.getId()).thenReturn(70L);
        Mockito.when(user.getFirstName()).thenReturn("fn");
        Mockito.when(user.getLastName()).thenReturn("ln");
        Mockito.when(user.getDate()).thenReturn(LocalDate.EPOCH);
        Mockito.when(user.getLocationString()).thenReturn("l");
        Mockito.when(user.getProfilePicName()).thenReturn("p");

        Mockito.when(userService.getRecommendedUsers(user)).thenReturn(follows);
        Mockito.when(userService.findFollowedUsersByUserId(user.getId())).thenReturn(List.of(follows));

        Mockito.when(teamMemberService.getAllTeamsFromUser(user)).thenReturn(new ArrayList<>());
        Mockito.when(user.getSports()).thenReturn(new ArrayList<>());

        Assertions.assertEquals("profile", controller.profileView(model));
        Mockito.verify(model, Mockito.times(1)).addAttribute("email", "e");
        Mockito.verify(model, Mockito.times(1)).addAttribute("fName", "fn");
        Mockito.verify(model, Mockito.times(1)).addAttribute("lName", "ln");
        Mockito.verify(model, Mockito.times(1)).addAttribute("dateOfBirth", "1 / 1 / 1970");
        Mockito.verify(model, Mockito.times(1)).addAttribute("location", "l");
        Mockito.verify(model, Mockito.times(1)).addAttribute("image", "p");

        Mockito.verify(model, Mockito.times(1)).addAttribute("noTeams", true);
        Mockito.verify(model, Mockito.never()).addAttribute("noTeams", false);
        Mockito.verify(model, Mockito.never()).addAttribute(Mockito.eq("teamRoles"), Mockito.any());
        Mockito.verify(model, Mockito.never()).addAttribute(Mockito.eq("teamNames"), Mockito.any());

        Mockito.verify(model, Mockito.times(1)).addAttribute("noSports", true);
        Mockito.verify(model, Mockito.never()).addAttribute("noSports", false);
        Mockito.verify(model, Mockito.never()).addAttribute(Mockito.eq("favSports"), Mockito.any());
    }


    @Test
    void follow_noUser_hasOther_test() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Mockito.when(authentication.getPrincipal()).thenReturn("email");
        Mockito.when(userService.getUserByEmail("email")).thenReturn(null);

        User other = Mockito.mock(User.class);
        Mockito.when(userService.getUser(10L)).thenReturn(Optional.of(other));

        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);

        Assertions.assertEquals("redirect:/", controller.follow(10L, httpServletRequest));

        Mockito.verify(httpServletRequest, Mockito.never()).getHeader(Mockito.any());
    }

    @Test
    void follow_hasUser_noOther_test() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Mockito.when(authentication.getPrincipal()).thenReturn("email");
        User user = Mockito.mock(User.class);
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);

        User other = Mockito.mock(User.class);
        Mockito.when(userService.getUser(10L)).thenReturn(Optional.empty());

        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);

        Assertions.assertEquals("redirect:/", controller.follow(10L, httpServletRequest));

        Mockito.verify(httpServletRequest, Mockito.never()).getHeader(Mockito.any());
    }

    @Test
    void follow_noUser_noOther_test() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Mockito.when(authentication.getPrincipal()).thenReturn("email");
        Mockito.when(userService.getUserByEmail("email")).thenReturn(null);

        Mockito.when(userService.getUser(10L)).thenReturn(Optional.empty());

        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);

        Assertions.assertEquals("redirect:/", controller.follow(10L, httpServletRequest));

        Mockito.verify(httpServletRequest, Mockito.never()).getHeader(Mockito.any());
    }

    @Test
    void follow_hasUser_hasOther_gotHeader_test() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Mockito.when(authentication.getPrincipal()).thenReturn("email");
        User user = Mockito.mock(User.class);
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);

        User other = Mockito.mock(User.class);
        Mockito.when(userService.getUser(10L)).thenReturn(Optional.of(other));

        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(httpServletRequest.getHeader("Referer")).thenReturn("TEST");

        Assertions.assertEquals("redirect:TEST", controller.follow(10L, httpServletRequest));

        Mockito.verify(httpServletRequest, Mockito.times(1)).getHeader("Referer");
    }

    @Test
    void unfollow_test_success() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Mockito.when(authentication.getPrincipal()).thenReturn("email");
        User user = Mockito.mock(User.class);
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);

        User other = Mockito.mock(User.class);
        Mockito.when(userService.getUser(17L)).thenReturn(Optional.of(other));

        Mockito.when(user.isFollowing(other)).thenReturn(true);

        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(httpServletRequest.getHeader("Referer")).thenReturn("TEST");

        Assertions.assertEquals("redirect:TEST", controller.unfollow(17L, httpServletRequest));
        Mockito.verify(httpServletRequest, Mockito.times(1)).getHeader("Referer");

    }

    @Test
    void unfollow_test_notfollowing() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Mockito.when(authentication.getPrincipal()).thenReturn("email");
        User user = Mockito.mock(User.class);
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);

        User other = Mockito.mock(User.class);
        Mockito.when(userService.getUser(17L)).thenReturn(Optional.of(other));

        Mockito.when(user.isFollowing(other)).thenReturn(false);

        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);

        Assertions.assertEquals("redirect:/", controller.unfollow(17L, httpServletRequest));
    }

    @Test
    void unfollow_noUser() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Mockito.when(authentication.getPrincipal()).thenReturn("email");
        Mockito.when(userService.getUserByEmail("email")).thenReturn(null);

        User other = Mockito.mock(User.class);
        Mockito.when(userService.getUser(17L)).thenReturn(Optional.of(other));


        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);

        Assertions.assertEquals("redirect:/", controller.unfollow(17L, httpServletRequest));
    }


    @Test
    void unfollow_test_noOther() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Mockito.when(authentication.getPrincipal()).thenReturn("email");
        User user = Mockito.mock(User.class);
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);

        User other = Mockito.mock(User.class);
        Mockito.when(userService.getUser(17L)).thenReturn(Optional.empty());


        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);

        Assertions.assertEquals("redirect:/", controller.unfollow(17L, httpServletRequest));
    }

    @Test
    void follow_hasUser_hasOther_noHeader_test() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Mockito.when(authentication.getPrincipal()).thenReturn("email");
        User user = Mockito.mock(User.class);
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);

        User other = Mockito.mock(User.class);
        Mockito.when(userService.getUser(10L)).thenReturn(Optional.of(other));

        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(httpServletRequest.getHeader("Referer")).thenReturn(null);

        Assertions.assertEquals("redirect:/", controller.follow(10L, httpServletRequest));

        Mockito.verify(httpServletRequest, Mockito.times(1)).getHeader("Referer");
    }

    @Test
    void getOtherProfile_loggedOut_test() {
        Model model = Mockito.mock(Model.class);
        User user = Mockito.mock(User.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Mockito.when(authentication.getPrincipal()).thenReturn("loggedIn");

        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(userService.getUserByEmail("loggedIn")).thenReturn(null);

        Mockito.when(user.getEmail()).thenReturn("NE");
        Mockito.when(user.getFirstName()).thenReturn("firstName");
        Mockito.when(user.getLastName()).thenReturn("lastName");
        LocalDate localDate = LocalDate.now();
        Mockito.when(user.getDate()).thenReturn(localDate);
        Mockito.when(user.getLocationString()).thenReturn("locString");
        Mockito.when(user.getProfilePicName()).thenReturn("image");
        Mockito.when(user.getProfilePicName()).thenReturn("prevPage");

        Assertions.assertEquals("otherProfile", controller.getOtherProfile("email", 1, "search", model));

        Mockito.verify(model, Mockito.never()).addAttribute(Mockito.eq("isLoggedIn"), Mockito.any());
        Mockito.verify(model, Mockito.never()).addAttribute(Mockito.eq("canFollow"), Mockito.any());
        Mockito.verify(model, Mockito.never()).addAttribute(Mockito.eq("userId"), Mockito.any());


        Mockito.verify(model, Mockito.times(1)).addAttribute("backToSearch", "<a class=\"light-button-md\" href=\"/allProfiles?search=&page=0\">< Back to user list</a>");
        Mockito.verify(model, Mockito.times(1)).addAttribute("email", "NE");
        Mockito.verify(model, Mockito.times(1)).addAttribute("fName", "firstName");
        Mockito.verify(model, Mockito.times(1)).addAttribute("lName", "lastName");
        Mockito.verify(model, Mockito.times(1)).addAttribute("dateOfBirth", localDate);
        Mockito.verify(model, Mockito.times(1)).addAttribute("prevPage", 1);
        Mockito.verify(model, Mockito.times(1)).addAttribute("search", "search");
    }

    @Test
    void getOtherProfile_loggedIn_test() {
        Model model = Mockito.mock(Model.class);
        User user = Mockito.mock(User.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Mockito.when(authentication.getPrincipal()).thenReturn("loggedIn");

        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        User loggedIn = Mockito.mock(User.class);
        Mockito.when(userService.getUserByEmail("loggedIn")).thenReturn(loggedIn);
        Mockito.when(loggedIn.isFollowing(user)).thenReturn(true);

        Mockito.when(user.getEmail()).thenReturn("NE");
        Mockito.when(user.getFirstName()).thenReturn("firstName");
        Mockito.when(user.getLastName()).thenReturn("lastName");
        LocalDate localDate = LocalDate.now();
        Mockito.when(user.getDate()).thenReturn(localDate);
        Mockito.when(user.getLocationString()).thenReturn("locString");
        Mockito.when(user.getProfilePicName()).thenReturn("image");
        Mockito.when(user.getProfilePicName()).thenReturn("prevPage");
        Mockito.when(user.getId()).thenReturn(1L);

        Assertions.assertEquals("otherProfile", controller.getOtherProfile("email", 1, "search", model));

        Mockito.verify(model, Mockito.times(1)).addAttribute("isLoggedIn", true);
        Mockito.verify(model, Mockito.times(1)).addAttribute("canFollow", false);
        Mockito.verify(model, Mockito.times(1)).addAttribute("userId", 1L);


        Mockito.verify(model, Mockito.times(1)).addAttribute("backToSearch", "<a class=\"light-button-md\" href=\"/allProfiles?search=&page=0\">< Back to user list</a>");
        Mockito.verify(model, Mockito.times(1)).addAttribute("email", "NE");
        Mockito.verify(model, Mockito.times(1)).addAttribute("fName", "firstName");
        Mockito.verify(model, Mockito.times(1)).addAttribute("lName", "lastName");
        Mockito.verify(model, Mockito.times(1)).addAttribute("dateOfBirth", localDate);
        Mockito.verify(model, Mockito.times(1)).addAttribute("prevPage", 1);
        Mockito.verify(model, Mockito.times(1)).addAttribute("search", "search");
    }
    @Test
    void profileView_withTeams_withSports_test() {
        Model model = Mockito.mock(Model.class);

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn("email");
        User user = Mockito.mock(User.class);
        User[] follows = new ArrayList<User>().toArray(new User[0]);
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);


        Mockito.when(user.getEmail()).thenReturn("e");
        Mockito.when(user.getId()).thenReturn(70L);
        Mockito.when(user.getFirstName()).thenReturn("fn");
        Mockito.when(user.getLastName()).thenReturn("ln");
        Mockito.when(user.getDate()).thenReturn(LocalDate.EPOCH);
        Mockito.when(user.getLocationString()).thenReturn("l");
        Mockito.when(user.getProfilePicName()).thenReturn("p");

        Mockito.when(userService.getRecommendedUsers(user)).thenReturn(follows);
        Mockito.when(userService.findFollowedUsersByUserId(user.getId())).thenReturn(List.of(follows));

        TeamMember teamMember = Mockito.mock(TeamMember.class);
        Mockito.when(teamMember.getRole()).thenReturn(Role.MEMBER);
        Mockito.when(teamMember.getTeamMemberId()).thenReturn(Mockito.mock(TeamMemberId.class));
        Mockito.when(teamMember.getTeamMemberId().getTeam()).thenReturn(Mockito.mock(Team.class));
        Mockito.when(teamMember.getTeamMemberId().getTeam().getName()).thenReturn("team");

        List<TeamMember> teamMembers = new ArrayList<>();
        teamMembers.add(teamMember);

        Sport sport = Mockito.mock(Sport.class);
        Mockito.when(sport.getSportName()).thenReturn("sport");

        List<Sport> sports = new ArrayList<>();
        sports.add(sport);

        Mockito.when(teamMemberService.getAllTeamsFromUser(user)).thenReturn(teamMembers);
        Mockito.when(user.getSports()).thenReturn(sports);

        Assertions.assertEquals("profile",controller.profileView(model));
        Mockito.verify(model,Mockito.times(1)).addAttribute("email","e");
        Mockito.verify(model,Mockito.times(1)).addAttribute("fName","fn");
        Mockito.verify(model,Mockito.times(1)).addAttribute("lName","ln");
        Mockito.verify(model,Mockito.times(1)).addAttribute("dateOfBirth","1 / 1 / 1970");
        Mockito.verify(model,Mockito.times(1)).addAttribute("location","l");
        Mockito.verify(model,Mockito.times(1)).addAttribute("image","p");

        Mockito.verify(model,Mockito.times(1)).addAttribute("noTeams",false);
        Mockito.verify(model,Mockito.never()).addAttribute("noTeams",true);

        Mockito.verify(model,Mockito.times(1)).addAttribute("noSports",false);
        Mockito.verify(model,Mockito.never()).addAttribute("noSports",true);
        Mockito.verify(model,Mockito.times(1)).addAttribute("favSportsString", "sport");
    }

    private static Stream<Arguments> allPrivacyTypes() {
        return Stream.of(
                Arguments.of(PrivacyType.FREINDS_ONLY),
                Arguments.of(PrivacyType.PUBLIC),
                Arguments.of(PrivacyType.PRIVATE)
        );
    }

    @ParameterizedTest
    @MethodSource("allPrivacyTypes")
    void setUserPrivacy_hasUser(PrivacyType privacyType) {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn("user");

        User user = Mockito.mock(User.class);
        Mockito.when(userService.getUserByEmail("user")).thenReturn(user);

        Assertions.assertEquals("redirect:/profile",controller.setUserPrivacy(privacyType));

        Mockito.verify(user,Mockito.times(1)).setPrivacyType(privacyType);
        Mockito.verify(userService,Mockito.times(1)).save(user);
    }

    @ParameterizedTest
    @MethodSource("allPrivacyTypes")
    void setUserPrivacy_noUser(PrivacyType privacyType) {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn("user");

        User user = Mockito.mock(User.class);
        Mockito.when(userService.getUserByEmail("user")).thenReturn(null);

        Assertions.assertEquals("redirect:/",controller.setUserPrivacy(privacyType));

        Mockito.verify(user,Mockito.never()).setPrivacyType(privacyType);
        Mockito.verify(userService,Mockito.never()).save(user);
    }
}

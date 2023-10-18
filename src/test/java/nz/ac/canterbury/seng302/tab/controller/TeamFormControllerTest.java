package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithAnonymousUser
public class TeamFormControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;


    @Autowired
    private TeamFormController teamFormController;


    @MockBean
    private TeamService teamService;

    @MockBean
    private FeedPostService postService;

    @MockBean
    private UserService userService;


    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        User user = Mockito.mock(User.class);
        Mockito.when(userService.getUserByEmail(Mockito.any())).thenReturn(user);
        teamService = Mockito.mock(TeamService.class);
        postService = Mockito.mock(FeedPostService.class);
        userService = Mockito.mock(UserService.class);
    }

    @Test
    public void givenWac_whenServletContext_thenItProvidesTeamFormController() {
        ServletContext servletContext = webApplicationContext.getServletContext();
        assertNotNull(servletContext);
        assertTrue(servletContext instanceof MockServletContext);
        assertNotNull(webApplicationContext.getBean("teamFormController"));
    }
    @Test
    void whenValidInput_thenRedirectsToTeamPage() throws Exception {
        User user = Mockito.mock(User.class);
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("name", "TrailBlazers");
        requestParams.add("location", "Portland, United States of America");
        requestParams.add("city", "Portland");
        requestParams.add("country", "United States of America");
        requestParams.add("sport", "Basketball");

        mockMvc.perform(post("/form")
                .contentType(APPLICATION_FORM_URLENCODED)
                .params(requestParams))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void creatingTeam_noCity_thenBadRequest() throws Exception {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("name", "TrailBlazers");
        requestParams.add("location", "8 Big Lane, 80345, United States of America");
        requestParams.add("address1", "8 Big Lane");
        requestParams.add("postcode", "80345");
        requestParams.add("country", "United States of America");
        requestParams.add("sport", "Basketball");

        mockMvc.perform(post("/form")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .params(requestParams))
                .andExpect(status().isBadRequest());
    }

    @Test
    void creatingTeam_noCountry_thenBadRequest() throws Exception {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("name", "TrailBlazers");
        requestParams.add("location", "8 Big Lane, 80345, United States of America");
        requestParams.add("address1", "8 Big Lane");
        requestParams.add("postcode", "80345");
        requestParams.add("city", "Portland");
        requestParams.add("sport", "Basketball");

        mockMvc.perform(post("/form")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .params(requestParams))
                .andExpect(status().isBadRequest());
    }

    @Test
    void creatingTeam_invalidCity_thenStayOnFormPage() throws Exception {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("name", "TrailBlazers");
        requestParams.add("location", "8 Big Lane, 80345, Portland, United States of America");
        requestParams.add("address1", "8 Big Lane");
        requestParams.add("postcode", "80345");
        requestParams.add("city", "800Portland!!");
        requestParams.add("country", "United States of America");
        requestParams.add("sport", "Basketball");

        mockMvc.perform(post("/form")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .params(requestParams))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("teamFormTemplate"));
    }

    @Test
    void creatingTeam_invalidCountry_thenStayOnFormPage() throws Exception {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("name", "TrailBlazers");
        requestParams.add("location", "8 Big Lane, 80345, United States of America");
        requestParams.add("address1", "8 Big Lane");
        requestParams.add("postcode", "80345");
        requestParams.add("city", "Portland");
        requestParams.add("country", "United States of 1000");
        requestParams.add("sport", "Basketball");

        mockMvc.perform(post("/form")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .params(requestParams))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("teamFormTemplate"));
    }

    @Test
    void whenValidInputTeam_thenRedirectsToTeamPage() throws Exception {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("name", "Trail Blazers");
        requestParams.add("location", "Portland, US");
        requestParams.add("city", "Portland");
        requestParams.add("country", "United States of America");
        requestParams.add("sport", "Basketball");

        mockMvc.perform(post("/form")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .params(requestParams))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void whenValidInputSport_thenRedirectsToTeamPage() throws Exception {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("name", "TrailBlazers");
        requestParams.add("location", "Portland, US");
        requestParams.add("city", "Portland");
        requestParams.add("country", "United States of America");
        requestParams.add("sport", "Super Rugby");

        mockMvc.perform(post("/form")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .params(requestParams))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void whenInvalidInputName_testingServerSide() throws Exception {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("name", " ");
        requestParams.add("location", "Portland, US");
        requestParams.add("city", "Portland");
        requestParams.add("country", "United States of America");
        requestParams.add("sport", "Super Rugby");

        mockMvc.perform(post("/form")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .params(requestParams))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenInvalidInputLocation_testingServerSide() throws Exception {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("name", "team");
        requestParams.add("location", " ");
        requestParams.add("city", "800");
        requestParams.add("country", "H3he");
        requestParams.add("suburb", "");
        requestParams.add("sport", "Super Rugby");

        mockMvc.perform(post("/form")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .params(requestParams))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenInvalidInputSport_testingServerSide() throws Exception {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("name", "team");
        requestParams.add("location", "Portland, US");
        requestParams.add("city", "Portland");
        requestParams.add("country", "United States of America");
        requestParams.add("sport", " ");

        mockMvc.perform(post("/form")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .params(requestParams))
                .andExpect(status().isBadRequest());

    }

    @Test
    void whenInvalidInput_teamName_testingServerSide() throws Exception {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("name", "{}");
        requestParams.add("location", "Portland, US");
        requestParams.add("city", "Portland");
        requestParams.add("country", "United States of America");
        requestParams.add("sport", "team");

        mockMvc.perform(post("/form")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .params(requestParams))
                .andExpect(status().isBadRequest());

    }
    @Test
    void whenInvalidInput_sportWithBrackets_testingServerSide() throws Exception {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("name", "team");
        requestParams.add("location", "Portland, US");
        requestParams.add("city", "Portland");
        requestParams.add("country", "United States of America");
        requestParams.add("sport", "{}");

        mockMvc.perform(post("/form")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .params(requestParams))
                .andExpect(status().isBadRequest());

    }
    @Test
    void whenValidInput_sportWithAllSpecialCharacters_testingServerSide() throws Exception {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("name", "TrailBlazers");
        requestParams.add("location", "Portland, US");
        requestParams.add("city", "Portland");
        requestParams.add("country", "United States of America");
        requestParams.add("sport", "sport's-name yes");

        mockMvc.perform(post("/form")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .params(requestParams))
                .andExpect(status().is3xxRedirection());

    }

    @Test
    void whenValidInput_locationWithAllSpecialCharacters_testingServerSide() throws Exception {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("name", "TrailBlazers");
        requestParams.add("location", "80 David-Finch's Lane, 8095-435, Portland-Maine's, US");
        requestParams.add("address1", "80 David-Finch's Lane");
        requestParams.add("postcode", "8095-435");
        requestParams.add("city", "Portland-Maine's");
        requestParams.add("country", "United-State's of America");
        requestParams.add("sport", "sport's-name yes ");

        mockMvc.perform(post("/form")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .params(requestParams))
                .andExpect(status().is3xxRedirection());

    }

    @Test
     void editTeam_notManager_homeRedirection() throws Exception {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("id", "1");
        requestParams.add("name", "TrailBlazers");
        requestParams.add("address1", "20 Mangorei Rd");
        requestParams.add("suburb", "Merrilands");
        requestParams.add("postcode", "4312");
        requestParams.add("city", "New Plymouth");
        requestParams.add("country", "New Zealand");
        requestParams.add("sport", "Basketball");

        MvcResult mvcResult1 = mockMvc.perform(post("/form")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .params(requestParams))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String redirectedUrl = mvcResult1.getResponse().getRedirectedUrl();

        Assertions.assertEquals("/", redirectedUrl);
    }

    @Test
    void get_editTeam_nonExistentTeam_homeRedirection() throws Exception {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("id", "1");
        requestParams.add("name", "TrailBlazers");
        requestParams.add("address1", "20 Mangorei Rd");
        requestParams.add("suburb", "Merrilands");
        requestParams.add("postcode", "4312");
        requestParams.add("city", "New Plymouth");
        requestParams.add("country", "New Zealand");
        requestParams.add("sport", "Basketball");

        MvcResult mvcResult1 = mockMvc.perform(get("/teamEdit")
                        .param("displayId", "-1"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String redirectedUrl = mvcResult1.getResponse().getRedirectedUrl();
        Assertions.assertEquals("/", redirectedUrl);
    }

    @Test
    void get_teamProfile_nonExistentTeam_homeRedirection() {
        SportService sportService = Mockito.mock(SportService.class);
        TeamService teamService = Mockito.mock(TeamService.class);
        LocationService locationService = Mockito.mock(LocationService.class);
        TableController tableController = Mockito.mock(TableController.class);
        ClubService clubService = Mockito.mock(ClubService.class);
        TeamMemberController teamMemberController = Mockito.mock(TeamMemberController.class);
        UserService userService = Mockito.mock(UserService.class);
        ActivityService activityService = Mockito.mock(ActivityService.class);
        FeedPostService feedPostService = Mockito.mock(FeedPostService.class);
        CommentService commentService = Mockito.mock(CommentService.class);
        ModerationAPIController moderationAPIController = Mockito.mock(ModerationAPIController.class);

        Model model = Mockito.mock(Model.class);
        RedirectAttributes redirectAttributes = Mockito.mock(RedirectAttributes.class);

        TeamFormController teamFormController = new TeamFormController(sportService,
                teamService,
                locationService,
                tableController,
                clubService,
                teamMemberController,
                userService,
                activityService,
                feedPostService,
                commentService,
                moderationAPIController);

        teamFormController.getTemplate(-1L, "false", "false", model, redirectAttributes);
        Mockito.verify(redirectAttributes, Mockito.times(1)).addAttribute("teamError", "Team does not exist");
    }

    @Test
    void post_uploadImage_nonExistentTeam_homeRedirection() throws Exception {
        SportService sportService = Mockito.mock(SportService.class);
        TeamService teamService = Mockito.mock(TeamService.class);
        LocationService locationService = Mockito.mock(LocationService.class);
        TableController tableController = Mockito.mock(TableController.class);
        ClubService clubService = Mockito.mock(ClubService.class);
        TeamMemberController teamMemberController = Mockito.mock(TeamMemberController.class);
        UserService userService = Mockito.mock(UserService.class);
        ActivityService activityService = Mockito.mock(ActivityService.class);
        FeedPostService feedPostService = Mockito.mock(FeedPostService.class);
        ModerationAPIController moderationAPIController = Mockito.mock(ModerationAPIController.class);
        CommentService commentService = Mockito.mock(CommentService.class);

        RedirectAttributes redirectAttributes = Mockito.mock(RedirectAttributes.class);

        TeamFormController teamFormController = new TeamFormController(sportService,
                teamService,
                locationService,
                tableController,
                clubService,
                teamMemberController,
                userService,
                activityService,
                feedPostService,
                commentService,
                moderationAPIController);

        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        teamFormController.uploadImage(-1L, multipartFile, redirectAttributes);
        Mockito.verify(redirectAttributes, Mockito.times(1)).addAttribute("teamError", "Team does not exist");
    }

    @Test
    void post_editTeam_nonExistentTeam_homeRedirection() {
        SportService sportService = Mockito.mock(SportService.class);
        TeamService teamService = Mockito.mock(TeamService.class);
        LocationService locationService = Mockito.mock(LocationService.class);
        TableController tableController = Mockito.mock(TableController.class);
        ClubService clubService = Mockito.mock(ClubService.class);
        TeamMemberController teamMemberController = Mockito.mock(TeamMemberController.class);
        UserService userService = Mockito.mock(UserService.class);
        ActivityService activityService = Mockito.mock(ActivityService.class);
        FeedPostService feedPostService = Mockito.mock(FeedPostService.class);
        CommentService commentService = Mockito.mock(CommentService.class);
        ModerationAPIController moderationAPIController = Mockito.mock(ModerationAPIController.class);

        Model model = Mockito.spy(Model.class);
        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);

        TeamFormController teamFormController = new TeamFormController(sportService,
                teamService,
                locationService,
                tableController,
                clubService,
                teamMemberController,
                userService,
                activityService,
                feedPostService,
                commentService,
                moderationAPIController);

        Mockito.when(teamMemberController.checkIsManager(Mockito.any())).thenReturn(true);

        teamFormController.submitForm("TrailBlazers", "10 Test Rd", "", "Ilam", "8042", "Christchurch", "New Zealand", "Basketball", -1L, model, response);

        Mockito.verify(response, Mockito.times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void get_generateToken_nonExistentTeam_homeRedirection() {
        SportService sportService = Mockito.mock(SportService.class);
        TeamService teamService = Mockito.mock(TeamService.class);
        LocationService locationService = Mockito.mock(LocationService.class);
        TableController tableController = Mockito.mock(TableController.class);
        ClubService clubService = Mockito.mock(ClubService.class);
        TeamMemberController teamMemberController = Mockito.mock(TeamMemberController.class);
        UserService userService = Mockito.mock(UserService.class);
        ActivityService activityService = Mockito.mock(ActivityService.class);
        FeedPostService feedPostService = Mockito.mock(FeedPostService.class);
        CommentService commentService = Mockito.mock(CommentService.class);
        ModerationAPIController moderationAPIController = Mockito.mock(ModerationAPIController.class);

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);

        TeamFormController teamFormController = new TeamFormController(sportService,
                teamService,
                locationService,
                tableController,
                clubService,
                teamMemberController,
                userService,
                activityService,
                feedPostService,
                commentService,
                moderationAPIController);

        teamFormController.regenerateToken(-1L, response);
        Mockito.verify(response, Mockito.times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }


    @Test
    void get_allTeams_asMorgan_containsClubs() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("morgan.english@hotmail.com", null));

        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();

        mockMvc.perform(get("/allTeams")
                        .params(requestParams))
                .andExpect(status().isOk())
                .andExpect(view().name("allTeams"))
                .andExpect(model().attributeExists("teamClubs"));
    }


    @Test
    void followTeam_nonExistentTeam_redirection() throws Exception {
        MvcResult mvcResult1 = mockMvc.perform(post("/followTeam")
                        .param("id", "-1"))
                .andExpect(status().is3xxRedirection())
                .andReturn();


        Optional<NoSuchElementException> someException = Optional.ofNullable((NoSuchElementException) mvcResult1.getResolvedException());
        someException.ifPresent( (se) -> Assertions.assertEquals(se.getClass(), NoSuchElementException.class));

        String redirectedUrl = mvcResult1.getResponse().getRedirectedUrl();
        Assertions.assertEquals("/", redirectedUrl);
    }

    @Test
    void followTeam_validTeam_redirect() throws Exception {
        MvcResult mvcResult1 = mockMvc.perform(post("/followTeam")
                        .param("id", "13"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String redirectedUrl = mvcResult1.getResponse().getRedirectedUrl();
        Assertions.assertEquals("/", redirectedUrl);
    }

    @Test
    void getPostForm_test() {
        Model model = Mockito.mock(Model.class);
        teamFormController.getPostForm(2L,model);

        Mockito.verify(model,Mockito.times(1)).addAttribute("submitPage","teamPost");
        Mockito.verify(model,Mockito.times(1)).addAttribute("title","Add a Team Post");
        Mockito.verify(model,Mockito.times(1)).addAttribute("id",2L);

    }

    @Test
    void createPost_notManager() {
        RedirectAttributes redirectAttributes = Mockito.mock(RedirectAttributes.class);
        Team team = Mockito.mock(Team.class);
        List<Team> teamList = new ArrayList<>();
        teamList.add(team);
        Mockito.when(teamService.getTeamNamesWhoUserManageOrCoach(2L)).thenReturn(teamList);
        Mockito.when(teamService.getTeam(2L)).thenReturn(Optional.of(team));
        Assertions.assertEquals("redirect:/teamPost",teamFormController.createPost(2L,"","",null,redirectAttributes));

        Mockito.verify(postService,Mockito.never()).save(Mockito.any());
    }

    @Test
    void createPost_throws() {
        RedirectAttributes redirectAttributes = Mockito.mock(RedirectAttributes.class);

        Team team = Mockito.mock(Team.class);
        List<Team> teamList = new ArrayList<>();
        teamList.add(team);
        Mockito.when(teamService.getTeamNamesWhoUserManageOrCoach(2L)).thenReturn(teamList);
        Mockito.when(teamService.getTeam(2L)).thenReturn(Optional.of(team));

        Assertions.assertEquals("redirect:/teamPost",teamFormController.createPost(2L,"test","test",null,redirectAttributes));

        Mockito.verify(redirectAttributes,Mockito.times(1)).addFlashAttribute("error","You must be a manager or coach of the team to add a post.");
        Mockito.verify(postService,Mockito.never()).save(Mockito.any());
    }

    @Test
    void createPost_valid() {
        RedirectAttributes redirectAttributes = Mockito.mock(RedirectAttributes.class);
        SportService sportService = Mockito.mock(SportService.class);
        TeamService teamService = Mockito.mock(TeamService.class);
        LocationService locationService = Mockito.mock(LocationService.class);
        TableController tableController = Mockito.mock(TableController.class);
        ClubService clubService = Mockito.mock(ClubService.class);
        TeamMemberController teamMemberController = Mockito.mock(TeamMemberController.class);
        UserService userService = Mockito.mock(UserService.class);
        ActivityService activityService = Mockito.mock(ActivityService.class);
        FeedPostService feedPostService = Mockito.mock(FeedPostService.class);
        CommentService commentService = Mockito.mock(CommentService.class);
        ModerationAPIController moderationAPIController = Mockito.mock(ModerationAPIController.class);

        TeamFormController teamFormController = new TeamFormController(sportService,
                teamService,
                locationService,
                tableController,
                clubService,
                teamMemberController,
                userService,
                activityService,
                feedPostService,
                commentService,
                moderationAPIController);


        User user = Mockito.mock(User.class);
        Mockito.when(userService.getUserByEmail(Mockito.any())).thenReturn(user);
        Mockito.when(user.getId()).thenReturn(2L);

        Team team = Mockito.mock(Team.class);
        ArrayList<Team> teamList = new ArrayList<>();
        teamList.add(team);

        Mockito.when(teamService.getTeamNamesWhoUserManageOrCoach(2L)).thenReturn(teamList);
        Mockito.when(team.getId()).thenReturn(2L);

        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        Mockito.when(multipartFile.getContentType()).thenReturn("application/octet-stream");

        Mockito.when(teamService.getTeam(2L)).thenReturn(Optional.of(team));


        Assertions.assertEquals("redirect:/teamProfile?id=2",teamFormController.createPost(2L,"test","test", multipartFile, redirectAttributes));
        Mockito.verify(feedPostService,Mockito.times(1)).save(Mockito.any());
    }
}

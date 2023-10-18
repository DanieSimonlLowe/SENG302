package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.service.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Stream;

@SpringBootTest
@AutoConfigureMockMvc
@WithAnonymousUser
@SuppressWarnings("unchecked")
public class ClubControllerTest {

    private ModerationAPIController moderationAPIController;

    private ClubController clubController;

    private ClubService clubService;

    private LocationService locationService;

    private TeamService teamService;

    private UserService userService;

    private SportService sportService;

    private FeedPostService feedPostService;

    private TableController tableController;

    private FeedPostService postService;

    private CommentService commentService;

    private NumCommentsService numCommentsService;

    private Authentication authentication;

    private NumPostsService numPostsService;


    @BeforeEach
    void setup() {
        SecurityContext context = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(context);
        authentication = Mockito.mock(Authentication.class);
        Mockito.when(context.getAuthentication()).thenReturn(authentication);

        clubService = Mockito.mock(ClubService.class);
        locationService = Mockito.mock(LocationService.class);
        teamService = Mockito.mock(TeamService.class);
        userService = Mockito.mock(UserService.class);
        sportService = Mockito.mock(SportService.class);
        tableController = Mockito.mock(TableController.class);
        feedPostService = Mockito.mock(FeedPostService.class);
        moderationAPIController = Mockito.mock(ModerationAPIController.class);

        postService = Mockito.mock(FeedPostService.class);
        commentService = Mockito.mock(CommentService.class);
        numCommentsService = Mockito.mock(NumCommentsService.class);

        numPostsService = Mockito.mock(NumPostsService.class);

        clubController = new ClubController(clubService,
                locationService,
                teamService,
                userService,
                tableController,
                sportService,
                postService,
                commentService,
                moderationAPIController,
                numCommentsService,
                numPostsService);
    }

    @Test
    void getCreatePage_test() {
        Model model = Mockito.spy(Model.class);
        User user = Mockito.mock(User.class);
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(user.getId()).thenReturn(1L);
        List<Team> teams = new ArrayList<>();
        Team team = Mockito.mock(Team.class);
        Mockito.when(team.getId()).thenReturn(1L);
        teams.add(team);
        Mockito.when(teamService.getTeamsThatUserManagesFromUserId(1L)).thenReturn(teams);
        Mockito.when(authentication.getPrincipal()).thenReturn("email");

        Assertions.assertEquals("clubForm", clubController.getCreate(model));
    }

    @Test
    void createClub_blueSky_test() {
        Model model = Mockito.spy(Model.class);
        RedirectAttributes redirectAttributes = Mockito.spy(RedirectAttributes.class);

        List<Team> teams = new ArrayList<>();
        Team team = Mockito.mock(Team.class);
        Mockito.when(team.getId()).thenReturn(1L);
        teams.add(team);

        // Create a mock MultipartFile
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                "test image content".getBytes()
        );

        clubController.makeClub("name", null, teams, file, "address1", "address2", "somewhere", "postcode", "Memphis", "United States of America", redirectAttributes, model);

        Mockito.verify(locationService, Mockito.times(1)).addLocation(Mockito.any());
        Mockito.verify(clubService, Mockito.times(1)).save(Mockito.any());
    }


    @Test
    void getSearch_shortSearch_loggedIn_test() {
        RedirectAttributes ra = Mockito.mock(RedirectAttributes.class);
        String uri = "test";
        String search = "se";

        Mockito.when(authentication.getPrincipal()).thenReturn("user");

        Assertions.assertEquals("redirect:" + uri, clubController.getSearch(ra, uri, search));
        Mockito.verify(ra, Mockito.times(1)).addFlashAttribute(Mockito.eq("searchError"), Mockito.eq("Please enter at least 3 characters"));
        Mockito.verify(ra, Mockito.times(1)).addFlashAttribute(Mockito.eq("loggedIn"), Mockito.eq(true));
        Mockito.verify(ra, Mockito.never()).addAttribute(Mockito.any(), Mockito.any());
    }

    @Test
    void getSearch_shortSearch_loggedOut_test() {
        RedirectAttributes ra = Mockito.mock(RedirectAttributes.class);
        String uri = "test";
        String search = "se";

        Mockito.when(authentication.getPrincipal()).thenReturn("anonymousUser");

        Assertions.assertEquals("redirect:" + uri, clubController.getSearch(ra, uri, search));
        Mockito.verify(ra, Mockito.times(1)).addFlashAttribute(Mockito.eq("searchError"), Mockito.eq("Please enter at least 3 characters"));
        Mockito.verify(ra, Mockito.times(1)).addFlashAttribute(Mockito.eq("loggedIn"), Mockito.eq(false));
        Mockito.verify(ra, Mockito.never()).addAttribute(Mockito.any(), Mockito.any());
    }

    @Test
    void getSearch_rightSearch_loggedOut_test() {
        RedirectAttributes ra = Mockito.mock(RedirectAttributes.class);
        String uri = "test";
        String search = "search";

        Mockito.when(authentication.getPrincipal()).thenReturn("anonymousUser");

        Assertions.assertEquals("redirect:/allClubs", clubController.getSearch(ra, uri, search));
        Mockito.verify(ra, Mockito.never()).addFlashAttribute(Mockito.any(), Mockito.any());

        Mockito.verify(ra, Mockito.times(1)).addAttribute("search", search);
    }


    @Test
    void viewClubs_shortSearch_hasQuery_hasClubs_test() {
        Model model = Mockito.mock(Model.class);
        String search = "ab";
        int page = 5;
        List<Long> selectedSports = Mockito.mock(List.class);
        List<String> selectedCities = Mockito.mock(List.class);
        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);

        Filter filter = Mockito.mock(Filter.class);
        Mockito.when(tableController.initializeOptionalParameters(search, selectedSports, selectedCities)).thenReturn(filter);

        Mockito.when(filter.getCities()).thenReturn(selectedCities);
        Mockito.when(filter.getSports()).thenReturn(selectedSports);
        Mockito.when(filter.getSearch()).thenReturn(search);

        List<Club> filteredClubs = Mockito.mock(List.class);
        List<Club> allClubs = Mockito.mock(List.class);
        Mockito.when(clubService.getClubsFiltered(selectedCities, selectedSports)).thenReturn(filteredClubs);
        Mockito.when(clubService.getClubs()).thenReturn(allClubs);

        Page<Club> pageTable = Mockito.mock(Page.class);
        Mockito.when(tableController.convertToPage(page, 10, filteredClubs)).thenReturn(pageTable);

        List<Sport> sports = Mockito.mock(List.class);
        Mockito.when(sportService.findAll()).thenReturn(sports);

        List<String> cities = Mockito.mock(List.class);
        Mockito.when(clubService.getCitiesFromClubs(allClubs)).thenReturn(cities);

        String qearyString = "test";
        Mockito.when(httpServletRequest.getQueryString()).thenReturn(qearyString);

        Mockito.when(filteredClubs.isEmpty()).thenReturn(false);

        //Function is run here
        Assertions.assertEquals("allClubs", clubController.viewClubs(page, search, selectedSports, selectedCities, httpServletRequest, model));


        Mockito.verify(tableController, Mockito.times(1)).setModelAttributes(model, pageTable, "allClubs", page, sports, cities, filter);
        Mockito.verify(model, Mockito.times(1)).addAttribute("previousSearchUri", "allClubs?" + qearyString);
        Mockito.verify(model, Mockito.never()).addAttribute(Mockito.eq("noClubsDisplay"), Mockito.any());
    }

    @Test
    void viewClubs_shortSearch_hasQuery_noClubs_test() {
        Model model = Mockito.mock(Model.class);
        String search = "ab";
        int page = 5;
        List<Long> selectedSports = Mockito.mock(List.class);
        List<String> selectedCities = Mockito.mock(List.class);
        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);

        Filter filter = Mockito.mock(Filter.class);
        Mockito.when(tableController.initializeOptionalParameters(search, selectedSports, selectedCities)).thenReturn(filter);

        Mockito.when(filter.getCities()).thenReturn(selectedCities);
        Mockito.when(filter.getSports()).thenReturn(selectedSports);
        Mockito.when(filter.getSearch()).thenReturn(search);

        List<Club> filteredClubs = Mockito.mock(List.class);
        List<Club> allClubs = Mockito.mock(List.class);
        Mockito.when(clubService.getClubsFiltered(selectedCities, selectedSports)).thenReturn(filteredClubs);
        Mockito.when(clubService.getClubs()).thenReturn(allClubs);

        Page<Club> pageTable = Mockito.mock(Page.class);
        Mockito.when(tableController.convertToPage(page, 10, filteredClubs)).thenReturn(pageTable);

        List<Sport> sports = Mockito.mock(List.class);
        Mockito.when(sportService.findAll()).thenReturn(sports);

        List<String> cities = Mockito.mock(List.class);
        Mockito.when(clubService.getCitiesFromClubs(allClubs)).thenReturn(cities);

        String qearyString = "test";
        Mockito.when(httpServletRequest.getQueryString()).thenReturn(qearyString);

        Mockito.when(filteredClubs.isEmpty()).thenReturn(true);

        String message = "OUT";
        Mockito.when(tableController.emptyEntriesMessage("clubs", search)).thenReturn(message);

        //Function is run here
        Assertions.assertEquals("allClubs", clubController.viewClubs(page, search, selectedSports, selectedCities, httpServletRequest, model));


        Mockito.verify(tableController, Mockito.times(1)).setModelAttributes(model, pageTable, "allClubs", page, sports, cities, filter);
        Mockito.verify(model, Mockito.times(1)).addAttribute("previousSearchUri", "allClubs?" + qearyString);
        Mockito.verify(model, Mockito.times(1)).addAttribute("noClubsDisplay", message);
    }

    @Test
    void viewClubs_shortSearch_noQuery_hasClubs_test() {
        Model model = Mockito.mock(Model.class);
        String search = "ab";
        int page = 5;
        List<Long> selectedSports = Mockito.mock(List.class);
        List<String> selectedCities = Mockito.mock(List.class);
        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);

        Filter filter = Mockito.mock(Filter.class);
        Mockito.when(tableController.initializeOptionalParameters(search, selectedSports, selectedCities)).thenReturn(filter);

        Mockito.when(filter.getCities()).thenReturn(selectedCities);
        Mockito.when(filter.getSports()).thenReturn(selectedSports);
        Mockito.when(filter.getSearch()).thenReturn(search);

        List<Club> filteredClubs = Mockito.mock(List.class);
        List<Club> allClubs = Mockito.mock(List.class);
        Mockito.when(clubService.getClubsFiltered(selectedCities, selectedSports)).thenReturn(filteredClubs);
        Mockito.when(clubService.getClubs()).thenReturn(allClubs);

        Page<Club> pageTable = Mockito.mock(Page.class);
        Mockito.when(tableController.convertToPage(page, 10, filteredClubs)).thenReturn(pageTable);

        List<Sport> sports = Mockito.mock(List.class);
        Mockito.when(sportService.findAll()).thenReturn(sports);

        List<String> cities = Mockito.mock(List.class);
        Mockito.when(clubService.getCitiesFromClubs(allClubs)).thenReturn(cities);

        Mockito.when(httpServletRequest.getQueryString()).thenReturn(null);

        Mockito.when(filteredClubs.isEmpty()).thenReturn(false);

        //Function is run here
        Assertions.assertEquals("allClubs", clubController.viewClubs(page, search, selectedSports, selectedCities, httpServletRequest, model));


        Mockito.verify(tableController, Mockito.times(1)).setModelAttributes(model, pageTable, "allClubs", page, sports, cities, filter);
        Mockito.verify(model, Mockito.times(1)).addAttribute("previousSearchUri", "allClubs");
        Mockito.verify(model, Mockito.never()).addAttribute(Mockito.eq("noClubsDisplay"), Mockito.any());
    }

    @Test
    void viewClubs_longSearch_hasQuery_hasClubs_test() {
        Model model = Mockito.mock(Model.class);
        String search = "abc";
        int page = 5;
        List<Long> selectedSports = Mockito.mock(List.class);
        List<String> selectedCities = Mockito.mock(List.class);
        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);

        Filter filter = Mockito.mock(Filter.class);
        Mockito.when(tableController.initializeOptionalParameters(search, selectedSports, selectedCities)).thenReturn(filter);

        Mockito.when(filter.getCities()).thenReturn(selectedCities);
        Mockito.when(filter.getSports()).thenReturn(selectedSports);
        Mockito.when(filter.getSearch()).thenReturn(search);

        List<Club> filteredClubs = Mockito.mock(List.class);
        List<Club> allClubs = Mockito.mock(List.class);
        Mockito.when(clubService.getClubsFilteredSearch(search, selectedCities, selectedSports)).thenReturn(filteredClubs);
        Mockito.when(clubService.getByKeyword(search)).thenReturn(allClubs);

        Page<Club> pageTable = Mockito.mock(Page.class);
        Mockito.when(tableController.convertToPage(page, 10, filteredClubs)).thenReturn(pageTable);

        List<Sport> sports = Mockito.mock(List.class);
        Mockito.when(sportService.findAll()).thenReturn(sports);

        List<String> cities = Mockito.mock(List.class);
        Mockito.when(clubService.getCitiesFromClubs(allClubs)).thenReturn(cities);

        String qearyString = "test";
        Mockito.when(httpServletRequest.getQueryString()).thenReturn(qearyString);

        Mockito.when(filteredClubs.isEmpty()).thenReturn(false);

        //Function is run here
        Assertions.assertEquals("allClubs", clubController.viewClubs(page, search, selectedSports, selectedCities, httpServletRequest, model));


        Mockito.verify(tableController, Mockito.times(1)).setModelAttributes(model, pageTable, "allClubs", page, sports, cities, filter);
        Mockito.verify(model, Mockito.times(1)).addAttribute("previousSearchUri", "allClubs?" + qearyString);
        Mockito.verify(model, Mockito.never()).addAttribute(Mockito.eq("noClubsDisplay"), Mockito.any());
    }

    @Test
    void viewClubs_nullSearch_hasQuery_hasClubs_test() {
        Model model = Mockito.mock(Model.class);
        int page = 5;
        List<Long> selectedSports = Mockito.mock(List.class);
        List<String> selectedCities = Mockito.mock(List.class);
        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);

        Filter filter = Mockito.mock(Filter.class);
        Mockito.when(tableController.initializeOptionalParameters(null, selectedSports, selectedCities)).thenReturn(filter);

        Mockito.when(filter.getCities()).thenReturn(selectedCities);
        Mockito.when(filter.getSports()).thenReturn(selectedSports);
        Mockito.when(filter.getSearch()).thenReturn(null);

        List<Club> filteredClubs = Mockito.mock(List.class);
        List<Club> allClubs = Mockito.mock(List.class);
        Mockito.when(clubService.getClubsFiltered(selectedCities, selectedSports)).thenReturn(filteredClubs);
        Mockito.when(clubService.getClubs()).thenReturn(allClubs);

        Page<Club> pageTable = Mockito.mock(Page.class);
        Mockito.when(tableController.convertToPage(page, 10, filteredClubs)).thenReturn(pageTable);

        List<Sport> sports = Mockito.mock(List.class);
        Mockito.when(sportService.findAll()).thenReturn(sports);

        List<String> cities = Mockito.mock(List.class);
        Mockito.when(clubService.getCitiesFromClubs(allClubs)).thenReturn(cities);

        String qearyString = "test";
        Mockito.when(httpServletRequest.getQueryString()).thenReturn(qearyString);

        Mockito.when(filteredClubs.isEmpty()).thenReturn(false);

        //Function is run here
        Assertions.assertEquals("allClubs", clubController.viewClubs(page, null, selectedSports, selectedCities, httpServletRequest, model));


        Mockito.verify(tableController, Mockito.times(1)).setModelAttributes(model, pageTable, "allClubs", page, sports, cities, filter);
        Mockito.verify(model, Mockito.times(1)).addAttribute("previousSearchUri", "allClubs?" + qearyString);
        Mockito.verify(model, Mockito.never()).addAttribute(Mockito.eq("noClubsDisplay"), Mockito.any());
    }

    @Test
    void getClubProfile_noClub() {
        Model model = Mockito.mock(Model.class);
        long id = 1L;
        Optional<Club> clubOptional = Optional.empty();
        Mockito.when(clubService.getClub(id)).thenReturn(clubOptional);

        Assertions.assertEquals("redirect:/",clubController.getClubProfile(id,model));
        Mockito.verify(model,Mockito.times(1)).addAttribute(Mockito.any(),Mockito.any());
    }

    @Test
    void getClubProfile_hasClub_noUser() {
        Model model = Mockito.mock(Model.class);
        long id = 1L;
        Club club = Mockito.mock(Club.class);
        Optional<Club> clubOptional = Optional.of(club);
        Mockito.when(clubService.getClub(id)).thenReturn(clubOptional);
        Mockito.when(userService.getUserByEmail(Mockito.any())).thenReturn(null);

        Mockito.when(club.getName()).thenReturn("a");
        Mockito.when(club.getSport()).thenReturn("s");
        List<Team> teams = Mockito.mock(List.class);
        Mockito.when(club.getTeams()).thenReturn(teams);

        Assertions.assertEquals("clubProfile", clubController.getClubProfile(id, model));
        Mockito.verify(model, Mockito.times(1)).addAttribute("clubName", "a");
        Mockito.verify(model, Mockito.times(1)).addAttribute("id", id);
        Mockito.verify(model, Mockito.times(1)).addAttribute("sport", "s");
        Mockito.verify(model, Mockito.times(1)).addAttribute("teams", teams);

        Mockito.verify(model,Mockito.times(1)).addAttribute(Mockito.eq("isManager"),Mockito.any());
    }

    @Test
    void getClubProfile_hasClub_hasWrongUser() {
        Model model = Mockito.mock(Model.class);
        long id = 1L;
        Club club = Mockito.mock(Club.class);
        Optional<Club> clubOptional = Optional.of(club);
        Mockito.when(clubService.getClub(id)).thenReturn(clubOptional);
        User user = Mockito.mock(User.class);
        Mockito.when(userService.getUserByEmail(Mockito.any())).thenReturn(user);
        Mockito.when(user.getId()).thenReturn(2L);

        Mockito.when(club.getName()).thenReturn("a");
        Mockito.when(club.getManager()).thenReturn(3L);
        Mockito.when(club.getSport()).thenReturn("s");
        List<Team> teams = Mockito.mock(List.class);
        Mockito.when(club.getTeams()).thenReturn(teams);

        Assertions.assertEquals("clubProfile",clubController.getClubProfile(id,model));
        Mockito.verify(model,Mockito.times(1)).addAttribute("clubName","a");
        Mockito.verify(model,Mockito.times(1)).addAttribute("id",id);
        Mockito.verify(model,Mockito.times(1)).addAttribute("sport","s");
        Mockito.verify(model,Mockito.times(1)).addAttribute("teams",teams);
        Mockito.verify(model,Mockito.times(1)).addAttribute(Mockito.eq("isManager"),Mockito.any());
    }

    @Test
    void getClubProfile_hasClub_hasRightUser() {
        Model model = Mockito.mock(Model.class);
        long id = 1L;
        Club club = Mockito.mock(Club.class);
        Optional<Club> clubOptional = Optional.of(club);
        Mockito.when(clubService.getClub(id)).thenReturn(clubOptional);
        User user = Mockito.mock(User.class);
        Mockito.when(userService.getUserByEmail(Mockito.any())).thenReturn(user);
        Mockito.when(user.getId()).thenReturn(2L);

        Mockito.when(club.getName()).thenReturn("a");
        Mockito.when(club.getManager()).thenReturn(2L);
        Mockito.when(club.getSport()).thenReturn("s");
        List<Team> teams = Mockito.mock(List.class);
        Mockito.when(club.getTeams()).thenReturn(teams);

        Assertions.assertEquals("clubProfile", clubController.getClubProfile(id, model));
        Mockito.verify(model, Mockito.times(1)).addAttribute("clubName", "a");
        Mockito.verify(model, Mockito.times(1)).addAttribute("id", id);
        Mockito.verify(model, Mockito.times(1)).addAttribute("isManager", true);
        Mockito.verify(model, Mockito.times(1)).addAttribute("sport", "s");
        Mockito.verify(model, Mockito.times(1)).addAttribute("teams", teams);
    }


    @Test
    void addDropdownTeamsCreate_test_isLoggedOut() {
        Mockito.when(authentication.getPrincipal()).thenReturn("anonymousUser");
        Assertions.assertEquals(0, clubController.addDropdownTeamsCreate().size());
    }

    @Test
    void addDropdownTeamsCreate_test_isLoggedIn() {
        Mockito.when(authentication.getPrincipal()).thenReturn("user");
        User user = Mockito.mock(User.class);
        Mockito.when(userService.getUserByEmail("user")).thenReturn(user);
        Mockito.when(user.getId()).thenReturn(2L);
        List<Team> expected = Mockito.mock(List.class);
        Mockito.when(teamService.getFreeTeamsThatUserManagesOrCoachesFromUserId(2L)).thenReturn(expected);

        Assertions.assertEquals(expected, clubController.addDropdownTeamsCreate());
    }

    @Test
    void addDropdownTeamsEdit_test_isLoggedOut() {
        Mockito.when(authentication.getPrincipal()).thenReturn("anonymousUser");
        Club club = Mockito.mock(Club.class);

        Assertions.assertEquals(0, clubController.addDropdownTeamsEdit(club).size());
    }

    @Test
    void addDropdownTeamsEdit_test_isLoggedIn() {
        Mockito.when(authentication.getPrincipal()).thenReturn("user");
        User user = Mockito.mock(User.class);
        Mockito.when(userService.getUserByEmail("user")).thenReturn(user);
        Mockito.when(user.getId()).thenReturn(2L);
        List<Team> l1 = new ArrayList<>();
        Mockito.when(teamService.getFreeTeamsThatUserManagesOrCoachesFromUserId(2L)).thenReturn(l1);
        Club club = Mockito.mock(Club.class);
        List<Team> l2 = new ArrayList<>();
        Mockito.when(club.getTeams()).thenReturn(l2);

        Team t1 = Mockito.mock(Team.class);
        Team t2 = Mockito.mock(Team.class);
        Team t3 = Mockito.mock(Team.class);
        Team t4 = Mockito.mock(Team.class);
        l2.add(t1);
        l2.add(t2);
        l1.add(t3);
        l1.add(t4);

        Assertions.assertEquals(4, clubController.addDropdownTeamsEdit(club).size());
        Assertions.assertEquals(t1, clubController.addDropdownTeamsEdit(club).get(0));
        Assertions.assertEquals(t2, clubController.addDropdownTeamsEdit(club).get(1));
        Assertions.assertEquals(t3, clubController.addDropdownTeamsEdit(club).get(2));
        Assertions.assertEquals(t4, clubController.addDropdownTeamsEdit(club).get(3));
    }

    @Test
    void createClub_nonExistentTeam_homeRedirection() {
        Model model = Mockito.spy(Model.class);
        RedirectAttributes redirectAttributes = Mockito.spy(RedirectAttributes.class);

        List<Team> teams = new ArrayList<>();
        Team team = Mockito.mock(Team.class);
        teams.add(team);
        Mockito.when(team.getId()).thenReturn(1L);
        Mockito.when(teamService.getTeam(1L)).thenReturn(Optional.empty());

        // Create a mock MultipartFile
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                "test image content".getBytes()
        );


        clubController.makeClub("name", null, teams, file, "address1", "address2", "somewhere", "postcode", "Memphis", "United States of America", redirectAttributes, model);
        Mockito.verify(model, Mockito.times(1)).addAttribute("teamError", "Please select a team.");
    }


    @Test
    void getPostForm_test() {
        Model model = Mockito.mock(Model.class);
        clubController.getPostForm(2L, model);

        Mockito.verify(model, Mockito.times(1)).addAttribute("submitPage", "clubPost");
        Mockito.verify(model, Mockito.times(1)).addAttribute("title", "Add a Club Post");
        Mockito.verify(model, Mockito.times(1)).addAttribute("id", 2L);

    }

    @Test
    void createPost_notManager() {
        RedirectAttributes redirectAttributes = Mockito.mock(RedirectAttributes.class);
        Mockito.when(clubService.managerCheck(2L)).thenReturn(false);
        Assertions.assertEquals("redirect:/clubPost", clubController.createPost(2L, "", "", null, redirectAttributes));

        Mockito.verify(postService, Mockito.never()).save(Mockito.any());
    }

    @Test
    void createPost_throws() {
        RedirectAttributes redirectAttributes = Mockito.mock(RedirectAttributes.class);
        Mockito.when(clubService.managerCheck(2L)).thenReturn(true);
        Club club = Mockito.mock(Club.class);
        Mockito.when(clubService.getClub(2L)).thenReturn(Optional.of(club));

        Assertions.assertEquals("redirect:/clubPost", clubController.createPost(2L, "test", "test", null, redirectAttributes));

        Mockito.verify(redirectAttributes, Mockito.times(1)).addFlashAttribute("error", "Failed to Save.");
        Mockito.verify(postService, Mockito.never()).save(Mockito.any());
    }


    @Test
    void viewClubProfile() {
        Club club = Mockito.mock(Club.class);
        User user = Mockito.mock(User.class);
        Model model = Mockito.mock(Model.class);

        Mockito.when(clubService.getClub(1L)).thenReturn(Optional.ofNullable(club));
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);

        FeedPost feedPost = Mockito.mock(FeedPost.class);

        List<FeedPost> feedPosts = new ArrayList<>(List.of(feedPost));

        List<Comment> comments = new ArrayList<>();
        List<String> usersFlagged = new ArrayList<>();
        List<Team> teams = new ArrayList<>();
        List<NumComments> numComments = new ArrayList<>();

        Mockito.when(feedPostService.getClubFeedPosts(Mockito.any())).thenReturn(feedPosts);

        var controller = new ClubController(clubService,
                locationService,
                teamService,
                userService,
                tableController,
                sportService,
                feedPostService,
                commentService,
                moderationAPIController,
                numCommentsService,
                numPostsService);

        controller.getClubProfile(1L, model);

        Mockito.verify(model).addAttribute("clubName", null);
        Mockito.verify(model).addAttribute("flaggedPostsPresent", false);
        Mockito.verify(model).addAttribute("flaggedComments", comments);
        Mockito.verify(model).addAttribute("flaggedCommentsPresent", false);
        Mockito.verify(model).addAttribute("usersFlagged", usersFlagged);
        Mockito.verify(model).addAttribute("canPost", false);
        Mockito.verify(model).addAttribute("id", 1L);
        Mockito.verify(model).addAttribute("sport", null);
        Mockito.verify(model).addAttribute("teams", teams);
        Mockito.verify(model).addAttribute("clubPicture", null);
        Mockito.verify(model).addAttribute("topThreeCommenters", numComments);

    }


    @Test
    void getClubEdit() {
        var controller = new ClubController(clubService,
                locationService,
                teamService,
                userService,
                tableController,
                sportService,
                feedPostService,
                commentService,
                moderationAPIController,
                numCommentsService,
                numPostsService);

        Club club = Mockito.mock(Club.class);
        User user = Mockito.mock(User.class);
        Model model = Mockito.mock(Model.class);
        Team team = Mockito.mock(Team.class);
        List<Team> teams = Collections.singletonList(team);
        Location location = Mockito.mock(Location.class);

        Mockito.when(clubService.managerCheck(1L)).thenReturn(true);
        Mockito.when(clubService.getClub(1L)).thenReturn(Optional.ofNullable(club));
        Mockito.when(Objects.requireNonNull(club).getTeams()).thenReturn(teams);

        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn("email");
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(user.getId()).thenReturn(1L);
        Mockito.when(teamService.getFreeTeamsThatUserManagesOrCoachesFromUserId(1L)).thenReturn(teams);
        Mockito.when(club.getTeams()).thenReturn(teams);
        Mockito.when(controller.addDropdownTeamsEdit(club)).thenReturn(teams);

        Mockito.when(club.getLocation()).thenReturn(location);
        Mockito.when(location.getAddressOne()).thenReturn("");
        Mockito.when(location.getAddressTwo()).thenReturn("");
        Mockito.when(location.getSuburb()).thenReturn("");
        Mockito.when(location.getPostcode()).thenReturn("");
        Mockito.when(location.getCity()).thenReturn("");
        Mockito.when(location.getCountry()).thenReturn("");
        Mockito.when(club.getProfilePicName()).thenReturn("");

        controller.editClub(1L, model);

        Mockito.verify(model).addAttribute("clubId", 1L);
        Mockito.verify(model).addAttribute("dropdownTeams", Stream.concat(teams.stream(), teams.stream()).toList());
        Mockito.verify(model).addAttribute("clubName", null);
        Mockito.verify(model).addAttribute("selectedTeams", teams);
        Mockito.verify(model).addAttribute("address1", "");
        Mockito.verify(model).addAttribute("address2", "");
        Mockito.verify(model).addAttribute("suburb", "");
        Mockito.verify(model).addAttribute("postcode", "");
        Mockito.verify(model).addAttribute("city", "");
        Mockito.verify(model).addAttribute("country", "");
        Mockito.verify(model).addAttribute("image", "");
    }

    @Test
    void myClubs_test() {
        Model model = Mockito.mock(Model.class);

        List<Club> clubs = List.of(Mockito.mock(Club.class), Mockito.mock(Club.class));

        Mockito.when(clubService.getAllClubsFromUser(Mockito.any())).thenReturn(new ArrayList<>(clubs));

        Assertions.assertEquals("myClubs", clubController.getMyClubs(1, model));

        Mockito.verify(model, Mockito.never()).addAttribute("noClubs");
    }
}
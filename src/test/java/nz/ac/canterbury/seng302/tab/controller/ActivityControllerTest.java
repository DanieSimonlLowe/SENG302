package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.ActivityRepository;
import nz.ac.canterbury.seng302.tab.service.*;
import org.junit.jupiter.api.Assertions;
import nz.ac.canterbury.seng302.tab.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;

import org.springframework.ui.Model;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "tom.barthelmeh@hotmail.com")
class ActivityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private Model model;

    private User user;

    private Team team;

    @Autowired
    private WebApplicationContext wac;

    @MockBean
    private UserService userService;

    /** The LocationService for database logic */
    @MockBean
    private LocationService locationService;

    @MockBean
    private ActivityService activityService;

    @MockBean
    private LineupService lineupService;

    @MockBean
    private FormationService formationService;

    @MockBean
    private LineupPlayerService lineupPlayerService;

    @MockBean
    private TeamService teamService;

    @MockBean
    private FeedPostService feedPostService;

    @MockBean
    private ActivityRepository activityRepository;

    private final ActivityService activityS = new ActivityService(activityRepository);



    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        this.model = mock(Model.class);

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn("email");
        this.user = Mockito.mock(User.class);
        this.team = Mockito.mock(Team.class);
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(teamService.getTeam(1)).thenReturn(Optional.ofNullable(team));

        Mockito.when(user.getEmail()).thenReturn("e");
        Mockito.when(user.getFirstName()).thenReturn("fn");
        Mockito.when(user.getLastName()).thenReturn("ln");
        Mockito.when(user.getDate()).thenReturn(LocalDate.EPOCH);
        Mockito.when(user.getLocationString()).thenReturn("l");
        Mockito.when(user.getProfilePicName()).thenReturn("p");
        Mockito.when(user.getId()).thenReturn(0L);

        Mockito.when(team.getId()).thenReturn(1L);
        Mockito.when(team.getName()).thenReturn("nam");
        Mockito.when(team.getSport()).thenReturn("s");
        Mockito.when(team.getLocationString()).thenReturn("l");
        Mockito.when(team.getCreationDate()).thenReturn(LocalDate.EPOCH.atStartOfDay());

    }

    @Test
    void creatingActivityFeedPost() throws Exception {
        Location activityLocation = new Location("address1", "", "suburb", "1234", "city", "country");
        Activity activity = new Activity(ActivityType.TRAINING, null, null, "", LocalDateTime.now(), LocalDateTime.now(), user, activityLocation);
        Mockito.when(activityService.getActivity(1L)).thenReturn(activity);
        Assertions.assertNotNull(ActivityController.createFeedPost(activity));
    }

    @Test
    void whenInvalidActivityTime_thenError() throws Exception {
        Location activityLocation = new Location("address1", "", "suburb", "1234", "city", "country");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm");
        String startTime = java.time.LocalDateTime.now().format(formatter);

        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("actType", "training");
        requestParams.add("actDesc", "This is an activity description");
        requestParams.add("actStart", startTime);
        // Error so start time is same as end time.
        requestParams.add("actEnd", startTime);
        requestParams.add("actTeam", "0");
        requestParams.add("actOpposition", "0");
        requestParams.add("address1", activityLocation.getAddressOne());
        requestParams.add("address2", activityLocation.getAddressTwo());
        requestParams.add("suburb", activityLocation.getSuburb());
        requestParams.add("postcode", activityLocation.getPostcode());
        requestParams.add("city", activityLocation.getCity());
        requestParams.add("country", activityLocation.getCountry());

        requestParams.add("id", null);
        requestParams.add("players", "1,");
        requestParams.add("formationStore", "0");
        requestParams.add("substitutions", "1,");

        mockMvc.perform(post("/activityForm")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .params(requestParams))
                .andExpect(model().attribute("endTimeError", "The end time of an activity cannot be before the start time."))
                .andExpect(view().name("activityForm"));
    }

    @Test
    void whenInvalidFormationId_thenError() throws Exception {
        Location activityLocation = new Location("address1", "", "suburb", "1234", "city", "country");

        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("actType", "game");
        requestParams.add("actDesc", "This is an activity description");
        requestParams.add("actStart", "9999-11-01T01:01");
        requestParams.add("actEnd", "9999-12-01T01:01");
        requestParams.add("actTeam", "1");
        requestParams.add("actOpposition", "0");
        requestParams.add("address1", activityLocation.getAddressOne());
        requestParams.add("address2", activityLocation.getAddressTwo());
        requestParams.add("suburb", activityLocation.getSuburb());
        requestParams.add("postcode", activityLocation.getPostcode());
        requestParams.add("city", activityLocation.getCity());
        requestParams.add("country", activityLocation.getCountry());

        requestParams.add("players", "1");
        requestParams.add("formationStore", "0");
        requestParams.add("substitutions", "");

        mockMvc.perform(post("/activityForm")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .params(requestParams))
                .andExpect(model().attribute("invalidLineup", "Could not load formation."))
                .andExpect(view().name("activityForm"));
    }

    @Test
    void whenInvalidLineup_PlayerMoreThanOnePosition_thenError() throws Exception {
        Location activityLocation = new Location("address1", "", "suburb", "1234", "city", "country");

        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("actType", "game");
        requestParams.add("actDesc", "This is an activity description");
        requestParams.add("actStart", "9999-11-01T01:01");
        requestParams.add("actEnd", "9999-12-01T01:01");
        requestParams.add("actTeam", "1");
        requestParams.add("actOpposition", "0");
        requestParams.add("address1", activityLocation.getAddressOne());
        requestParams.add("address2", activityLocation.getAddressTwo());
        requestParams.add("suburb", activityLocation.getSuburb());
        requestParams.add("postcode", activityLocation.getPostcode());
        requestParams.add("city", activityLocation.getCity());
        requestParams.add("country", activityLocation.getCountry());

        requestParams.add("players", "1,1,1");
        requestParams.add("formationStore", "1");
        requestParams.add("substitutions", "");

        Mockito.when(formationService.getFormationById(1)).thenReturn(Optional.of(new Formation("2-1", "football_pitch", team)));

        mockMvc.perform(post("/activityForm")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .params(requestParams))
                .andExpect(model().attribute("invalidLineup", "Lineup could not be saved a player cannot be in more than one position."))
                .andExpect(view().name("activityForm"));
    }

    @Test
    void whenInvalidLineup_populatedSubs_noPlayers_thenError() throws Exception {
        Location activityLocation = new Location("address1", "", "suburb", "1234", "city", "country");

        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("actType", "game");
        requestParams.add("actDesc", "This is an activity description");
        requestParams.add("actStart", "9999-11-01T01:01");
        requestParams.add("actEnd", "9999-12-01T01:01");
        requestParams.add("actTeam", "1");
        requestParams.add("actOpposition", "0");
        requestParams.add("address1", activityLocation.getAddressOne());
        requestParams.add("address2", activityLocation.getAddressTwo());
        requestParams.add("suburb", activityLocation.getSuburb());
        requestParams.add("postcode", activityLocation.getPostcode());
        requestParams.add("city", activityLocation.getCity());
        requestParams.add("country", activityLocation.getCountry());

        requestParams.add("players", "-1");
        requestParams.add("formationStore", "1");
        requestParams.add("substitutions", "1");

        Mockito.when(formationService.getFormationById(1)).thenReturn(Optional.of(new Formation("2-1", "football_pitch", team)));

        mockMvc.perform(post("/activityForm")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .params(requestParams))
                .andExpect(model().attribute("invalidLineup", "Lineup could not be saved as not all player slots have been filled."))
                .andExpect(view().name("activityForm"));
    }

    @Test
    void whenInvalidLineup_NotEnoughPlayers_thenError() throws Exception {
        Location activityLocation = new Location("address1", "", "suburb", "1234", "city", "country");

        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("actType", "game");
        requestParams.add("actDesc", "This is an activity description");
        requestParams.add("actStart", "9999-11-01T01:01");
        requestParams.add("actEnd", "9999-12-01T01:01");
        requestParams.add("actTeam", "1");
        requestParams.add("actOpposition", "0");
        requestParams.add("address1", activityLocation.getAddressOne());
        requestParams.add("address2", activityLocation.getAddressTwo());
        requestParams.add("suburb", activityLocation.getSuburb());
        requestParams.add("postcode", activityLocation.getPostcode());
        requestParams.add("city", activityLocation.getCity());
        requestParams.add("country", activityLocation.getCountry());

        // -1 Indicates an empty slot
        requestParams.add("players", "1,-1");
        requestParams.add("formationStore", "1");
        requestParams.add("substitutions", "1");

        Mockito.when(formationService.getFormationById(1)).thenReturn(Optional.of(new Formation("2-1", "football_pitch", team)));

        mockMvc.perform(post("/activityForm")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .params(requestParams))
                .andExpect(model().attribute("invalidLineup", "Lineup could not be saved as not all player slots have been filled."))
                .andExpect(view().name("activityForm"));
    }

    @Test
    void whenNoActivityType_thenError() throws Exception {
        Location activityLocation = new Location("address1", "", "suburb", "1234", "city", "country");

        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("actType", "None");
        requestParams.add("actDesc", "This is an activity description");
        requestParams.add("actStart", "9999-11-01T01:01");
        requestParams.add("actEnd", "9999-12-01T01:01");
        requestParams.add("actTeam", "1");
        requestParams.add("actOpposition", "0");
        requestParams.add("address1", activityLocation.getAddressOne());
        requestParams.add("address2", activityLocation.getAddressTwo());
        requestParams.add("suburb", activityLocation.getSuburb());
        requestParams.add("postcode", activityLocation.getPostcode());
        requestParams.add("city", activityLocation.getCity());
        requestParams.add("country", activityLocation.getCountry());

        requestParams.add("players", "1");
        requestParams.add("formationStore", "0");
        requestParams.add("substitutions", "");

        mockMvc.perform(post("/activityForm")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .params(requestParams))
                .andExpect(model().attribute("typeError", "An activity type is required"))
                .andExpect(view().name("activityForm"));
    }

    @Test
    void whenBlankDescription_thenError() throws Exception {
        Location activityLocation = new Location("address1", "", "suburb", "1234", "city", "country");

        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("actType", "game");
        requestParams.add("actDesc", "");
        requestParams.add("actStart", "9999-11-01T01:01");
        requestParams.add("actEnd", "9999-12-01T01:01");
        requestParams.add("actTeam", "1");
        requestParams.add("actOpposition", "0");
        requestParams.add("address1", activityLocation.getAddressOne());
        requestParams.add("address2", activityLocation.getAddressTwo());
        requestParams.add("suburb", activityLocation.getSuburb());
        requestParams.add("postcode", activityLocation.getPostcode());
        requestParams.add("city", activityLocation.getCity());
        requestParams.add("country", activityLocation.getCountry());

        requestParams.add("players", "1");
        requestParams.add("formationStore", "0");
        requestParams.add("substitutions", "");

        mockMvc.perform(post("/activityForm")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .params(requestParams))
                .andExpect(model().attribute("descError", "An activity description is required"))
                .andExpect(view().name("activityForm"));
    }
    @Test
    void whenBlankStart_thenError() throws Exception {
        Location activityLocation = new Location("address1", "", "suburb", "1234", "city", "country");

        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("actType", "game");
        requestParams.add("actDesc", "This is an activity description");
        requestParams.add("actStart", "");
        requestParams.add("actEnd", "9999-12-01T01:01");
        requestParams.add("actTeam", "1");
        requestParams.add("actOpposition", "0");
        requestParams.add("address1", activityLocation.getAddressOne());
        requestParams.add("address2", activityLocation.getAddressTwo());
        requestParams.add("suburb", activityLocation.getSuburb());
        requestParams.add("postcode", activityLocation.getPostcode());
        requestParams.add("city", activityLocation.getCity());
        requestParams.add("country", activityLocation.getCountry());

        requestParams.add("players", "1");
        requestParams.add("formationStore", "0");
        requestParams.add("substitutions", "");

        mockMvc.perform(post("/activityForm")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .params(requestParams))
                .andExpect(model().attribute("startTimeError", "An activity start time is required"))
                .andExpect(view().name("activityForm"));
    }

    @Test
    void whenBlankEndTime_thenError() throws Exception {
        Location activityLocation = new Location("address1", "", "suburb", "1234", "city", "country");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm");
        String startTime = java.time.LocalDateTime.now().format(formatter);

        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("actType", "training");
        requestParams.add("actDesc", "This is an activity description");
        requestParams.add("actStart", startTime);
        // Error so start time is same as end time.
        requestParams.add("actEnd", "");
        requestParams.add("actTeam", "0");
        requestParams.add("actOpposition", "0");
        requestParams.add("address1", activityLocation.getAddressOne());
        requestParams.add("address2", activityLocation.getAddressTwo());
        requestParams.add("suburb", activityLocation.getSuburb());
        requestParams.add("postcode", activityLocation.getPostcode());
        requestParams.add("city", activityLocation.getCity());
        requestParams.add("country", activityLocation.getCountry());

        requestParams.add("id", null);
        requestParams.add("players", "1,");
        requestParams.add("formationStore", "0");
        requestParams.add("substitutions", "1,");

        mockMvc.perform(post("/activityForm")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .params(requestParams))
                .andExpect(model().attribute("endTimeError", "An activity end time is required"))
                .andExpect(view().name("activityForm"));
    }
    @Test
    void whenBlankAddress1_thenError() throws Exception {
        Location activityLocation = new Location("address1", "", "suburb", "1234", "city", "country");

        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("actType", "game");
        requestParams.add("actDesc", "This is an activity description");
        requestParams.add("actStart", "9999-11-01T01:01");
        requestParams.add("actEnd", "9999-12-01T01:01");
        requestParams.add("actTeam", "1");
        requestParams.add("actOpposition", "0");
        requestParams.add("address1", "");
        requestParams.add("address2", activityLocation.getAddressTwo());
        requestParams.add("suburb", activityLocation.getSuburb());
        requestParams.add("postcode", activityLocation.getPostcode());
        requestParams.add("city", activityLocation.getCity());
        requestParams.add("country", activityLocation.getCountry());

        requestParams.add("players", "1");
        requestParams.add("formationStore", "0");
        requestParams.add("substitutions", "");

        mockMvc.perform(post("/activityForm")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .params(requestParams))
                .andExpect(model().attribute("invalidAddress1", "Please enter an address 1 containing at least one digit alongside any letters. Hyphens and apostrophes are also acceptable."))
                .andExpect(view().name("activityForm"));
    }
    @Test
    void whenBlankPostcode_thenError() throws Exception {
        Location activityLocation = new Location("address1", "", "suburb", "1234", "city", "country");

        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("actType", "game");
        requestParams.add("actDesc", "This is an activity description");
        requestParams.add("actStart", "9999-11-01T01:01");
        requestParams.add("actEnd", "9999-12-01T01:01");
        requestParams.add("actTeam", "1");
        requestParams.add("actOpposition", "0");
        requestParams.add("address1", activityLocation.getAddressOne());
        requestParams.add("address2", activityLocation.getAddressTwo());
        requestParams.add("suburb", activityLocation.getPostcode());
        requestParams.add("postcode", "");
        requestParams.add("city", activityLocation.getCity());
        requestParams.add("country", activityLocation.getCountry());

        requestParams.add("players", "1");
        requestParams.add("formationStore", "0");
        requestParams.add("substitutions", "");

        mockMvc.perform(post("/activityForm")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .params(requestParams))
                .andExpect(model().attribute("invalidPostcode", "Please enter a postcode which only contains numbers, letters or hyphens."))
                .andExpect(view().name("activityForm"));
    }

    @Test
    void whenBlankCity_thenError() throws Exception {
        Location activityLocation = new Location("address1", "", "suburb", "1234", "city", "country");

        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("actType", "game");
        requestParams.add("actDesc", "This is an activity description");
        requestParams.add("actStart", "9999-11-01T01:01");
        requestParams.add("actEnd", "9999-12-01T01:01");
        requestParams.add("actTeam", "1");
        requestParams.add("actOpposition", "0");
        requestParams.add("address1", activityLocation.getAddressOne());
        requestParams.add("address2", activityLocation.getAddressTwo());
        requestParams.add("suburb", activityLocation.getPostcode());
        requestParams.add("postcode", activityLocation.getPostcode());
        requestParams.add("city", "");
        requestParams.add("country", activityLocation.getCountry());

        requestParams.add("players", "1");
        requestParams.add("formationStore", "0");
        requestParams.add("substitutions", "");

        mockMvc.perform(post("/activityForm")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .params(requestParams))
                .andExpect(model().attribute("invalidCity", "Please enter a city which can only contain letters or hyphens."))
                .andExpect(view().name("activityForm"));
    }

    @Test
    void whenBlankCountry_thenError() throws Exception {
        Location activityLocation = new Location("address1", "", "suburb", "1234", "city", "country");

        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("actType", "game");
        requestParams.add("actDesc", "This is an activity description");
        requestParams.add("actStart", "9999-11-01T01:01");
        requestParams.add("actEnd", "9999-12-01T01:01");
        requestParams.add("actTeam", "1");
        requestParams.add("actOpposition", "0");
        requestParams.add("address1", activityLocation.getAddressOne());
        requestParams.add("address2", activityLocation.getAddressTwo());
        requestParams.add("suburb", activityLocation.getPostcode());
        requestParams.add("postcode", activityLocation.getPostcode());
        requestParams.add("city", activityLocation.getCity());
        requestParams.add("country", "");

        requestParams.add("players", "1");
        requestParams.add("formationStore", "0");
        requestParams.add("substitutions", "");

        mockMvc.perform(post("/activityForm")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .params(requestParams))
                .andExpect(model().attribute("invalidCountry", "Please enter a country which can only contain letters or hyphens."))
                .andExpect(view().name("activityForm"));
    }

    @Test
    void whenGameNoTeam_thenError() throws Exception {
        Location activityLocation = new Location("address1", "", "suburb", "1234", "city", "country");

        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("actType", "game");
        requestParams.add("actDesc", "This is an activity description");
        requestParams.add("actStart", "9999-11-01T01:01");
        requestParams.add("actEnd", "9999-12-01T01:01");
        requestParams.add("actTeam", "0");
        requestParams.add("actOpposition", "0");
        requestParams.add("address1", activityLocation.getAddressOne());
        requestParams.add("address2", activityLocation.getAddressTwo());
        requestParams.add("suburb", activityLocation.getPostcode());
        requestParams.add("postcode", activityLocation.getPostcode());
        requestParams.add("city", activityLocation.getCity());
        requestParams.add("country", activityLocation.getCountry());

        requestParams.add("players", "1");
        requestParams.add("formationStore", "0");
        requestParams.add("substitutions", "");

        mockMvc.perform(post("/activityForm")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .params(requestParams))
                .andExpect(model().attribute("typeError", "Activity types 'Game' and 'Friendly' require a team."))
                .andExpect(view().name("activityForm"));
    }

    @Test
    void whenActivityDateBeforeTeamCreation_thenError() throws Exception {
        Location activityLocation = new Location("address1", "", "suburb", "1234", "city", "country");

        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("actType", "game");
        requestParams.add("actDesc", "This is an activity description");
        requestParams.add("actStart", "0001-11-01T01:01");
        requestParams.add("actEnd", "0001-12-01T01:01");
        requestParams.add("actTeam", "1");
        requestParams.add("actOpposition", "0");
        requestParams.add("address1", activityLocation.getAddressOne());
        requestParams.add("address2", activityLocation.getAddressTwo());
        requestParams.add("suburb", activityLocation.getPostcode());
        requestParams.add("postcode", activityLocation.getPostcode());
        requestParams.add("city", activityLocation.getCity());
        requestParams.add("country", activityLocation.getCountry());

        requestParams.add("players", "1");
        requestParams.add("formationStore", "0");
        requestParams.add("substitutions", "");

        mockMvc.perform(post("/activityForm")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .params(requestParams))
                .andExpect(model().attributeExists("startTimeError"))
                .andExpect(view().name("activityForm"));
    }

    @Test
    void whenDescriptionAllNonAlphabeticalThenError() throws Exception {
        Location activityLocation = new Location("address1", "", "suburb", "1234", "city", "country");

        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("actType", "game");
        requestParams.add("actDesc", "123");
        requestParams.add("actStart", "9999-11-01T01:01");
        requestParams.add("actEnd", "9999-12-01T01:01");
        requestParams.add("actTeam", "1");
        requestParams.add("actOpposition", "0");
        requestParams.add("address1", activityLocation.getAddressOne());
        requestParams.add("address2", activityLocation.getAddressTwo());
        requestParams.add("suburb", activityLocation.getSuburb());
        requestParams.add("postcode", activityLocation.getPostcode());
        requestParams.add("city", activityLocation.getCity());
        requestParams.add("country", activityLocation.getCountry());

        requestParams.add("players", "1");
        requestParams.add("formationStore", "0");
        requestParams.add("substitutions", "");

        mockMvc.perform(post("/activityForm")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .params(requestParams))
                .andExpect(model().attribute("descError", "Please enter a description made up of alphabetical characters and is no longer than 150 characters."))
                .andExpect(view().name("activityForm"));
    }


    @Test
    void viewMyActivities_test() throws Exception {
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        String JS1 = "{id: \"0\", start: \"9999-11-01T01:01\", end:\"9999-12-01T01:01\", title: \"help\", color: \"\"},";
        String JS2 = "{id: \"1\", start: \"9999-11-01T01:01\", end:\"9999-12-01T01:01\", title: \"help\", color: \"\"},";
        String JS3 = "{id: \"2\", start: \"9999-11-01T01:01\", end:\"9999-12-01T01:01\", title: \"help\", color: \"\"},";
        String JS4 = "{id: \"3\", start: \"9999-11-01T01:01\", end:\"9999-12-01T01:01\", title: \"help\", color: \"\"},";

        Team opp = Mockito.mock(Team.class);
        Mockito.when(opp.getName()).thenReturn("help");

        Activity activity1 = Mockito.mock(Activity.class);
        Mockito.when(activity1.getId()).thenReturn(1L);
        Mockito.when(activity1.getTeam()).thenReturn(team);
        Mockito.when(activity1.getJavaScriptEvent(Math.toIntExact(1L))).thenReturn(JS1);
        Mockito.when(activity1.getOpposition()).thenReturn(opp);
        Mockito.when(activity1.getType()).thenReturn(ActivityType.GAME);

        Activity activity2 = Mockito.mock(Activity.class);
        Mockito.when(activity2.getId()).thenReturn(2L);
        Mockito.when(activity2.getTeam()).thenReturn(team);
        Mockito.when(activity2.getJavaScriptEvent(Math.toIntExact(2L))).thenReturn(JS2);
        Mockito.when(activity2.getOpposition()).thenReturn(opp);
        Mockito.when(activity2.getType()).thenReturn(ActivityType.GAME);

        Activity activity3 = Mockito.mock(Activity.class);
        Mockito.when(activity3.getId()).thenReturn(2L);
        Mockito.when(activity3.getTeam()).thenReturn(team);
        Mockito.when(activity3.getJavaScriptEvent(Math.toIntExact(2L))).thenReturn(JS3);
        Mockito.when(activity3.getOpposition()).thenReturn(opp);
        Mockito.when(activity3.getType()).thenReturn(ActivityType.GAME);

        Activity activity4 = Mockito.mock(Activity.class);
        Mockito.when(activity4.getId()).thenReturn(1L);
        Mockito.when(activity4.getTeam()).thenReturn(team);
        Mockito.when(activity4.getJavaScriptEvent(Math.toIntExact(1L))).thenReturn(JS4);
        Mockito.when(activity4.getOpposition()).thenReturn(opp);
        Mockito.when(activity4.getType()).thenReturn(ActivityType.GAME);

        List<Activity> userActivityList = new ArrayList<>();
        userActivityList.add(activity1);
        userActivityList.add(activity2);

        List<Activity> teamActivityList = new ArrayList<>();
        teamActivityList.add(activity3);
        teamActivityList.add(activity4);

        Mockito.when(activityService.getMyTeamActivities(0L)).thenReturn(userActivityList);
        Mockito.when(activityService.getMyPersonalActivities(0L)).thenReturn(teamActivityList);

        List<Activity> activities = Stream.concat(teamActivityList.stream(), userActivityList.stream()).toList();

        Mockito.when(activityService.getByIdCanEdit(1L,user)).thenReturn(null);
        Mockito.when(activityService.getByIdCanEdit(2L,user)).thenReturn(userActivityList.get(0));

        Model model = Mockito.mock(Model.class);

        var controller = new ActivityController(teamService, userService, activityService, locationService, formationService, lineupService, lineupPlayerService, feedPostService);

        String out = controller.viewMyActivities(model);

        Assertions.assertEquals("activityCalendar",out);

        ArrayList<Boolean> canEdit = new ArrayList<>();
        canEdit.add(true);
        canEdit.add(false);
        canEdit.add(false);
        canEdit.add(true);

        mockMvc.perform(get("/viewActivities"))
                .andExpect(status().is2xxSuccessful());

        Mockito.verify(model).addAttribute("activities", activities);

        Mockito.verify(model).addAttribute("canEdit",canEdit);


    }


    @Test
    void whenActTeamNotExistError() throws Exception {
        Location activityLocation = new Location("address1", "", "suburb", "1234", "city", "country");

        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("actType", "game");
        requestParams.add("actDesc", "Test");
        requestParams.add("actStart", "9999-11-01T01:01");
        requestParams.add("actEnd", "9999-12-01T01:01");
        requestParams.add("actTeam", "-99");
        requestParams.add("actOpposition", "0");
        requestParams.add("address1", activityLocation.getAddressOne());
        requestParams.add("address2", activityLocation.getAddressTwo());
        requestParams.add("suburb", activityLocation.getSuburb());
        requestParams.add("postcode", activityLocation.getPostcode());
        requestParams.add("city", activityLocation.getCity());
        requestParams.add("country", activityLocation.getCountry());

        requestParams.add("players", "1");
        requestParams.add("formationStore", "0");
        requestParams.add("substitutions", "");

        mockMvc.perform(post("/activityForm")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .params(requestParams))
                .andExpect(model().attribute("teamError", "Team does not exist."))
                .andExpect(view().name("activityForm"));
    }

    @Test
    void whenOppositionTeamNotExistError() throws Exception {
        Location activityLocation = new Location("address1", "", "suburb", "1234", "city", "country");

        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("actType", "game");
        requestParams.add("actDesc", "Test");
        requestParams.add("actStart", "9999-11-01T01:01");
        requestParams.add("actEnd", "9999-12-01T01:01");
        requestParams.add("actTeam", "1");
        requestParams.add("actOpposition", "-99");
        requestParams.add("address1", activityLocation.getAddressOne());
        requestParams.add("address2", activityLocation.getAddressTwo());
        requestParams.add("suburb", activityLocation.getSuburb());
        requestParams.add("postcode", activityLocation.getPostcode());
        requestParams.add("city", activityLocation.getCity());
        requestParams.add("country", activityLocation.getCountry());

        requestParams.add("players", "1");
        requestParams.add("formationStore", "0");
        requestParams.add("substitutions", "");



        mockMvc.perform(post("/activityForm")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .params(requestParams))
                .andExpect(model().attribute("oppositionError", "Team does not exist."))
                .andExpect(view().name("activityForm"));
    }

    @Test
    void testColourOfModel() throws Exception {
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        LocalDateTime start = LocalDateTime.of(2023, 9, 25, 12, 0);
        LocalDateTime endt = LocalDateTime.of(2023, 10, 20, 12, 0);
        Location activityLocation = new Location("address1", "", "suburb", "1234", "city", "country");


        String JS1 = "{id: \"0\", start: \"2021-09-04T01:01\", end:\"2023-10-01T01:01\", title: \"help\", color: \"\"},";

        Team opp = Mockito.mock(Team.class);
        Mockito.when(opp.getName()).thenReturn("help");
        Mockito.when(opp.getId()).thenReturn(9L);

        String colour = activityS.generateHexColour(9L);

        Activity activity1 = new Activity(ActivityType.GAME, opp, opp, "help", start, endt, user, activityLocation);
        activity1.setId(1L);


        List<Activity> userActivityList = new ArrayList<>();
        userActivityList.add(activity1);
        Mockito.when(activityService.getMyTeamActivities(0L)).thenReturn(userActivityList);
        Mockito.when(activityService.generateHexColour(9L)).thenReturn(colour);

        Model model = Mockito.mock(Model.class);

        var controller = new ActivityController(teamService, userService, activityService, locationService, formationService, lineupService, lineupPlayerService, feedPostService);

        String out = controller.viewMyActivities(model);

        Assertions.assertEquals("activityCalendar",out);

        mockMvc.perform(get("/viewActivities"))
                .andExpect(status().is2xxSuccessful());

        Mockito.verify(model).addAttribute("activities", userActivityList);
        Mockito.verify(model).addAttribute("activitiesJavaScript", "[{id: \"help1\", start: \"2023-09-25T12:00:00\", end: \"2023-10-20T12:00:00\", title: \"help\", backgroundColor: \"#e5aa70\"},]");

    }
}

package nz.ac.canterbury.seng302.tab.integration;

import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.repository.LocationRepository;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@SpringBootTest
@Transactional
@WithMockUser(username = "morgan.english@hotmail.com")
class TeamSearchTest {
    private MockMvc mockMvc;
    @Resource
    private TeamRepository teamRepository;

    @Resource
    private LocationRepository locationRepository;

    @Autowired
    private WebApplicationContext wac;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("morgan.english@hotmail.com", null));
    }

    @Test
    void searchTeamInsufficientQueryLength() throws Exception {
        String searchQuery = "Me";

        mockMvc.perform(post("/searchTeams").param("search", searchQuery))
                .andExpect(flash().attribute("searchError", "Please enter at least 3 characters"));
    }

    @Test
    void searchTeam() throws Exception {
        List<Team> expectedTeams = new ArrayList<>();
        Location location = new Location("", "", "", "", "Memphis", "United States of America");
        locationRepository.save(location);
        Team team = new Team(
                "Grizzles",
                location,
                "Basketball"
        );

        teamRepository.save(team);
        expectedTeams.add(team);

        Page<Team> expectedTeamsPage = new PageImpl<>(expectedTeams, PageRequest.of(0, 10),0);

        mockMvc.perform(get("/allTeams").param("search", "Memphis"))
                .andExpect(model().attribute("entries", expectedTeamsPage));

    }

    @Test
    void searchMultipleTeams() throws Exception {
        List<Team> expectedTeams = new ArrayList<>();

        Location locationA = new Location("", "", "", "", "Memphis", "United States of America");
        Location locationB = new Location("", "", "", "", "Test-land", "United States of America");
        locationRepository.save(locationA);
        locationRepository.save(locationB);

        Team team1 = new Team(
                "Grizzles",
                locationA,
                "Basketball"
        );

        Team team2 = new Team(
                "Heat",
                locationB,
                "Basketball"
        );

        Team team3 = new Team(
                "Dolphins",
                locationB,
                "AmericanFootball"
        );

        teamRepository.save(team1);
        teamRepository.save(team2);
        teamRepository.save(team3);

        expectedTeams.add(team3);
        expectedTeams.add(team2);

        Page<Team> expectedTeamsPage = new PageImpl<>(expectedTeams, PageRequest.of(0, 10),0);

        mockMvc.perform(get("/allTeams").param("search", "Test-land"))
                .andExpect(model().attribute("entries", expectedTeamsPage));
    }

    @Test
    void searchNonExistentTeam() throws Exception {
        List<Team> expectedTeams = new ArrayList<>();

        Location location = new Location("", "", "", "", "Memphis", "United States of America");
        locationRepository.save(location);

        Team team1 = new Team(
                "Grizzles",
                location,
                "Basketball"
        );

        teamRepository.save(team1);

        Page<Team> expectedTeamsPage = new PageImpl<>(expectedTeams, PageRequest.of(0, 10),0);

        mockMvc.perform(get("/allTeams").param("search", "NON-EXISTENT"))
                .andExpect(model().attribute("entries", expectedTeamsPage));
    }

    @Test
    void searchLocationSortByName() throws Exception {
        List<Team> expectedTeams = new ArrayList<>();

        Location aLocation = new Location("", "", "", "", "Test-land", "New Zealand");
        Location bLocation = new Location("", "", "", "", "Dunedin", "New Zealand");
        locationRepository.save(aLocation);
        locationRepository.save(bLocation);


        Team aTeam = new Team(
                "aTeam",
                aLocation,
                "Basketball"
        );

        Team bTeam1 = new Team(
                "bTeam",
                aLocation,
                "Basketball"
        );

        Team cTeam = new Team(
                "cTeam",
                aLocation,
                "Basketball"
        );

        Team bTeam2 = new Team(
                "bTeam",
                bLocation,
                "Basketball"
        );

        teamRepository.save(bTeam1);
        teamRepository.save(aTeam);
        teamRepository.save(cTeam);
        teamRepository.save(bTeam2);

        expectedTeams.add(aTeam);
        expectedTeams.add(bTeam1);
        expectedTeams.add(cTeam);

        Page<Team> expectedTeamsPage = new PageImpl<>(expectedTeams, PageRequest.of(0, 10),0);

        mockMvc.perform(get("/allTeams").param("search", "Test-land"))
                .andExpect(model().attribute("entries", expectedTeamsPage));
    }

}
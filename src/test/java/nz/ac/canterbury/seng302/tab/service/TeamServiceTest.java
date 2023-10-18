package nz.ac.canterbury.seng302.tab.service;


import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Transactional
@ExtendWith(MockitoExtension.class)
@WithMockUser("morgan.english@hotmail.com")
public class TeamServiceTest {

    TeamService teamService;

    @Mock
    TeamRepository teamRepository;

    @Mock
    SportService sportService;

    @Mock
    UserService userService;

    @Mock
    TeamMemberService teamMemberService;

    @BeforeEach
    void beforeEach() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("morgan.english@hotmail.com",null));
        MockitoAnnotations.openMocks(this);

        teamRepository = Mockito.mock(TeamRepository.class);
        sportService = Mockito.mock(SportService.class);
        userService = Mockito.mock(UserService.class);
        teamMemberService = Mockito.mock(TeamMemberService.class);
        teamService = new TeamService(teamRepository,sportService,userService,teamMemberService);
    }

    @Test
    void findingTeam_byEmptyCityListAndEmptySportList_validOutput() {
        List<String> cities = new ArrayList<>();
        List<Long> sports = new ArrayList<>();
        teamService.getTeamsFiltered(cities, sports);
        Mockito.verify(teamRepository).findAll();
    }

    @Test
    void findingTeam_bySingleCity_validOutput() {
        List<String> cities = new ArrayList<>();
        cities.add("Portland".toUpperCase());
        List<Long> sports = new ArrayList<>();
        teamService.getTeamsFiltered(cities, sports);
        Mockito.verify(teamRepository).findByCity(cities);
    }

    @Test
    void findingTeam_bySingleSport_validOutput() {
        List<String> cities = new ArrayList<>();
        List<Long> sports = new ArrayList<>();
        sports.add(0L);
        teamService.getTeamsFiltered(cities, sports);
        Mockito.verify(teamRepository).findBySport(sports);
    }


    @Test
    void findingTeam_byMultipleCitiesAndKeyword_validOutput() {
        String keyword = "test";
        List<String> cities = new ArrayList<>();
        List<Long> sports = new ArrayList<>();
        cities.add("Portland".toUpperCase());
        cities.add("Miami".toUpperCase());
        teamService.getTeamsFilteredSearch(keyword, cities, sports);
        Mockito.verify(teamRepository).findByCityAndKeyword(cities, keyword.toUpperCase());
    }

    @Test
    void findingTeam_byMultipleCitiesAndMultipleSportsAndKeyword_validOutput() {
        String keyword = "test";
        List<String> cities = new ArrayList<>();
        List<Long> sports = new ArrayList<>();

        cities.add("Portland".toUpperCase());
        cities.add("Miami".toUpperCase());
        sports.add(0L);
        sports.add(1L);

        teamService.getTeamsFilteredSearch(keyword, cities, sports);
        Mockito.verify(teamRepository).findBySportAndCityAndKeyword(sports, cities, keyword.toUpperCase());
    }


    @Test
    void getFreeTeams_test() {
        List<Team> list = Mockito.mock(List.class);
        Mockito.when(teamRepository.findFreeTeams()).thenReturn(list);
        Assertions.assertEquals(list,teamService.getFreeTeams());
        Mockito.verify(teamRepository,Mockito.times(1)).findFreeTeams();
    }

    @Test
    void getFreeTeamsThatUserManagesOrCoachesFromUserId_test() {
        Long id = 1L;
        List<Team> teams = Mockito.mock(List.class);

        final int[] roles = {Role.MANAGER.ordinal(), Role.COACH.ordinal()};

        Mockito.when(teamRepository.getFreeTeamNamesWhoUserManageOrCoach(id,roles)).thenReturn(teams);

        Assertions.assertEquals(teams,teamService.getFreeTeamsThatUserManagesOrCoachesFromUserId(id));
        Mockito.verify(teamRepository,Mockito.times(1)).getFreeTeamNamesWhoUserManageOrCoach(id,roles);
    }

    @Test
    void getPercentOfUsersWhoShareTeam_noMembers_test() {
        Team team = Mockito.mock(Team.class);
        User user = Mockito.mock(User.class);

        Mockito.when(teamMemberService.getAllTeamMembersFromTeam(team)).thenReturn(new ArrayList<>());

        Assertions.assertEquals(0,teamService.getPercentOfUsersWhoShareTeam(user,team));
    }


    @Test
    void getPercentOfUsersWhoShareTeam_hasMembers_test() {
        Team team = Mockito.mock(Team.class);
        User user = Mockito.mock(User.class);

        List list = Mockito.mock(List.class);
        Mockito.when(list.size()).thenReturn(10);
        Mockito.when(teamMemberService.getAllTeamMembersFromTeam(team)).thenReturn(list);

        Mockito.when(team.getId()).thenReturn(1L);
        Mockito.when(user.getId()).thenReturn(2L);
        Mockito.when(teamRepository.countUsersInTeamWhoAreInTeamWithUser(1L,2L)).thenReturn(3L);

        Assertions.assertEquals(0.3,teamService.getPercentOfUsersWhoShareTeam(user,team),0.001);
    }




    @Test
    void getRecommendedTeam_lastThreeBest() {
        User user = Mockito.mock(User.class);
        Team team1 = Mockito.mock(Team.class);
        Mockito.when(team1.getSuggestionMetric(user,teamService)).thenReturn(1.0F);
        Team team2 = Mockito.mock(Team.class);
        Mockito.when(team2.getSuggestionMetric(user,teamService)).thenReturn(2.0F);
        Team team3 = Mockito.mock(Team.class);
        Mockito.when(team3.getSuggestionMetric(user,teamService)).thenReturn(8.0F);

        Team team4 = Mockito.mock(Team.class);
        Mockito.when(team4.getSuggestionMetric(user,teamService)).thenReturn(70.0F);
        Team team5 = Mockito.mock(Team.class);
        Mockito.when(team5.getSuggestionMetric(user,teamService)).thenReturn(50.0F);
        Team team6 = Mockito.mock(Team.class);
        Mockito.when(team6.getSuggestionMetric(user,teamService)).thenReturn(20.0F);


        ArrayList<Team> teams = new ArrayList<>();
        teams.add(team1);
        teams.add(team2);
        teams.add(team3);
        teams.add(team4);
        teams.add(team5);
        teams.add(team6);
        Mockito.when(teamRepository.findAll()).thenReturn(teams);

        Mockito.when(teamMemberService.getAllTeamMembersFromTeam(Mockito.any())).thenReturn(new ArrayList<>());

        List<Team> out = teamService.getRecommendedTeams(user);
        Assertions.assertEquals(3,out.size());
        Assertions.assertTrue(out.contains(team4),"at pos 0");
        Assertions.assertTrue(out.contains(team5),"at pos 1");
        Assertions.assertTrue(out.contains(team6),"at pos 2");
    }


    @Test
    void getRecommendedTeam_mixThreeBest() {
        User user = Mockito.mock(User.class);
        Team team1 = Mockito.mock(Team.class);
        Mockito.when(team1.getSuggestionMetric(user,teamService)).thenReturn(1.0F);
        Team team2 = Mockito.mock(Team.class);
        Mockito.when(team2.getSuggestionMetric(user,teamService)).thenReturn(200.0F);
        Team team3 = Mockito.mock(Team.class);
        Mockito.when(team3.getSuggestionMetric(user,teamService)).thenReturn(8.0F);

        Team team4 = Mockito.mock(Team.class);
        Mockito.when(team4.getSuggestionMetric(user,teamService)).thenReturn(70.0F);
        Team team5 = Mockito.mock(Team.class);
        Mockito.when(team5.getSuggestionMetric(user,teamService)).thenReturn(50.0F);
        Team team6 = Mockito.mock(Team.class);
        Mockito.when(team6.getSuggestionMetric(user,teamService)).thenReturn(20.0F);


        ArrayList<Team> teams = new ArrayList<>();
        teams.add(team1);
        teams.add(team2);
        teams.add(team3);
        teams.add(team4);
        teams.add(team5);
        teams.add(team6);
        Mockito.when(teamRepository.findAll()).thenReturn(teams);

        Mockito.when(teamMemberService.getAllTeamMembersFromTeam(Mockito.any())).thenReturn(new ArrayList<>());

        List<Team> out = teamService.getRecommendedTeams(user);
        Assertions.assertEquals(3,out.size());
        Assertions.assertTrue(out.contains(team2),"at pos 0");
        Assertions.assertTrue(out.contains(team4),"at pos 1");
        Assertions.assertTrue(out.contains(team5),"at pos 2");
    }


    @Test
    void getRecommendedTeam_hasTeamMember() {
        User user = Mockito.mock(User.class);
        Mockito.when(user.getId()).thenReturn(1L);
        Team team1 = Mockito.mock(Team.class);
        Mockito.when(team1.getSuggestionMetric(user,teamService)).thenReturn(1.0F);
        Team team2 = Mockito.mock(Team.class);
        Mockito.when(team2.getSuggestionMetric(user,teamService)).thenReturn(200.0F);
        Team team3 = Mockito.mock(Team.class);
        Mockito.when(team3.getSuggestionMetric(user,teamService)).thenReturn(8.0F);

        Team team4 = Mockito.mock(Team.class);
        Team team5 = Mockito.mock(Team.class);
        Mockito.when(team5.getSuggestionMetric(user,teamService)).thenReturn(50.0F);
        Team team6 = Mockito.mock(Team.class);
        Mockito.when(team6.getSuggestionMetric(user,teamService)).thenReturn(20.0F);


        ArrayList<Team> teams = new ArrayList<>();
        teams.add(team1);
        teams.add(team2);
        teams.add(team3);
        teams.add(team4);
        teams.add(team5);
        teams.add(team6);
        Mockito.when(teamRepository.findAll()).thenReturn(teams);

        ArrayList<TeamMember> teamMembers = new ArrayList<>();
        TeamMember teamMember = Mockito.mock(TeamMember.class);
        TeamMemberId teamMemberId = Mockito.mock(TeamMemberId.class);
        Mockito.when(teamMember.getTeamMemberId()).thenReturn(teamMemberId);
        User user1 = Mockito.mock(User.class);
        Mockito.when(teamMemberId.getUser()).thenReturn(user1);
        Mockito.when(user1.getId()).thenReturn(1L);

        teamMembers.add(teamMember);

        Mockito.when(teamMemberService.getAllTeamMembersFromTeam(team4)).thenReturn(teamMembers);

        Mockito.when(teamMemberService.getAllTeamMembersFromTeam(Mockito.argThat(t -> t != team4))).thenReturn(new ArrayList<>());

        List<Team> out = teamService.getRecommendedTeams(user);
        Assertions.assertEquals(3,out.size());
        Assertions.assertTrue(out.contains(team2),"at pos 0");
        Assertions.assertTrue(out.contains(team5),"at pos 1");
        Assertions.assertTrue(out.contains(team6),"at pos 2");
    }


    @Test
    void getRecommendedTeam_allHaveTeamMember() {
        User user = Mockito.mock(User.class);
        Mockito.when(user.getId()).thenReturn(1L);
        Team team1 = Mockito.mock(Team.class);
        Team team2 = Mockito.mock(Team.class);
        Team team3 = Mockito.mock(Team.class);

        Team team4 = Mockito.mock(Team.class);
        Team team5 = Mockito.mock(Team.class);
        Team team6 = Mockito.mock(Team.class);


        ArrayList<Team> teams = new ArrayList<>();
        teams.add(team1);
        teams.add(team2);
        teams.add(team3);
        teams.add(team4);
        teams.add(team5);
        teams.add(team6);
        Mockito.when(teamRepository.findAll()).thenReturn(teams);

        ArrayList<TeamMember> teamMembers = new ArrayList<>();
        TeamMember teamMember = Mockito.mock(TeamMember.class);
        TeamMemberId teamMemberId = Mockito.mock(TeamMemberId.class);
        Mockito.when(teamMember.getTeamMemberId()).thenReturn(teamMemberId);
        User user1 = Mockito.mock(User.class);
        Mockito.when(teamMemberId.getUser()).thenReturn(user1);
        Mockito.when(user1.getId()).thenReturn(1L);

        teamMembers.add(teamMember);

        Mockito.when(teamMemberService.getAllTeamMembersFromTeam(Mockito.any())).thenReturn(teamMembers);

        List<Team> out = teamService.getRecommendedTeams(user);
        Assertions.assertEquals(0,out.size());
    }
}

package nz.ac.canterbury.seng302.tab.repository;


import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.tab.entity.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class TeamRepositoryTest {
    @Resource
    private TeamRepository teamRepository;

    @Resource
    private LocationRepository locationRepository;

    @Resource UserRepository userRepository;

    @Resource TeamMemberRepository teamMemberRepository;

    @Resource SportRepository sportRepository;

    @Resource ClubRepository clubRepository;

    @Test
    void findById() {
        // given
        Location location = new Location("", "", "", "", "Memphis", "United States of America");
        locationRepository.save(location);

        Team team = new Team(
                "Grizzles",
                location,
                "Basketball"
        );
        teamRepository.save(team);
        // when
        Optional<Team> foundTeam = teamRepository.findById(team.getId());
        // then
        assertTrue(foundTeam.isPresent());
    }

//    @Test
//    void findAll() {
//        Location locationA = new Location("", "", "", "", "Portland", "United States of America");
//        Location locationB = new Location("", "", "", "", "Memphis", "United States of America");
//        locationRepository.save(locationA);
//        locationRepository.save(locationB);
//        Team team1 = new Team(
//                "TrailBlazers",
//                locationA,
//                "Basketball"
//        );
//        Team team2 = new Team(
//                "Heat",
//                locationB,
//                "Basketball"
//        );
//        teamRepository.save(team1);
//        teamRepository.save(team2);
//        assertEquals(14, teamRepository.findAll().size());
//    }

    @Test
    void findTeamNamesThatUserHasRoles_NoMemberShips() {
        Location locationA = new Location("", "", "", "", "Portland", "United States of America");
        Location locationB = new Location("", "", "", "", "Memphis", "United States of America");
        locationRepository.save(locationA);
        locationRepository.save(locationB);

        Team team1 = new Team(
                "TrailBlazers",
                locationA,
                "Basketball"
        );
        Team team2 = new Team(
                "Heat",
                locationB,
                "Basketball"
        );
        teamRepository.save(team1);
        teamRepository.save(team2);
        User user = new User("e", "n", "l", LocalDate.now(), locationA, "blob");
        userRepository.save(user);
        List<Team> teams = teamRepository.findTeamsThatUserHasRoles(user.getId(), new int[]{Role.COACH.ordinal(), Role.MANAGER.ordinal(), Role.MEMBER.ordinal()});
        Assertions.assertEquals(0,teams.size());
    }

    @Test
    void findTeamNamesThatUserHasRoles_HasMemberShip() {
        Location locationA = new Location("", "", "", "", "Portland", "United States of America");
        Location locationB = new Location("", "", "", "", "Memphis", "United States of America");
        locationRepository.save(locationA);
        locationRepository.save(locationB);

        Team team1 = new Team(
                "TrailBlazers",
                locationA,
                "Basketball"
        );
        Team team2 = new Team(
                "Heat",
                locationB,
                "Basketball"
        );
        teamRepository.save(team1);
        teamRepository.save(team2);
        User user = new User("e", "n", "l", LocalDate.now(), locationA, "blob");
        userRepository.save(user);
        TeamMember teamMember = new TeamMember(user,team1,Role.MEMBER);
        teamMemberRepository.save(teamMember);

        List<Team> teams = teamRepository.findTeamsThatUserHasRoles(user.getId(), new int[]{Role.COACH.ordinal(), Role.MANAGER.ordinal(), Role.MEMBER.ordinal()});
        Assertions.assertEquals(1,teams.size());
        Assertions.assertEquals(team1.getId(),teams.get(0).getId());
    }

    @Test
    void findTeamNamesThatUserHasRoles_HasMemberShipWrongType() {
        Location locationA = new Location("", "", "", "", "Portland", "United States of America");
        Location locationB = new Location("", "", "", "", "Memphis", "United States of America");
        locationRepository.save(locationA);
        locationRepository.save(locationB);

        Team team1 = new Team(
                "TrailBlazers",
                locationA,
                "Basketball"
        );
        Team team2 = new Team(
                "Heat",
                locationB,
                "Basketball"
        );
        teamRepository.save(team1);
        teamRepository.save(team2);
        User user = new User("e", "n", "l", LocalDate.now(), locationA, "blob");
        userRepository.save(user);
        TeamMember teamMember = new TeamMember(user,team1,Role.MEMBER);
        teamMemberRepository.save(teamMember);

        List<Team> teams = teamRepository.findTeamsThatUserHasRoles(user.getId(), new int[]{Role.COACH.ordinal(), Role.MANAGER.ordinal()});
        Assertions.assertEquals(0,teams.size());
    }

    @Test
    void findTeamNamesThatUserHasRoles_HasMemberShipRightTypes() {
        Location locationA = new Location("", "", "", "", "Portland", "United States of America");
        Location locationB = new Location("", "", "", "", "Memphis", "United States of America");
        locationRepository.save(locationA);
        locationRepository.save(locationB);

        Team team1 = new Team(
                "TrailBlazers",
                locationA,
                "Basketball"
        );
        Team team2 = new Team(
                "Heat",
                locationB,
                "Basketball"
        );
        teamRepository.save(team1);
        teamRepository.save(team2);
        User user = new User("e", "n", "l", LocalDate.now(), locationA, "blob");
        userRepository.save(user);
        TeamMember teamMember = new TeamMember(user,team1,Role.MANAGER);
        teamMemberRepository.save(teamMember);
        TeamMember teamMember2 = new TeamMember(user,team2,Role.COACH);
        teamMemberRepository.save(teamMember2);

        List<Team> teams = teamRepository.findTeamsThatUserHasRoles(user.getId(), new int[]{Role.COACH.ordinal(), Role.MANAGER.ordinal()});
        Assertions.assertEquals(2,teams.size());
        Assertions.assertTrue(Objects.equals(teams.get(0).getId(), team1.getId()) || Objects.equals(teams.get(1).getId(), team1.getId()));
        Assertions.assertTrue(Objects.equals(teams.get(0).getId(), team2.getId()) || Objects.equals(teams.get(1).getId(), team2.getId()));
    }

    @Test
    void findTeamNamesThatUserHasRolesTest_HasMemberShipWrongAndRight() {
        Location locationA = new Location("", "", "", "", "Portland", "United States of America");
        Location locationB = new Location("", "", "", "", "Memphis", "United States of America");
        locationRepository.save(locationA);
        locationRepository.save(locationB);

        Team team1 = new Team(
                "TrailBlazers",
                locationA,
                "Basketball"
        );
        Team team2 = new Team(
                "Heat",
                locationB,
                "Basketball"
        );
        teamRepository.save(team1);
        teamRepository.save(team2);
        User user = new User("e", "n", "l", LocalDate.now(), locationA, "blob");
        userRepository.save(user);
        TeamMember teamMember = new TeamMember(user,team1,Role.MEMBER);
        teamMemberRepository.save(teamMember);
        TeamMember teamMember2 = new TeamMember(user,team2,Role.COACH);
        teamMemberRepository.save(teamMember2);

        List<Team> teams = teamRepository.findTeamsThatUserHasRoles(user.getId(), new int[]{Role.COACH.ordinal(), Role.MANAGER.ordinal()});
        Assertions.assertEquals(1,teams.size());
        Assertions.assertEquals(team2.getId(),teams.get(0).getId());
    }

    @Test
    void findByKeyword_ValidOutput() {
        Location locationA = new Location("", "", "", "", "Portland", "United States of America");
        Location locationB = new Location("", "", "", "", "Memphis", "United States of America");
        locationRepository.save(locationA);
        locationRepository.save(locationB);

        Team team1 = new Team(
                "TrailBlazers",
                locationA,
                "Basketball"
        );
        Team team2 = new Team(
                "TestHeat",
                locationB,
                "Basketball"
        );
        teamRepository.save(team1);
        teamRepository.save(team2);
        List<Team> teams = teamRepository.findByKeyword("TestHeat".toUpperCase());
        Assertions.assertEquals(1,teams.size());
        Assertions.assertEquals(team2.getId(),teams.get(0).getId());
    }

    @Test
    void findByCity_ValidOutput(){
        List<String> cities = new ArrayList<>();
        Location locationA = new Location("", "", "", "", "Portland", "United States of America");
        Location locationB = new Location("", "", "", "", "Memphis", "United States of America");
        locationRepository.save(locationA);
        locationRepository.save(locationB);

        Team team1 = new Team(
                "TrailBlazers",
                locationA,
                "Basketball"
        );
        Team team2 = new Team(
                "Heat",
                locationB,
                "Basketball"
        );
        teamRepository.save(team1);
        teamRepository.save(team2);

        cities.add("Memphis".toUpperCase());

        List<Team> teams = teamRepository.findByCity(cities);
        Assertions.assertEquals(1,teams.size());
        Assertions.assertEquals(team2.getId(),teams.get(0).getId());
    }

    @Test
    void findByCitiesAndKeyword_ValidOutput(){
        List<String> cities = new ArrayList<>();
        List<Team> expectedTeams = new ArrayList<>();

        Location location1 = new Location("", "", "", "", "Portland", "United States of America");
        Location location2 = new Location("", "", "", "", "Miami", "United States of America");
        Location location3 = new Location("", "", "", "", "Chicago", "United States of America");

        locationRepository.save(location1);
        locationRepository.save(location2);
        locationRepository.save(location3);

        Team team1 = new Team(
                "Test",
                location1,
                "Basketball"
        );
        Team team2 = new Team(
                "Test",
                location2,
                "Basketball"
        );
        Team team3 = new Team(
                "Test",
                location3,
                "Basketball"
        );

        teamRepository.save(team1);
        teamRepository.save(team2);
        teamRepository.save(team3);

        expectedTeams.add(team2);
        expectedTeams.add(team1);

        cities.add("Portland".toUpperCase());
        cities.add("Miami".toUpperCase());
        assertEquals(expectedTeams, teamRepository.findByCityAndKeyword(cities, "Test".toUpperCase()));
    }

    @Test
    void findBySport_ValidOutput() {
        List<Long> sports = new ArrayList<>();

        Location location1 = new Location("", "", "", "", "Memphis", "United States of America");
        locationRepository.save(location1);

        Team team1 = new Team(
                "Dolphins",
                location1,
                "AmericanFootball"
        );
        Team team2 = new Team(
                "Heat",
                location1,
                "Basketball"
        );

        teamRepository.save(team1);
        teamRepository.save(team2);
        sports.add(sportRepository.findBySportName("Basketball").getId());
        assertTrue(teamRepository.findBySport(sports).contains(team2));
    }

    @Test
    void findBySportsAndKeyword_ValidOutput(){
        List<Long> sports = new ArrayList<>();

        Location location1 = new Location("", "", "", "", "Portland", "United States of America");
        Location location2 = new Location("", "", "", "", "Miami", "United States of America");
        Location location3 = new Location("", "", "", "", "Chicago", "United States of America");

        locationRepository.save(location1);
        locationRepository.save(location2);
        locationRepository.save(location3);

        Team team1 = new Team(
                "Test",
                location1,
                "Basketball"
        );
        Team team2 = new Team(
                "Test",
                location2,
                "Basketball"
        );
        Team team3 = new Team(
                "Test",
                location3,
                "Football"
        );

        teamRepository.save(team1);
        teamRepository.save(team2);
        teamRepository.save(team3);

        sports.add(sportRepository.findBySportName("Basketball").getId());
        assertTrue(teamRepository.findBySportAndKeyword(sports, "Test".toUpperCase()).contains(team1));
        assertTrue(teamRepository.findBySportAndKeyword(sports, "Test".toUpperCase()).contains(team2));
    }

//    @Test
//    void findBySportsAndCities_ValidOutput() {
//        List<String> cities = new ArrayList<>();
//        List<Long> sports = new ArrayList<>();
//        List<Team> expectedTeams = new ArrayList<>();
//
//        Location location1 = new Location("", "", "", "", "Portland", "United States of America");
//        Location location2 = new Location("", "", "", "", "Miami", "United States of America");
//        Location location3 = new Location("", "", "", "", "Chicago", "United States of America");
//
//        locationRepository.save(location1);
//        locationRepository.save(location2);
//        locationRepository.save(location3);
//
//        Team team1 = new Team(
//                "Test",
//                location1,
//                "Basketball"
//        );
//        Team team2 = new Team(
//                "Test",
//                location2,
//                "Baseball"
//        );
//        Team team3 = new Team(
//                "Test",
//                location3,
//                "Basketball"
//        );
//        Team team4 = new Team(
//                "Dolphins",
//                location2,
//                "AmericanFootball"
//        );
//        Team team5 = new Team(
//                "Test",
//                location3,
//                "Baseball"
//        );
//
//
//        teamRepository.save(team1);
//        teamRepository.save(team2);
//        teamRepository.save(team3);
//        teamRepository.save(team4);
//        teamRepository.save(team5);
//
//        expectedTeams.add(team2);
//        expectedTeams.add(team1);
//
//        cities.add("Portland".toUpperCase());
//        cities.add("Miami".toUpperCase());
//        sports.add(sportRepository.findBySportName("Baseball").getId());
//        sports.add(sportRepository.findBySportName("Basketball").getId());
//
//        assertEquals(expectedTeams, teamRepository.findBySportAndCity(sports, cities));
//    }

    @Test
    void findBySportsAndCitiesAndKeyword_ValidOutput() {
        List<String> cities = new ArrayList<>();
        List<Long> sports = new ArrayList<>();
        List<Team> expectedTeams = new ArrayList<>();

        Location location1 = new Location("", "", "", "", "Portland", "United States of America");
        Location location2 = new Location("", "", "", "", "Miami", "United States of America");
        Location location3 = new Location("", "", "", "", "Chicago", "United States of America");

        locationRepository.save(location1);
        locationRepository.save(location2);
        locationRepository.save(location3);

        Team team1 = new Team(
                "Test",
                location1,
                "Basketball"
        );
        Team team2 = new Team(
                "Test",
                location2,
                "Baseball"
        );
        Team team3 = new Team(
                "Test",
                location3,
                "Basketball"
        );
        Team team4 = new Team(
                "Dolphins",
                location2,
                "AmericanFootball"
        );
        Team team5 = new Team(
                "Test",
                location3,
                "Baseball"
        );


        teamRepository.save(team1);
        teamRepository.save(team2);
        teamRepository.save(team3);
        teamRepository.save(team4);
        teamRepository.save(team5);

        expectedTeams.add(team2);
        expectedTeams.add(team1);

        cities.add("Portland".toUpperCase());
        cities.add("Miami".toUpperCase());
        sports.add(sportRepository.findBySportName("Baseball").getId());
        sports.add(sportRepository.findBySportName("Basketball").getId());

        assertEquals(expectedTeams, teamRepository.findBySportAndCityAndKeyword(sports, cities, "Test".toUpperCase()));
    }


    @Test
    void findFreeTeams() {
        List<Team> out = teamRepository.findFreeTeams();
        Assertions.assertEquals(7,out.size(),out.get(0).getName());
        Assertions.assertEquals(2,out.get(0).getId());
        Assertions.assertEquals(11,out.get(1).getId());
        Assertions.assertEquals(12,out.get(2).getId());
        Assertions.assertEquals(13,out.get(3).getId());
        Assertions.assertEquals(14,out.get(4).getId());
        Assertions.assertEquals(15,out.get(5).getId());
        Assertions.assertEquals(16,out.get(6).getId());
    }


    @Test
    void getFreeTeamNamesWhoUserManageOrCoach_manger_find() {
        Location location1 = new Location("", "", "", "", "Portland", "United States of America");

        locationRepository.save(location1);

        Team team1 = new Team(
                "Test",
                location1,
                "Basketball"
        );

        Team team2 = new Team(
                "Test2",
                location1,
                "Basketball"
        );
        teamRepository.save(team1);
        teamRepository.save(team2);

        User user = new User("e", "n", "l", LocalDate.now(), location1, "blob");
        userRepository.save(user);

        ArrayList<Team> teams = new ArrayList<>();
        teams.add(team2);
        Club club = new Club(user,"n",location1,teams);
        clubRepository.save(club);
        TeamMember teamMember = new TeamMember(user,team1,Role.MANAGER);
        teamMemberRepository.save(teamMember);
        TeamMember teamMember2 = new TeamMember(user,team2,Role.MANAGER);
        teamMemberRepository.save(teamMember2);

        final int[] roles = {Role.MANAGER.ordinal()};
        List<Team> out = teamRepository.getFreeTeamNamesWhoUserManageOrCoach(user.getId(),roles);
        Assertions.assertEquals(1,out.size());
        Assertions.assertEquals(team1.getId(),out.get(0).getId());
    }

    @Test
    void getFreeTeamNamesWhoUserManageOrCoach_manger_none() {
        Location location1 = new Location("", "", "", "", "Portland", "United States of America");

        locationRepository.save(location1);

        Team team1 = new Team(
                "Test",
                location1,
                "Basketball"
        );

        Team team2 = new Team(
                "Test2",
                location1,
                "Basketball"
        );
        teamRepository.save(team1);
        teamRepository.save(team2);

        User user = new User("e", "n", "l", LocalDate.now(), location1, "blob");
        userRepository.save(user);

        ArrayList<Team> teams = new ArrayList<>();
        teams.add(team2);
        Club club = new Club(user,"n",location1,teams);
        clubRepository.save(club);
        TeamMember teamMember = new TeamMember(user,team1,Role.MEMBER);
        teamMemberRepository.save(teamMember);
        TeamMember teamMember2 = new TeamMember(user,team2,Role.MEMBER);
        teamMemberRepository.save(teamMember2);

        final int[] roles = {Role.MANAGER.ordinal()};
        List<Team> out = teamRepository.getFreeTeamNamesWhoUserManageOrCoach(user.getId(),roles);
        Assertions.assertEquals(0,out.size());
    }

    @Test
    void getFreeTeamNamesWhoUserManageOrCoach_coach_find() {
        Location location1 = new Location("", "", "", "", "Portland", "United States of America");

        locationRepository.save(location1);

        Team team1 = new Team(
                "Test",
                location1,
                "Basketball"
        );

        Team team2 = new Team(
                "Test2",
                location1,
                "Basketball"
        );
        teamRepository.save(team1);
        teamRepository.save(team2);

        User user = new User("e", "n", "l", LocalDate.now(), location1, "blob");
        userRepository.save(user);

        ArrayList<Team> teams = new ArrayList<>();
        teams.add(team2);
        Club club = new Club(user,"n",location1,teams);
        clubRepository.save(club);
        TeamMember teamMember = new TeamMember(user,team1,Role.COACH);
        teamMemberRepository.save(teamMember);
        TeamMember teamMember2 = new TeamMember(user,team2,Role.COACH);
        teamMemberRepository.save(teamMember2);

        final int[] roles = {Role.COACH.ordinal()};
        List<Team> out = teamRepository.getFreeTeamNamesWhoUserManageOrCoach(user.getId(),roles);
        Assertions.assertEquals(1,out.size());
        Assertions.assertEquals(team1.getId(),out.get(0).getId());
    }

    @Test
    void getFreeTeamNamesWhoUserManageOrCoach_coach_none() {
        Location location1 = new Location("", "", "", "", "Portland", "United States of America");

        locationRepository.save(location1);

        Team team1 = new Team(
                "Test",
                location1,
                "Basketball"
        );

        Team team2 = new Team(
                "Test2",
                location1,
                "Basketball"
        );
        teamRepository.save(team1);
        teamRepository.save(team2);

        User user = new User("e", "n", "l", LocalDate.now(), location1, "blob");
        userRepository.save(user);

        ArrayList<Team> teams = new ArrayList<>();
        teams.add(team2);
        Club club = new Club(user,"n",location1,teams);
        clubRepository.save(club);
        TeamMember teamMember = new TeamMember(user,team1,Role.MEMBER);
        teamMemberRepository.save(teamMember);
        TeamMember teamMember2 = new TeamMember(user,team2,Role.MEMBER);
        teamMemberRepository.save(teamMember2);

        final int[] roles = {Role.COACH.ordinal()};
        List<Team> out = teamRepository.getFreeTeamNamesWhoUserManageOrCoach(user.getId(),roles);
        Assertions.assertEquals(0,out.size());
    }


    @Test
    public void countUsersInTeamWhoAreInTeamWithUser_test() {
        Assertions.assertEquals(4,teamRepository.countUsersInTeamWhoAreInTeamWithUser(2,1));
    }

}

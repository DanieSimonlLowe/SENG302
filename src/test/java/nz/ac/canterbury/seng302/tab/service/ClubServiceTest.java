package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.entity.Club;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.ClubRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClubServiceTest {

    ClubService clubService;
    ClubRepository clubRepository;
    UserRepository userRepository;

    UserService userService;

    Club club;
    Team team;
    @BeforeEach
    public void setup() {
        clubRepository = Mockito.mock(ClubRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        userService = Mockito.mock(UserService.class);
        clubService = new ClubService(clubRepository, userRepository, userService);
        club = Mockito.mock(Club.class);
        team = Mockito.mock(Team.class);
    }

    @Test
    public void save_test() {
        clubService.save(club);
        Mockito.verify(clubRepository,Mockito.times(1)).save(club);
    }

    @Test
    void checkTeamInClub_test() {
        clubService.checkIfTeamAlreadyInClub(1L, 1L);
        Mockito.verify(clubRepository,Mockito.times(1)).findTeamClubByTeamId(1L,  1L);
    }

    @Test
    void checkTeamInClubNoCLubId_test() {
        clubService.checkIfTeamAlreadyInClubNoClubId(1L);
        Mockito.verify(clubRepository,Mockito.times(1)).findTeamClubNoClubIdByTeamId(1L);
    }

    @Test
    void checkClubCanAddTeam_test() {
        clubService.checkIfCanAddTeam(1L, team);
        Mockito.verify(clubRepository,Mockito.times(1)).findClubTeamSport(1L);
    }

    @Test
    void managerCheck_true() {

        Integer[] roles = {Role.COACH.ordinal(), Role.MANAGER.ordinal()};
        Mockito.when(clubRepository.findIfTeamMemberHasRole(roles,2L,3L)).thenReturn(Optional.of(1L));

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn("email");
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);

        User user = Mockito.mock(User.class);
        Mockito.when(user.getId()).thenReturn(2L);

        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);

        Assertions.assertTrue(clubService.managerCheck(3L));
        Mockito.verify(clubRepository,Mockito.times(1)).findIfTeamMemberHasRole(roles,2L,3L);
    }

    @Test
    void managerCheck_false() {

        Integer[] roles = {Role.COACH.ordinal(), Role.MANAGER.ordinal()};
        Mockito.when(clubRepository.findIfTeamMemberHasRole(roles,2L,3L)).thenReturn(Optional.empty());

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn("email");
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);

        User user = Mockito.mock(User.class);
        Mockito.when(user.getId()).thenReturn(2L);

        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);

        Assertions.assertFalse(clubService.managerCheck(3L));
        Mockito.verify(clubRepository,Mockito.times(1)).findIfTeamMemberHasRole(roles,2L,3L);
    }

    @Test
    void checkGetClubByManager_test() {
        clubService.getClubByManager(1L);
        Mockito.verify(clubRepository,Mockito.times(1)).findClubsByUserId(1L);
    }


    @Test
    void getClubsFiltered_noSport_noCity() {
        List cities = Mockito.mock(List.class);
        List sports = Mockito.mock(List.class);
        List out = Mockito.mock(List.class);

        Mockito.when(cities.isEmpty()).thenReturn(true);
        Mockito.when(sports.isEmpty()).thenReturn(true);

        Mockito.when(clubRepository.findAll()).thenReturn(out);

        Assertions.assertSame(out,clubService.getClubsFiltered(cities,sports));
    }

    @Test
    void getClubsFiltered_noSport_hasCity() {
        List cities = Mockito.mock(List.class);
        List sports = Mockito.mock(List.class);
        List out = Mockito.mock(List.class);

        Mockito.when(cities.isEmpty()).thenReturn(false);
        Mockito.when(sports.isEmpty()).thenReturn(true);

        Mockito.when(clubRepository.findByCity(cities)).thenReturn(out);

        Assertions.assertSame(out,clubService.getClubsFiltered(cities,sports));
    }


    @Test
    void getClubsFiltered_hasSport_noCity() {
        List cities = Mockito.mock(List.class);
        List sports = Mockito.mock(List.class);
        List out = Mockito.mock(List.class);

        Mockito.when(cities.isEmpty()).thenReturn(true);
        Mockito.when(sports.isEmpty()).thenReturn(false);

        Mockito.when(clubRepository.findBySport(sports)).thenReturn(out);

        Assertions.assertSame(out,clubService.getClubsFiltered(cities,sports));
    }


    @Test
    void getClubsFiltered_hasSport_hasCity() {
        List cities = Mockito.mock(List.class);
        List sports = Mockito.mock(List.class);
        List out = Mockito.mock(List.class);

        Mockito.when(cities.isEmpty()).thenReturn(false);
        Mockito.when(sports.isEmpty()).thenReturn(false);

        Mockito.when(clubRepository.findBySportAndCity(sports,cities)).thenReturn(out);

        Assertions.assertSame(out,clubService.getClubsFiltered(cities,sports));
    }


    @Test
    void getClubsFilteredSearch_noSport_noCity() {
        List cities = Mockito.mock(List.class);
        List sports = Mockito.mock(List.class);
        List out = Mockito.mock(List.class);
        String str = "test";

        Mockito.when(cities.isEmpty()).thenReturn(true);
        Mockito.when(sports.isEmpty()).thenReturn(true);

        Mockito.when(clubRepository.findByKeyword(str.toUpperCase())).thenReturn(out);

        Assertions.assertSame(out,clubService.getClubsFilteredSearch(str,cities,sports));
    }

    @Test
    void getClubsFilteredSearch_noSport_hasCity() {
        List cities = Mockito.mock(List.class);
        List sports = Mockito.mock(List.class);
        List out = Mockito.mock(List.class);
        String str = "test";

        Mockito.when(cities.isEmpty()).thenReturn(false);
        Mockito.when(sports.isEmpty()).thenReturn(true);

        Mockito.when(clubRepository.findByCityAndKeyword(cities,str.toUpperCase())).thenReturn(out);

        Assertions.assertSame(out,clubService.getClubsFilteredSearch(str,cities,sports));
    }

    @Test
    void getClubsFilteredSearch_hasSport_noCity() {
        List cities = Mockito.mock(List.class);
        List sports = Mockito.mock(List.class);
        List out = Mockito.mock(List.class);
        String str = "test";

        Mockito.when(cities.isEmpty()).thenReturn(true);
        Mockito.when(sports.isEmpty()).thenReturn(false);

        Mockito.when(clubRepository.findBySportAndKeyword(sports,str.toUpperCase())).thenReturn(out);

        Assertions.assertSame(out,clubService.getClubsFilteredSearch(str,cities,sports));
    }

    @Test
    void getClubsFilteredSearch_hasSport_hasCity() {
        List cities = Mockito.mock(List.class);
        List sports = Mockito.mock(List.class);
        List out = Mockito.mock(List.class);
        String str = "test";

        Mockito.when(cities.isEmpty()).thenReturn(false);
        Mockito.when(sports.isEmpty()).thenReturn(false);

        Mockito.when(clubRepository.findBySportAndCityAndKeyword(sports,cities,str.toUpperCase())).thenReturn(out);

        Assertions.assertSame(out,clubService.getClubsFilteredSearch(str,cities,sports));
    }

    @Test
    void findByKeyword() {
        String str = "test";
        List out = Mockito.mock(List.class);
        Mockito.when(clubRepository.findByKeyword(str.toUpperCase())).thenReturn(out);

        Assertions.assertEquals(out,clubService.getByKeyword(str));
    }

    @Test
    void getCitiesFromClubs() {
        List<Club> list = new ArrayList<>();
        list.add(Mockito.mock(Club.class));
        list.add(Mockito.mock(Club.class));
        Mockito.when(list.get(0).getId()).thenReturn(0l);
        Mockito.when(list.get(1).getId()).thenReturn(1l);

        List<Long> expectedList = new ArrayList<>();
        expectedList.add(0L);
        expectedList.add(1L);

        List out = Mockito.mock(List.class);

        Mockito.when(clubRepository.findDistinctCityFromClubs(expectedList)).thenReturn(out);

        Assertions.assertEquals(out,clubService.getCitiesFromClubs(list));
    }

    @Test
    void getAllUsersFromClub() {
        Club club = Mockito.mock(Club.class);
        Mockito.when(club.getId()).thenReturn(0L);
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

        Mockito.when(clubRepository.findUsersInAClub(club.getId())).thenReturn(ids);
        Mockito.when(userRepository.findAllById(ids)).thenReturn(users);
        Assertions.assertEquals(users, clubService.getAllUsersFromClub(club.getId()));
    }

    @Test
    void getClubsFromUser_test() {
        User user = Mockito.mock(User.class);
        Mockito.when(user.getId()).thenReturn(0L);

        clubService.getAllClubsFromUser(user);
        Mockito.verify(clubRepository,Mockito.times(1)).findAllClubsByUser(user.getId());
    }
}

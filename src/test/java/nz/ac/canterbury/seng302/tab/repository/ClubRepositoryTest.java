package nz.ac.canterbury.seng302.tab.repository;

import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.tab.entity.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Transactional
public class ClubRepositoryTest {

    @Resource
    ClubRepository clubRepository;

    @Resource
    TeamRepository teamRepository;

    @Resource
    TeamMemberRepository teamMemberRepository;

    @Resource
    LocationRepository locationRepository;

    @Resource
    UserRepository userRepository;

    @Resource
    SportRepository sportRepository;

    Location location;

    Team team1;
    Team team2;
    Team team3;

    User user;

    Club club;

    Sport sport;

    @BeforeEach
    public void setup() {
        location = new Location("", "", "", "", "Memphis", "United States of America");
        locationRepository.save(location);

        sport = new Sport("sport");
        sportRepository.save(sport);

        team1 = new Team(
                "A1",
                location,
                "sport"
        );
        teamRepository.save(team1);

        team2 = new Team(
                "A2",
                location,
                "sport"
        );
        teamRepository.save(team2);

        team3 = new Team(
                "A3",
                location,
                "sport"
        );

        teamRepository.save(team3);

        user = new User("","","", LocalDate.now(),location,"");
        userRepository.save(user);

        teamMemberRepository.save(new TeamMember(user, team1, Role.COACH));

        ArrayList<Team> teams = new ArrayList<>();
        teams.add(team1);
        teams.add(team2);
        teams.add(team3);

        club = new Club(user,"name",location,teams);



    }

    @Test
    public void findByKeyword_correct_name() {
        clubRepository.save(club);
        List<Club> clubs = clubRepository.findByKeyword("NAME");
        Assertions.assertEquals(1,clubs.size());
        Assertions.assertEquals(club,clubs.get(0));
    }

    @Test
    public void findByKeyword_correct_location() {
        clubRepository.save(club);
        List<Club> clubs = clubRepository.findByKeyword("MEMP");
        Assertions.assertEquals(1,clubs.size());
        Assertions.assertEquals(club,clubs.get(0));
    }

    @Test
    public void findByKeyword_no_match() {
        clubRepository.save(club);
        List<Club> clubs = clubRepository.findByKeyword("MEMPDEP");
        Assertions.assertEquals(0,clubs.size());
    }

    @Test
    public void findByCity_no_match() {
        ArrayList<String> strings = new ArrayList<>();
        strings.add("WRONG");
        strings.add("MEMPHISS");
        clubRepository.save(club);
        List<Club> clubs = clubRepository.findByCity(strings);
        Assertions.assertEquals(0,clubs.size());
    }

    @Test
    public void findByCity_match() {
        ArrayList<String> strings = new ArrayList<>();
        strings.add("WRONG");
        strings.add("MEMPHIS");
        clubRepository.save(club);
        List<Club> clubs = clubRepository.findByCity(strings);
        Assertions.assertEquals(1,clubs.size());
        Assertions.assertEquals(club,clubs.get(0));
    }

    @Test
    public void findByCityAndKeyword_no_match() {
        ArrayList<String> strings = new ArrayList<>();
        strings.add("WRONG");
        strings.add("MEMPHISS");
        clubRepository.save(club);
        List<Club> clubs = clubRepository.findByCityAndKeyword(strings, "WRONG");
        Assertions.assertEquals(0,clubs.size());
    }

    @Test
    public void findByCityAndKeyword_city() {
        ArrayList<String> strings = new ArrayList<>();
        strings.add("WRONG");
        strings.add("MEMPHIS");
        clubRepository.save(club);
        List<Club> clubs = clubRepository.findByCityAndKeyword(strings, "WRONG");
        Assertions.assertEquals(0,clubs.size());
    }

    @Test
    public void findByCityAndKeyword_name() {
        ArrayList<String> strings = new ArrayList<>();
        strings.add("WRONG");
        strings.add("MEMPHISS");
        clubRepository.save(club);
        List<Club> clubs = clubRepository.findByCityAndKeyword(strings, "NAME");
        Assertions.assertEquals(0,clubs.size());
    }

    @Test
    public void findByCityAndKeyword_location() {
        ArrayList<String> strings = new ArrayList<>();
        strings.add("WRONG");
        strings.add("MEMPHISS");
        clubRepository.save(club);
        List<Club> clubs = clubRepository.findByCityAndKeyword(strings, "MEMP");
        Assertions.assertEquals(0,clubs.size());
    }

    @Test
    public void findDistinctCityFromClubs() {
        Club club1 = new Club(user,"name2",location,new ArrayList<>());
        clubRepository.save(club1);
        clubRepository.save(club);

        List<Long> ids = new ArrayList<>();
        //ids.add(club1.getId());
        ids.add(club.getId());

        List<String> list = clubRepository.findDistinctCityFromClubs(ids);
        Assertions.assertEquals(1,list.size(),list.toString());
        Assertions.assertEquals("MEMPHIS",list.get(0));
    }

    @Test
    public void findBySport_noSport() {
        clubRepository.save(club);

        ArrayList<Long> ids = new ArrayList<>();
        ids.add(-1L);
        List<Club> clubs = clubRepository.findBySport(ids);

        Assertions.assertEquals(0,clubs.size());
    }

    @Test
    public void findBySport_hasSport() {
        clubRepository.save(club);

        ArrayList<Long> ids = new ArrayList<>();
        ids.add(sport.getId());
        List<Club> clubs = clubRepository.findBySport(ids);

        Assertions.assertEquals(1,clubs.size());
        Assertions.assertEquals(club,clubs.get(0));
    }


    @Test
    public void findBySportAndKeyword_noSport_noKeyword() {
        clubRepository.save(club);

        ArrayList<Long> ids = new ArrayList<>();
        ids.add(-1L);
        List<Club> clubs = clubRepository.findBySportAndKeyword(ids,"OTHER");

        Assertions.assertEquals(0,clubs.size());
    }

    @Test
    public void findBySportAndKeyword_noSport_hasKeyword() {
        clubRepository.save(club);

        ArrayList<Long> ids = new ArrayList<>();
        ids.add(-1L);
        List<Club> clubs = clubRepository.findBySportAndKeyword(ids,"MEMPHIS");

        Assertions.assertEquals(0,clubs.size());
    }

    @Test
    public void findBySportAndKeyword_hasSport_noKeyword() {
        clubRepository.save(club);

        ArrayList<Long> ids = new ArrayList<>();
        ids.add(sport.getId());
        List<Club> clubs = clubRepository.findBySportAndKeyword(ids,"OTHER");

        Assertions.assertEquals(0,clubs.size());
    }

    @Test
    public void findBySportAndKeyword_hasSport_hasKeyword() {
        clubRepository.save(club);

        ArrayList<Long> ids = new ArrayList<>();
        ids.add(sport.getId());
        List<Club> clubs = clubRepository.findBySportAndKeyword(ids,"MEMPHIS");

        Assertions.assertEquals(1,clubs.size());
        Assertions.assertEquals(club,clubs.get(0));
    }


    @Test
    public void findBySportAndCity_noSport_noCity() {
        clubRepository.save(club);

        ArrayList<Long> ids = new ArrayList<>();
        ids.add(-1L);
        ArrayList<String> strings = new ArrayList<>();
        strings.add("WRONG");
        strings.add("MEMPHISS");
        List<Club> clubs = clubRepository.findBySportAndCity(ids,strings);

        Assertions.assertEquals(0,clubs.size());
    }

    @Test
    public void findBySportAndCity_hasSport_noCity() {
        clubRepository.save(club);

        ArrayList<Long> ids = new ArrayList<>();
        ids.add(sport.getId());
        ArrayList<String> strings = new ArrayList<>();
        strings.add("WRONG");
        strings.add("MEMPHISS");
        List<Club> clubs = clubRepository.findBySportAndCity(ids,strings);

        Assertions.assertEquals(0,clubs.size());
    }

    @Test
    public void findBySportAndCity_noSport_hasCity() {
        clubRepository.save(club);

        ArrayList<Long> ids = new ArrayList<>();
        ids.add(-1L);
        ArrayList<String> strings = new ArrayList<>();
        strings.add("WRONG");
        strings.add("MEMPHIS");
        List<Club> clubs = clubRepository.findBySportAndCity(ids,strings);

        Assertions.assertEquals(0,clubs.size());
    }

    @Test
    public void findBySportAndCity_hasSport_hasCity() {
        clubRepository.save(club);

        ArrayList<Long> ids = new ArrayList<>();
        ids.add(sport.getId());
        ArrayList<String> strings = new ArrayList<>();
        strings.add("WRONG");
        strings.add("MEMPHIS");
        List<Club> clubs = clubRepository.findBySportAndCity(ids,strings);

        Assertions.assertEquals(1,clubs.size());
        Assertions.assertEquals(club,clubs.get(0));
    }


    @Test
    public void findBySportAndCityAndKeyword_noSport_noCity_noKeyword() {
        clubRepository.save(club);

        ArrayList<Long> ids = new ArrayList<>();
        ids.add(-1L);
        ArrayList<String> strings = new ArrayList<>();
        strings.add("WRONG");
        strings.add("MEMPHISS");
        List<Club> clubs = clubRepository.findBySportAndCityAndKeyword(ids,strings,"OTHER");

        Assertions.assertEquals(0,clubs.size());
    }

    @Test
    public void findBySportAndCityAndKeyword_hasSport_noCity_noKeyword() {
        clubRepository.save(club);

        ArrayList<Long> ids = new ArrayList<>();
        ids.add(sport.getId());
        ArrayList<String> strings = new ArrayList<>();
        strings.add("WRONG");
        strings.add("MEMPHISS");
        List<Club> clubs = clubRepository.findBySportAndCityAndKeyword(ids,strings,"OTHER");

        Assertions.assertEquals(0,clubs.size());
    }

    @Test
    public void findBySportAndCityAndKeyword_hasSport_hasCity_noKeyword() {
        clubRepository.save(club);

        ArrayList<Long> ids = new ArrayList<>();
        ids.add(sport.getId());
        ArrayList<String> strings = new ArrayList<>();
        strings.add("WRONG");
        strings.add("MEMPHIS");
        List<Club> clubs = clubRepository.findBySportAndCityAndKeyword(ids,strings,"OTHER");

        Assertions.assertEquals(0,clubs.size());
    }

    @Test
    public void findBySportAndCityAndKeyword_noSport_hasCity_noKeyword() {
        clubRepository.save(club);

        ArrayList<Long> ids = new ArrayList<>();
        ids.add(sport.getId());
        ArrayList<String> strings = new ArrayList<>();
        strings.add("WRONG");
        strings.add("MEMPHIS");
        List<Club> clubs = clubRepository.findBySportAndCityAndKeyword(ids,strings,"OTHER");

        Assertions.assertEquals(0,clubs.size());
    }

    @Test
    public void findBySportAndCityAndKeyword_noSport_hasCity_hasKeyword() {
        clubRepository.save(club);

        ArrayList<Long> ids = new ArrayList<>();
        ids.add(-1L);
        ArrayList<String> strings = new ArrayList<>();
        strings.add("WRONG");
        strings.add("MEMPHIS");
        List<Club> clubs = clubRepository.findBySportAndCityAndKeyword(ids,strings,"MEMPHIS");

        Assertions.assertEquals(0,clubs.size());
    }

    @Test
    public void findBySportAndCityAndKeyword_hasSport_noCity_hasKeyword() {
        clubRepository.save(club);

        ArrayList<Long> ids = new ArrayList<>();
        ids.add(-1L);
        ArrayList<String> strings = new ArrayList<>();
        strings.add("SPORT");
        strings.add("MEMPHISS");
        List<Club> clubs = clubRepository.findBySportAndCityAndKeyword(ids,strings,"MEMPHIS");

        Assertions.assertEquals(0,clubs.size());
    }

    @Test
    public void findBySportAndCityAndKeyword_noSport_noCity_hasKeyword() {
        clubRepository.save(club);

        ArrayList<Long> ids = new ArrayList<>();
        ids.add(-1L);
        ArrayList<String> strings = new ArrayList<>();
        strings.add("WRONG");
        strings.add("MEMPHISS");
        List<Club> clubs = clubRepository.findBySportAndCityAndKeyword(ids,strings,"MEMPHIS");

        Assertions.assertEquals(0,clubs.size());
    }

    @Test
    public void findBySportAndCityAndKeyword_hasSport_hasCity_hasKeyword() {
        clubRepository.save(club);

        ArrayList<Long> ids = new ArrayList<>();
        ids.add(sport.getId());
        ArrayList<String> strings = new ArrayList<>();
        strings.add("WRONG");
        strings.add("MEMPHIS");
        List<Club> clubs = clubRepository.findBySportAndCityAndKeyword(ids,strings,"NAME");

        Assertions.assertEquals(1,clubs.size());
        Assertions.assertEquals(club,clubs.get(0));
    }


    @Test
    void findAllTeamMembersWithRoles_notMember() {
        clubRepository.save(club);


        Integer[] roles = {Role.MEMBER.ordinal()};
        Assertions.assertTrue(clubRepository.findIfTeamMemberHasRole(roles,user.getId(),club.getId()).isEmpty());

    }

    @Test
    void findAllTeamMembersWithRoles_isMember() {
        clubRepository.save(club);

        TeamMember teamMember = new TeamMember(user,team1,Role.MEMBER);
        teamMemberRepository.save(teamMember);

        Integer[] roles = {Role.COACH.ordinal(), Role.MANAGER.ordinal()};
        Assertions.assertTrue(clubRepository.findIfTeamMemberHasRole(roles,user.getId(),club.getId()).isEmpty());

    }

    @Test
    void findAllTeamMembersWithRoles_isManager() {
        clubRepository.save(club);

        TeamMember teamMember = new TeamMember(user,team1,Role.MEMBER);
        teamMemberRepository.save(teamMember);

        Integer[] roles = {Role.COACH.ordinal(), Role.MEMBER.ordinal()};
        Assertions.assertTrue(clubRepository.findIfTeamMemberHasRole(roles,user.getId(),club.getId()).isPresent());

    }

    @Test
    void findUsersInAClub() {
        clubRepository.save(club);
        List<Long> userIds = clubRepository.findUsersInAClub(club.getId());
        Assertions.assertEquals(1, userIds.size());
        Assertions.assertEquals(user.getId(), userIds.get(0));
    }
}

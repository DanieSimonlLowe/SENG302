package nz.ac.canterbury.seng302.tab.repository;

import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.tab.entity.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@Transactional
 class UserRepositoryTest {

    @Resource
    private TeamRepository teamRepository;

    @Resource
    private LocationRepository locationRepository;

    @Resource UserRepository userRepository;

    @Resource TeamMemberRepository teamMemberRepository;

    Location location;
    User user;
    Team team;
    @BeforeEach
    void setup() {
        location = new Location("", "", "", "", "Memphis", "United States of America");
        locationRepository.save(location);
        user = new User("","","",LocalDate.now(),location,"");
        team  = new Team("",location,"");
    }

    private void linkTeamUser(Team team, User user) {
        TeamMember teamMember = new TeamMember(user,team, Role.MEMBER);
        teamMemberRepository.save(teamMember);
    }

    @Test
    void findUsersWhoAreInTeam_NoUserNoTeam() {
        List<User> userList = userRepository.findUsersWhoAreInTeam(0);
        Assertions.assertEquals(0,userList.size());
    }

    @Test
    void findUsersWhoAreInTeam_NoUserHasTeam() {
        teamRepository.save(team);
        List<User> userList = userRepository.findUsersWhoAreInTeam(team.getId());
        Assertions.assertEquals(0,userList.size());
    }

    @Test
    void findUsersWhoAreInTeam_HasUserHasTeamDiff() {
        teamRepository.save(team);
        userRepository.save(user);
        List<User> userList = userRepository.findUsersWhoAreInTeam(team.getId());
        Assertions.assertEquals(0,userList.size());
    }

    @Test
    void findUsersWhoAreInTeam_HasUserNoTeam() {
        userRepository.save(user);
        List<User> userList = userRepository.findUsersWhoAreInTeam(0);
        Assertions.assertEquals(0,userList.size());
    }

    @Test
    void findUsersWhoAreInTeam_HasUserHasTeamSame() {
        teamRepository.save(team);
        userRepository.save(user);
        linkTeamUser(team,user);

        List<User> userList = userRepository.findUsersWhoAreInTeam(team.getId());
        Assertions.assertEquals(1,userList.size());
        Assertions.assertEquals(user.getId(),userList.get(0).getId());
    }

    @Test
    void getFriends_NoFriends() {
        userRepository.save(user);
        List<Long> friends = userRepository.findFriends(user.getId());
        Assertions.assertEquals(0, friends.size());
    }

    @Test
    void getFriends_HasFriends() {
        userRepository.save(user);
        User user2 = new User("","","",LocalDate.now(),location,"");
        userRepository.save(user2);
        User user3 = new User("","","",LocalDate.now(),location,"");
        userRepository.save(user3);
        User user4 = new User("","","",LocalDate.now(),location,"");
        userRepository.save(user4);

        user.follow(user2);
        user.follow(user3);
        user.follow(user4);

        user2.follow(user);
        user3.follow(user);
        user4.follow(user);

        List<Long> friends = userRepository.findFriends(user.getId());
        Assertions.assertEquals(3, friends.size());
        Assertions.assertTrue(friends.contains(user2.getId()));
        Assertions.assertTrue(friends.contains(user3.getId()));
        Assertions.assertTrue(friends.contains(user4.getId()));
    }

    @Test
     void findUsersFollowing() {
        User followedUser = new User("followed1@test.com","followed","1",LocalDate.now(),location,"");
        followedUser = userRepository.save(followedUser);
        user.follow(followedUser);
        User repoUser = userRepository.save(user);
        List<User> followingUserList =  userRepository.findFollowedUsersByUserId(repoUser.getId());

        Assertions.assertEquals(1, followingUserList.size());
        Assertions.assertEquals(followedUser.getId(), followingUserList.get(0).getId());
    }

    @Test
    void findAllByIds() {
        Long user1Id = userRepository.save(user).getId();
        Long user2Id = userRepository.save(new User("","","",LocalDate.now(),location,"")).getId();
        Long user3Id = userRepository.save(new User("","","",LocalDate.now(),location,"")).getId();
        Long user4Id = userRepository.save(new User("","","",LocalDate.now(),location,"")).getId();
        List<Long> ids = List.of(user1Id, user2Id, user3Id, user4Id);
        Assertions.assertEquals(4, userRepository.findAllById(ids).size());
    }

    @Test
    void countTeamsInCommon_test() {
        Assertions.assertEquals(11,userRepository.countTeamsInCommon(1,3));
    }

    @Test
    void hasClubInCommon_noCommon() {
        Assertions.assertEquals(0, userRepository.hasClubInCommon(1L, 19L));
    }

    @Test
    void hasClubInCommon_hasCommon() {
        Assertions.assertTrue( 0 < userRepository.hasClubInCommon(1L,18L));
    }
}

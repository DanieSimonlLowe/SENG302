package nz.ac.canterbury.seng302.tab.repository;

import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.service.TeamMemberService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class ActivityRepositoryTest {
    @Resource
    private TeamRepository teamRepository;
    @Resource
    private UserRepository userRepository;
    @Resource
    private LocationRepository locationRepository;
    @Resource
    private ActivityRepository activityRepository;
    @Resource
    private TeamMemberService teamMemberService;

    final private Location location = new Location("", "", "", "", "Memphis", "United States of America");
    final private Team team1 = new Team("name", location, "sport");
    final private Team team2 = new Team("name", location, "sport");
    final private User user1 = new User("email", "firstName", "lastName", LocalDate.now(), location, "passwordHash");
    final private User user2 = new User("email", "firstName", "lastName", LocalDate.now(), location, "passwordHash");
    private Activity activity1;

    private Activity activity2;

    private Activity activity3;

    final int[] roles = {Role.MANAGER.ordinal(), Role.COACH.ordinal(), Role.MEMBER.ordinal()};

    final int[] roles_exMem = {Role.MANAGER.ordinal(), Role.COACH.ordinal()};

    User user;

    Team team;

    Activity activity;

    @BeforeEach
    void beforeEach() {
        locationRepository.save(location);
        teamRepository.save(team1);
        teamRepository.save(team2);
        userRepository.save(user1);
        userRepository.save(user2);
        activity1 = new Activity(ActivityType.OTHER, team1, null, "description1", LocalDateTime.now(), LocalDateTime.now(), user1, location);
        activity2 = new Activity(ActivityType.TRAINING, team2, null, "description2", LocalDateTime.now(), LocalDateTime.now(), user1, location);
        activity3 = new Activity(ActivityType.FRIENDLY, team1, null, "description3", LocalDateTime.now(), LocalDateTime.now(), user2, location);
        activityRepository.save(activity1);
        activityRepository.save(activity2);
        activityRepository.save(activity3);

        user = new User("","","",LocalDate.now(),location,"");
        userRepository.save(user);
        team = new Team("a",location,"a");
        teamRepository.save(team);
        activity = new Activity(ActivityType.OTHER,team, null, "", LocalDateTime.now(),LocalDateTime.now(), user, location);
        activityRepository.save(activity);
    }

    @Test
    void findByUserPersonalActivities() {

        // Given
        Activity personalActivity1 = new Activity(ActivityType.TRAINING, null, null, "personal activity 1", LocalDateTime.now(), LocalDateTime.now(), user1, location);
        Activity personalActivity2 = new Activity(ActivityType.OTHER, null, null, "personal activity 2", LocalDateTime.now(), LocalDateTime.now(), user1, location);
        Activity personalActivity3 = new Activity(ActivityType.OTHER, null, null, "personal activity 3", LocalDateTime.now(), LocalDateTime.now(), user2, location);

        activityRepository.save(personalActivity1);
        activityRepository.save(personalActivity2);
        activityRepository.save(personalActivity3);

        // When
        List<Activity> user1Activities = activityRepository.findPersonalActivitiesThatHasUser(user1.getId());
        List<Activity> user2Activities = activityRepository.findPersonalActivitiesThatHasUser(user2.getId());

        // Then
        assertEquals(2, user1Activities.size());
        assertEquals(1, user2Activities.size());

    }


    @Test
    void findByUserPartOfOneTeamActivities() {
        // Given
        // User1 is a member of team1
        teamMemberService.addTeamMember(user1, team1.getTeamToken());
        // When
        List<Activity> user1Activities = activityRepository.findTeamActivitiesThatHasUser(user1.getId());
        // then
        assertEquals(2, user1Activities.size());
    }

    @Test
    void findByUserPartOfTwoTeamsActivities() {
        // Given
        // User1 a member of team2 and team1
        teamMemberService.addTeamMember(user1, team2.getTeamToken());
        teamMemberService.addTeamMember(user1, team1.getTeamToken());
        Activity activity4 = new Activity(ActivityType.OTHER, team2, null, "description4", LocalDateTime.now(), LocalDateTime.now(), user2, location);
        Activity activity5 = new Activity(ActivityType.TRAINING, team1, null, "description5", LocalDateTime.now(), LocalDateTime.now(), user2, location);
        Activity activity6 = new Activity(ActivityType.FRIENDLY, team1, null, "description6", LocalDateTime.now(), LocalDateTime.now(), user2, location);
        activityRepository.save(activity4);
        activityRepository.save(activity5);
        activityRepository.save(activity6);
        // When
        List<Activity> user1Activities = activityRepository.findTeamActivitiesThatHasUser(user1.getId());
        // Then
        assertEquals(6, user1Activities.size());
    }

    private void JoinTeam(Team team, User user, Role role) {
        TeamMember teamMember = teamMemberService.addTeamMember(user,team.getTeamToken());
        try {
            teamMemberService.changeRole(teamMember,role);
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void findByIdWithTheUserOfRole_noActivityTest() {
        Assertions.assertTrue(activityRepository.findByIdWithTheUserOfRoleOrIsPersonal(-1,-1,roles).isEmpty());
    }

    @Test
    public void findByIdWithTheUserOfRole_hasActivityTest_UserNotInTeam() {
        Assertions.assertTrue(activityRepository.findByIdWithTheUserOfRoleOrIsPersonal(activity.getId(),user.getId(),roles).isEmpty());
    }

    @Test
    public void findByIdWithTheUserOfRole_hasActivityTest_aUserInTeam_UserNotInTeam() {
        User user1 = new User("","","",LocalDate.now(),location,"");
        userRepository.save(user1);
        JoinTeam(team,user1,Role.MANAGER);

        Assertions.assertTrue(activityRepository.findByIdWithTheUserOfRoleOrIsPersonal(activity.getId(),user2.getId(),roles).isEmpty());
    }

    @Test
    public void findByIdWithTheUserOfRole_hasActivityTest_userInTeam() {
        JoinTeam(team,user,Role.MANAGER);

        List<Activity> activity1 = activityRepository.findByIdWithTheUserOfRoleOrIsPersonal(activity.getId(),user.getId(),roles);
        Assertions.assertEquals(1,activity1.size());
        Assertions.assertEquals(activity.getId(),activity1.get(0).getId());
    }

    @Test
    public void findByIdWithTheUserOfRole_doseNotHasActivityTest_userInTeam_() {
        JoinTeam(team,user,Role.MANAGER);

        List<Activity> activity1 = activityRepository.findByIdWithTheUserOfRoleOrIsPersonal(activity.getId()+1,user.getId(),roles);
        Assertions.assertTrue(activity1.isEmpty());
    }

    @Test
    public void findByIdWithTheUserOfRole_hasActivityTest_userInTeam_WrongRole() {
        JoinTeam(team,user1,Role.MEMBER);

        List<Activity> activity1 = activityRepository.findByIdWithTheUserOfRoleOrIsPersonal(activity.getId(),user1.getId(),roles_exMem);
        Assertions.assertTrue(activity1.isEmpty());
    }

    @Test
    public void findByIdWithTheUserOfRole_hasActivityTest_userInTeam_WriteRole() {
        JoinTeam(team,user,Role.MANAGER);

        List<Activity> activity1 = activityRepository.findByIdWithTheUserOfRoleOrIsPersonal(activity.getId(),user.getId(),roles_exMem);
        Assertions.assertEquals(1,activity1.size());
        Assertions.assertEquals(activity.getId(),activity1.get(0).getId());
    }

    @Test
    public void findByIdWithTheUserOfRole_hasActivityTest_userInTeam_OnlyWriteRole() {
        JoinTeam(team,user,Role.MEMBER);
        int[] mem = {Role.MEMBER.ordinal()};

        List<Activity> activity1 = activityRepository.findByIdWithTheUserOfRoleOrIsPersonal(activity.getId(),user.getId(),mem);
        Assertions.assertFalse(activity1.isEmpty());
        Assertions.assertEquals(activity.getId(),activity1.get(0).getId());
    }

    @Test
    public void IfNoTeamOwner_getPersonalActivity() {
        Activity personalActivity1 = new Activity(ActivityType.TRAINING, null, null, "personal activity 1", LocalDateTime.now(), LocalDateTime.now(), user, location);
        activityRepository.save(personalActivity1);


        int[] mem = {Role.MEMBER.ordinal()};
        List<Activity> activities = activityRepository.findByIdWithTheUserOfRoleOrIsPersonal(personalActivity1.getId(),user.getId(),mem);
        Assertions.assertFalse(activities.isEmpty());
        Assertions.assertEquals(personalActivity1.getId(), activities.get(0).getId());
    }

    @Test
    public void findTeamActivitiesThatHasUser_getActivity() {
        JoinTeam(team,user,Role.MEMBER);
        List<Activity> activities = activityRepository.findTeamActivitiesThatHasUser(user.getId());
        Assertions.assertEquals(1,activities.size());
        Assertions.assertEquals(activity,activities.get(0));
    }

    @Test
    public void  findTeamActivitiesThatHasUser_getNoActivity() {
        locationRepository.save(user1.getLocation());
        userRepository.save(user1);
        List<Activity> activities = activityRepository.findTeamActivitiesThatHasUser(user1.getId());
        Assertions.assertEquals(0,activities.size());
    }

    @Test
    public void findTeamActivitiesThatHasUser_getActivity_notOwner() {
        locationRepository.save(user1.getLocation());
        userRepository.save(user1);
        JoinTeam(team,user1,Role.MEMBER);
        List<Activity> activities = activityRepository.findTeamActivitiesThatHasUser(user1.getId());
        Assertions.assertEquals(1,activities.size());
        Assertions.assertEquals(activity.getId(),activities.get(0).getId());
    }

    @Test
    public void findPersonalActivitiesThatHasUser_getNoActivity() {
        locationRepository.save(user1.getLocation());
        userRepository.save(user1);
        List<Activity> activities = activityRepository.findPersonalActivitiesThatHasUser(user1.getId());
        Assertions.assertEquals(0,activities.size());
    }

    @Test
    public void findPersonalActivitiesThatHasUser_getActivityHasTeam() {
        List<Activity> activities = activityRepository.findPersonalActivitiesThatHasUser(user.getId());
        Assertions.assertEquals(0,activities.size());
    }

    @Test
    public void findPersonalActivitiesThatHasUser_getActivityNoTeam() {
        locationRepository.save(user1.getLocation());
        userRepository.save(user1);
        activity1 = new Activity(ActivityType.OTHER,null, null,"",LocalDateTime.now(),LocalDateTime.now(),user1,location);
        activityRepository.save(activity1);

        List<Activity> activities = activityRepository.findPersonalActivitiesThatHasUser(user1.getId());
        Assertions.assertEquals(1,activities.size());
        Assertions.assertEquals(activity1.getId(),activities.get(0).getId());
    }
}

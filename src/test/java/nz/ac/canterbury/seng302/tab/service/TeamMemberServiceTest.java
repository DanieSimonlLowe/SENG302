package nz.ac.canterbury.seng302.tab.service;

import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.exceptions.InvalidTeamException;
import nz.ac.canterbury.seng302.tab.exceptions.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;

@SpringBootTest
@Transactional
@WithMockUser("morgan.english@hotmail.com")
public class TeamMemberServiceTest {

    @Resource
    private LocationService locationService;

    @Resource
    private UserService userService;

    @Resource
    private TeamService teamService;

    @Resource
    private TeamMemberService teamMemberService;

    private User user;
    private Team team;

    private Location christchurch;

    private static final String PASSWORD_HASH = "!2a$04$fvrk2c2qoLNtMbGFGsBLBO/BAUvCQxmmHeJyaesK5szZ6oqU0Q6Jq";


    @BeforeEach
    public void setup() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("morgan.english@hotmail.com",null));
        christchurch =  new Location("", "", "", "", "Christchurch", "New Zealand");
        Long locationId = locationService.addLocation(christchurch).getId();
        Assertions.assertNotNull(locationService.getLocation(locationId));
        user = new User("test@mail.com", "User", "Test", LocalDate.of(2002,10,21), christchurch, PASSWORD_HASH);
        Long userId = userService.save(user).getId();
        Assertions.assertTrue(userService.getUser(userId).isPresent());
        team = new Team("Test Team", christchurch, "Basketball");
        Long teamId = teamService.addTeam(team).getId();
        Assertions.assertNotNull(teamService.getTeam(teamId));
    }

    @Test
    public void getTeamMember_byId_validOutput() {
        TeamMember teamMember = teamMemberService.addTeamMember(user, team.getTeamToken());
        Assertions.assertNotNull(teamMember.getTeamMemberId());
        Assertions.assertTrue(teamMemberService.getTeamMember(teamMember.getTeamMemberId()).isPresent());
        Assertions.assertEquals(user.getId(), teamMemberService.getTeamMember(teamMember.getTeamMemberId()).get().getTeamMemberId().getUser().getId());
    }

    @Test
    public void getAllTeamMembers_byTeam_validOutput() {
        User user2 = new User("test@mail.com", "John", "Smith", LocalDate.of(2002,10,21), christchurch, PASSWORD_HASH);
        userService.save(user2);
        teamMemberService.addTeamMember(user, team.getTeamToken());
        teamMemberService.addTeamMember(user2, team.getTeamToken());
        Assertions.assertEquals(3, teamMemberService.getAllTeamMembersFromTeam(team).size());
    }

    @Test
    public void joiningTeam_validToken() {
        TeamMember teamMember = teamMemberService.addTeamMember(user, team.getTeamToken());
        boolean found = false;
        for (TeamMember member : teamMemberService.getAllTeamMembersFromTeam(team)) {
            if (member.getTeamMemberId().getUser().getId().equals(user.getId())) {
                found = true;
                Assertions.assertEquals(Role.MEMBER, member.getRole());
                Assertions.assertEquals(teamMember.getTeamMemberId(), member.getTeamMemberId());
            }
        }
        Assertions.assertTrue(found);
    }

    @Test
    public void joiningTeam_invalidToken() {
        TeamMember teamMember = teamMemberService.addTeamMember(user, "NONEXISTENCE");
        Assertions.assertNull(teamMember);
        Assertions.assertEquals(1, teamMemberService.getAllTeamMembersFromTeam(team).size());
    }

    @Test
    public void joiningTeam_alreadyPartOf() {
        TeamMember teamMember = teamMemberService.addTeamMember(user, team.getTeamToken());
        boolean found = false;
        for (TeamMember member : teamMemberService.getAllTeamMembersFromTeam(team)) {
            if (member.getTeamMemberId().getUser().getId().equals(user.getId())) {
                found = true;
                Assertions.assertEquals(Role.MEMBER, member.getRole());
                Assertions.assertEquals(teamMember.getTeamMemberId(), member.getTeamMemberId());
            }
        }
        Assertions.assertTrue(found);
        teamMemberService.addTeamMember(user, team.getTeamToken());
        int count = 0;
        for (TeamMember member : teamMemberService.getAllTeamMembersFromTeam(team)) {
            if (member.getTeamMemberId().getUser().getId().equals(user.getId())) {
                count++;
                Assertions.assertEquals(Role.MEMBER, member.getRole());
                Assertions.assertEquals(1, count);
            }
        }
    }

    @Test
    public void updatingTeamMemberRole_ToManagerAndTeamMemberExists_ValidOutput() throws NotFoundException, InvalidTeamException {
        TeamMember teamMember = teamMemberService.addTeamMember(user, team.getTeamToken());
        TeamMemberId oldId = teamMember.getTeamMemberId();
        Assertions.assertNotNull(teamMember.getTeamMemberId());
        teamMember = teamMemberService.changeRole(teamMember, Role.MANAGER);
        Assertions.assertEquals(Role.MANAGER, teamMember.getRole());
        Assertions.assertEquals(oldId, teamMember.getTeamMemberId());
    }

    @Test
    public void updatingTeamMemberRole_ToManagerAndTeamMemberNotExists_ValidOutput()  {
        TeamMember teamMember = new TeamMember(user, team, Role.MEMBER);
        Assertions.assertTrue(teamMemberService.getTeamMember(teamMember.getTeamMemberId()).isEmpty());
        Assertions.assertThrows(NotFoundException.class, () -> teamMemberService.changeRole(teamMember, Role.MANAGER));
    }

    @ParameterizedTest
    @EnumSource(Role.class)
    public void getAll_ByRole_FromTeam(Role role) throws NotFoundException, InvalidTeamException {
        TeamMember teamMember = teamMemberService.addTeamMember(user, team.getTeamToken());
        TeamMemberId oldId = teamMember.getTeamMemberId();
        teamMember = teamMemberService.changeRole(teamMember, role);
        Assertions.assertEquals(role, teamMember.getRole());
        Assertions.assertEquals(oldId, teamMember.getTeamMemberId());
    }

}

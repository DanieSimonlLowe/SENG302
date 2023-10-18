package nz.ac.canterbury.seng302.tab.service;

import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
@Transactional
@WithMockUser("morgan.english@hotmail.com")
public class TeamInvitationTokenServiceTest {
    @Resource
    private LocationService locationService;

    @Resource
    private TeamService teamService;

    @Resource
    private TeamInvitationTokenService teamInvitationTokenService;

    private Team team;

    @BeforeEach
    public void setup() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("morgan.english@hotmail.com",null));
        Location christchurch = new Location("", "", "", "", "Christchurch", "New Zealand");
        Long locationId = locationService.addLocation(christchurch).getId();
        Assertions.assertNotNull(locationService.getLocation(locationId));
        team = new Team("Test Team", christchurch, "Basketball");
        Long teamId = teamService.addTeam(team).getId();
        Assertions.assertNotNull(teamService.getTeam(teamId));
    }

    @Test
    public void getTeam_byTeamToken() {
        Assertions.assertTrue(teamInvitationTokenService.findByToken(team.getTeamToken()).isPresent());
        Assertions.assertEquals(teamInvitationTokenService.findByToken(team.getTeamToken()).get(), team);
    }

    @Test public void updateTeamToken() {
        String oldToken = team.getTeamToken();
        teamInvitationTokenService.updateTeamToken(team);
        Assertions.assertTrue(teamInvitationTokenService.findByToken(team.getTeamToken()).isPresent());
        Assertions.assertNotEquals(teamInvitationTokenService.findByToken(team.getTeamToken()).get().getTeamToken(), oldToken);
    }

}

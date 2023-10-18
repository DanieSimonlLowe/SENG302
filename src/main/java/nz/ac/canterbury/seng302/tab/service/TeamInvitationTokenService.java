package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.repository.TeamInvitationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * The service for the registration token.
 *
 * @author  Kahu Griffin
 * @version 1.0.0, April 28th
 */

@Service
public class TeamInvitationTokenService {

    @Autowired
    private TeamInvitationTokenRepository repository;


    public TeamInvitationTokenService(TeamInvitationTokenRepository repository) {
        this.repository = repository;
    }


    /**
     * Finds a team by a given invitation token
     * @param token the token to search by
     * @return optionally team with the matching token
     */
    public Optional<Team> findByToken(String token) {
        return repository.findByToken(token);
    }

    /**
     * Updates the given team's invitation token
     * @param team the team whose token to update
     * @return number of rows that where updated in the database
     */
    public int updateTeamToken(Team team) {
        String newToken = Team.generateToken();
        team.setToken(newToken);
        return repository.updateTeamToken(team.getId(), newToken);
    }
}

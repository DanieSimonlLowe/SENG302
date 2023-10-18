package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.Team;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


/**
 * TeamInvitationTokenRepository repository accessor using Spring's @link{CrudRepository}.
 * Repository to query data from the database
 */
@Repository
public interface TeamInvitationTokenRepository extends CrudRepository<Team, Long>{
    /**
     * Finds a team token by the string token
     * @param token the token to search by
     * @return optionally the team with the given token
     */
    Optional<Team> findByToken(String token);

    /**
     * Updates a team's token in the database
     * @param teamId the id of the team to update
     * @param token the update token
     * @return the number of rows that where updated in the database
     */
    @Transactional
    @Modifying
    @Query(value = "UPDATE team SET invitation_token = :token WHERE id = :teamId", nativeQuery = true)
    int updateTeamToken(Long teamId, String token);
}

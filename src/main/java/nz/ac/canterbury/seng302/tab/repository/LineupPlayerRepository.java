package nz.ac.canterbury.seng302.tab.repository;

import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.tab.entity.LineupPlayer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface LineupPlayerRepository extends CrudRepository<LineupPlayer, Long> {
    /**
     * Finds lineup by id
     * @param id the id to search for
     * @return an optional lineup
     */
    Optional<LineupPlayer> findById(Long id);

    /**
     * Finds the users in a lineup by lineup id
     * @param lineupId the lineup id to search for
     * @return a list of user ids
     */
    @Query(
            value="SELECT user_id FROM tab_lineup_player WHERE line_up_id = :lineupId",
            nativeQuery = true
    )
    List<Long> getUserIdsByLineUpId(Long lineupId);

    @Transactional
    void removeLineupPlayersByLineUp_Id(Long lineUpId);
}

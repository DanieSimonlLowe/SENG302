package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.LineupPlayer;
import nz.ac.canterbury.seng302.tab.repository.LineupPlayerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LineupPlayerService {

    private final LineupPlayerRepository lineupPlayerRepository;

    /**
     * Constructor for LineupPlayerService
     * @param lineupPlayerRepository the repository to be used
     */
    public LineupPlayerService(LineupPlayerRepository lineupPlayerRepository) {
        this.lineupPlayerRepository = lineupPlayerRepository;
    }

    /**
     * Saves a lineupPlayer to the database
     * @param lineupPlayer the lineupPlayer to be saved
     */
    public void save(LineupPlayer lineupPlayer) {
        lineupPlayerRepository.save(lineupPlayer);
    }

    /**
     * Gets all lineupPlayer's user ids by lineup id
     * @param id the lineup id to be searched for
     * @return the list of user ids
     */
    public List<Long> getAllUserIdsByLineUpId(long id) { return lineupPlayerRepository.getUserIdsByLineUpId(id); }

    /**
     * Removes all lineupPlayers by lineup id
     * @param lineUpId the lineup id to be searched for
     */
    public void removeLineupPlayersByLineUpId(Long lineUpId) {
        lineupPlayerRepository.removeLineupPlayersByLineUp_Id(lineUpId);
    }
}

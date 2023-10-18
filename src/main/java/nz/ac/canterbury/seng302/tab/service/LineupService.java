package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.LineUp;
import nz.ac.canterbury.seng302.tab.repository.LineupRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LineupService {

    private final LineupRepository lineupRepository;

    /**
     * Constructor for LineupService
     * @param lineupRepository the repository to be used
     */
    public LineupService(LineupRepository lineupRepository) {
        this.lineupRepository = lineupRepository;
    }

    /**
     * Saves a lineup to the database
     * @param lineup the lineup to be saved
     */
    public void save(LineUp lineup) {
        lineupRepository.save(lineup);
    }

    /**
     * Gets a lineup by activity id
     * @param activityId the activity id to be searched for
     * @return the lineup if found
     */
    public Optional<LineUp> getLineupByActivityId(long activityId) {return lineupRepository.findByActivityId(activityId);}

    /**
     * Removes a lineup by activity id
     * @param activityId the activity id to be searched for
     */
    public void removeLineupByActivityId(long activityId) {lineupRepository.removeAllByActivity_Id(activityId);}
}

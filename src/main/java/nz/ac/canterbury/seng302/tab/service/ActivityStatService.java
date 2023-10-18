package nz.ac.canterbury.seng302.tab.service;

import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.tab.entity.stats.*;
import nz.ac.canterbury.seng302.tab.repository.ActivityStatRepository;
import org.hibernate.WrongClassException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * The service for the ActivityStatService.
 *
 * @author  Daniel Lowe
 * @version 1.0.0, April 23
 */
@Service
public class ActivityStatService {
    ActivityStatRepository activityStatRepository;

    @Autowired
    ActivityStatService(ActivityStatRepository activityStatRepository) {
        this.activityStatRepository = activityStatRepository;
    }


    /**
     * Finds an activity stat from its id
     * @param activityStatId the id of the activity stat
     * @return an activity stat, if it exists
     */
    public Optional<ActivityStat> findById(Long activityStatId) {
        return activityStatRepository.findById(activityStatId);
    }

    /**
     * Gets all facts related to an activity
     * @param activityId the id of the activity
     * @return a list of facts related to an activity
     */
    public List<Fact> getAllActivityFacts(Long activityId) {
        return activityStatRepository.findAllFactByActivityId(activityId);
    }

    /**
     * Gets all substitutions related to an activity
     * @param activityId the id of the activity
     * @return a list of substitutions related to an activity
     */
    public List<Substituted> getAllActivitySubstituted(Long activityId) {
        return activityStatRepository.findAllSubstitutedByActivityId(activityId);
    }

    /**
     * Gets all scores related to an activity
     * @param activityId the id of the activity
     * @return a list of scores related to an activity
     */
    public List<IndividualScore> getAllActivityIndividualScores(Long activityId) {
        return activityStatRepository.findAllIndividualScoreByActivityId(activityId);
    }

    /**
     * Saves an activity stat
     * @param activityStat the activity stat to save
     * @throws IllegalStateException is thrown when the save fails
     */
    public void save(ActivityStat activityStat) throws IllegalStateException {
        if (!activityStat.canSave(activityStatRepository)) {
            throw new IllegalStateException("can't save that activity stat.");
        }
        activityStatRepository.save(activityStat);
    }

    /**
     * Deletes a fact from persistence.
     * @param factId the id of the fact to delete
     */
    @Transactional
    public void deleteFact(Long factId) {
        activityStatRepository.deleteFact(factId);
    }


    /**
     *Finds the games core associated with an activity if it exists
     *@param activityId the id of the activity
     *@return the game score of the activity
     * */
    public Optional<GameScore> getActivityGameScores(Long activityId) {
        return activityStatRepository.findGameScoreByActivityId(activityId);
    }

    /**
     * saves a game score, and override the old one if one already exists for the linked activity.
     * @param gameScore the game score to be saved
     * @throws IllegalStateException is thrown if the game score is not valid
     * @throws WrongClassException is thrown if the activityStatRepository retrieves a non game score stat when it is trying to retrieve the old game score
     * */
    public void saveGameScore(GameScore gameScore) throws IllegalStateException, WrongClassException {
        Optional<GameScore> oldGameScore = activityStatRepository.findGameScoreByActivityId(gameScore.getActivity().getId());
        if (oldGameScore.isEmpty()) {
            activityStatRepository.save(gameScore);
        } else {
            oldGameScore.get().setScores(gameScore);
            activityStatRepository.save(oldGameScore.get());
        }
    }
}

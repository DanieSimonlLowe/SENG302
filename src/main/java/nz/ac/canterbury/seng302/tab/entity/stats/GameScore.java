package nz.ac.canterbury.seng302.tab.entity.stats;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.ActivityType;
import nz.ac.canterbury.seng302.tab.exceptions.InvalidActivityException;
import nz.ac.canterbury.seng302.tab.exceptions.MismatchGameScoreTypeException;
import nz.ac.canterbury.seng302.tab.repository.ActivityStatRepository;



@Entity
@Table(name = "GameScoreStat")
public class GameScore extends ActivityStat {


    @Column
    private String score1;

    @Column
    private String score2;

    protected GameScore() {super();}

    /**
     * checks if a string is a valid score by throwing an execution if it is not.
     * @param score the string being checked
     * @return if the score is not multipart.
     * @exception NullPointerException is thrown if the score is blank or null
     * @exception NumberFormatException is thrown if the score contains non numbers.
     * */
    private boolean checkIsValidScore(String score) throws NullPointerException, NumberFormatException {
        if (score == null || score.isBlank()) {
            throw new NullPointerException();
        }
        if (score.charAt(0) == '-' || score.charAt(score.length()-1) == '-') {
            throw new NumberFormatException();
        }
        String[] scores = score.split("-");
        if (scores.length == 0) {
            throw new NullPointerException();
        }
        for (String s: scores) {
            if (!s.matches("\\d+")) {
                throw new NumberFormatException(s);
            }
        }
        return scores.length == 1;
    }

    /**
     * creates a score for a game.
     * @param activity the activity that the stat is of.
     * @param score1 the score of the first team in format "1-2-3" or "3"
     * @param score2 the score of the second team in format "1-2-3" or "3"
     * @exception InvalidActivityException thrown if the activity is in invalid.
     * @exception NullPointerException thrown if the score is blank or null
     * @exception NumberFormatException thrown if the score contains non numbers.
     * */
    public GameScore(Activity activity, String score1, String score2) throws InvalidActivityException, NullPointerException, NumberFormatException, MismatchGameScoreTypeException {
        super(activity);
        if (activity.getType() != ActivityType.FRIENDLY && activity.getType() != ActivityType.GAME) {
            throw new InvalidActivityException();
        }
        if (checkIsValidScore(score1) != checkIsValidScore(score2)) {
            throw new MismatchGameScoreTypeException();
        }
        this.score1 = score1;
        this.score2 = score2;
    }

    /**
     *  gets if the activity stat can be saved.
     *  @param activityStatRepository the repository that stores the activity stat.
     *  @return always returns false.
     * */
    @Override
    public boolean canSave(ActivityStatRepository activityStatRepository) {
        return false;
    }

    public void setScores(GameScore gameScore) {
        score1 = gameScore.score1;
        score2 = gameScore.score2;
    }

    public String getScore1() {
        return score1;
    }

    public String getScore2() {
        return score2;
    }
}

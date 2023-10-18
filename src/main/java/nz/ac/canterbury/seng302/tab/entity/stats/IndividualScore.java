package nz.ac.canterbury.seng302.tab.entity.stats;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.ActivityType;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.exceptions.InvalidActivityException;
import nz.ac.canterbury.seng302.tab.exceptions.TimeOutOfRangeException;

@Entity
@Table(name = "IndividualScoreStat")
public class IndividualScore extends ActivityStat {

    @Column(nullable = false)
    Integer scoreMinute;

    @ManyToOne
    User user;

    @Column
    float score;

    @ManyToOne
    Team team;

    protected IndividualScore() {
        super();
    }

    /**
     * creates an Individual Score.
     * @param activity the activity that the stat is of.
     * @param minute the minute that the score happened.
     * @param user the user that the score is for.
     * @param score the value that the score has
     * @exception InvalidActivityException thrown if the activity is invalid.
     * @exception NullPointerException thrown if the score is blank or null
     * @exception TimeOutOfRangeException thrown if the time is out of range of the activity.
     * */
    public IndividualScore(Activity activity, Team team, Integer minute, User user, float score) throws InvalidActivityException, NullPointerException, TimeOutOfRangeException {
        super(activity);
        if (activity.getType() != ActivityType.FRIENDLY && activity.getType() != ActivityType.GAME) {
            throw new InvalidActivityException();
        }
        if (user == null || minute == null) {
            throw new NullPointerException();
        }
        if (!activity.isInTimeRange(minute)) {
            throw new TimeOutOfRangeException();
        }
        this.team = team;
        this.scoreMinute = minute;
        this.score = score;
        this.user = user;
    }

    public Team getTeam() {
        return team;
    }
    public Integer getScoreMinute() {
        return scoreMinute;
    }

    public User getPlayer() {
        return user;
    }

    public float getPoints() {
        return score;
    }
}

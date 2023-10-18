package nz.ac.canterbury.seng302.tab.entity.stats;

import jakarta.persistence.*;
import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.ActivityType;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.exceptions.InvalidActivityException;
import nz.ac.canterbury.seng302.tab.exceptions.SameUserException;
import nz.ac.canterbury.seng302.tab.exceptions.TimeOutOfRangeException;

import java.util.Objects;

@Entity
@Table(name = "SubstitutedStat")
public class Substituted extends ActivityStat {

    @ManyToOne
    User oldPlayer;

    @ManyToOne
    User newPlayer;

    @Column(nullable = false)
    Integer subMinute;

    @ManyToOne
    Team team;


    protected Substituted() {super();}

    /**
     * creates a stat about the fact that a substitution happened in an activity.
     * @param activity the activity that the stat is of.
     * @param oldPlayer the old player that was substituted out.
     * @param newPlayer the new player that was substituted in.
     * @param minute the minute that the substitution happened.
     * @exception InvalidActivityException thrown if the activity is invalid.
     * @exception NullPointerException thrown if the time, oldPlayer or newPlayer is null.
     * @exception TimeOutOfRangeException thrown if the time is out of range of the activity.
     * @exception SameUserException thrown if the same user.
     * */
    public Substituted(Activity activity, User oldPlayer, User newPlayer, Integer minute, Team team) throws InvalidActivityException, TimeOutOfRangeException, SameUserException, NullPointerException {
        super(activity);
        if (minute == null || oldPlayer == null || newPlayer == null) {
            throw new NullPointerException();
        }
        if (activity.getType() != ActivityType.GAME && activity.getType() != ActivityType.FRIENDLY) {
            throw new InvalidActivityException();
        }
        if (!activity.isInTimeRange(minute)) {
            throw new TimeOutOfRangeException();
        }
        if (Objects.equals(oldPlayer.getId(), newPlayer.getId())) {
            throw new SameUserException();
        }
        this.oldPlayer = oldPlayer;
        this.newPlayer = newPlayer;
        this.subMinute = minute;
        this.team = team;
    }

    public User getOldPlayer() {
        return oldPlayer;
    }

    public User getNewPlayer() {
        return newPlayer;
    }

    public Integer getSubMinute() {
        return subMinute;
    }

    public Team getTeam() {
        return team;
    }


}

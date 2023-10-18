package nz.ac.canterbury.seng302.tab.entity.stats;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.ActivityType;
import nz.ac.canterbury.seng302.tab.exceptions.InvalidActivityException;
import nz.ac.canterbury.seng302.tab.exceptions.TimeOutOfRangeException;

import java.time.LocalDateTime;

@Entity
@Table(name = "FactStat")
public class Fact extends ActivityStat {
    @Column()
    String time;

    @Column
    String description;

    protected Fact() {super();}

    /**
     * creates a fact about an activity
     * @param activity the activity that the stat is of.
     * @param description the description of the fact
     * @param time the time that the fact is related to.
     * @exception InvalidActivityException thrown if the activity is in invalid.
     * @exception NullPointerException thrown if the description is blank or null
     * @exception TimeOutOfRangeException thrown if the time is out of range of the activity.
     * */
    public Fact(Activity activity, String description, String time) throws InvalidActivityException, NullPointerException, TimeOutOfRangeException {
        super(activity);
        if (activity.getType() == ActivityType.TRAINING) {
            throw new InvalidActivityException();
        }
        if (description == null || description.isBlank()) {
            throw new NullPointerException();
        }
        if (time != null) {
            long minutes  = Long.parseLong(time);
            LocalDateTime activityStart = activity.getStartDate();
            LocalDateTime activityEnd = activity.getEndDate();
            LocalDateTime factStart = activityStart.plusMinutes(minutes);
            if (factStart.isAfter(activityEnd)) {
                throw new TimeOutOfRangeException();
            }
        }
        this.description = description;
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public String getTime() {
        return time;
    }
}

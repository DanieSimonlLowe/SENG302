package nz.ac.canterbury.seng302.tab.entity.stats;

import jakarta.persistence.*;
import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.exceptions.InvalidActivityException;
import nz.ac.canterbury.seng302.tab.repository.ActivityStatRepository;

/**
 * a generic stat about an activity.
 * */
@Entity
@Table(name="ActivityStat")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class ActivityStat {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    public Long getId() {
        return id;
    }

    @ManyToOne
    private Activity activity;

    public Activity getActivity() {
        return activity;
    }

    /**
     * only use for testing.
     * */
    protected void setId(long id) {
        this.id = id;
    }

    protected ActivityStat () {}

    /**
     * creates a stat for an activity
     * @param activity the activity that the stat is of.
     * @exception InvalidActivityException thrown when the activity is invalid
     * */
    protected ActivityStat (Activity activity) throws InvalidActivityException {
        if (activity == null) {
            throw new InvalidActivityException();
        }
        this.activity = activity;
    }

    /**
     * gets if the activity stat can be saved.
     * @param activityStatRepository the repository that stores the activity stat.
     * @return if the activity stat can be saved
     * */
    public boolean canSave(ActivityStatRepository activityStatRepository) {
        return true;
    }
}
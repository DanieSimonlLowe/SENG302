package nz.ac.canterbury.seng302.tab.service.sorters;

import nz.ac.canterbury.seng302.tab.entity.Activity;

import java.util.Comparator;

/**
 * Custom comparator to sort Activities by ascending start time
 */
public class ActivityAlphaNameSorter implements Comparator<Activity> {

    /**
     * Compares activities in order to sort them by ascending start time
     * @param lhs an activity to compare
     * @param rhs an activity to compare
     */
    @Override
    public int compare(Activity lhs, Activity rhs) {
        int out = lhs.getStartTime().compareTo(rhs.getStartTime());
        if (out <= 0) {
            return out;
        } else {
            return lhs.getStartTime().compareTo(rhs.getStartTime());
        }
    }
}

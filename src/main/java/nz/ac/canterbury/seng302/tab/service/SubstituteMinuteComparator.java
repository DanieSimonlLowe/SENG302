
package nz.ac.canterbury.seng302.tab.service;


import nz.ac.canterbury.seng302.tab.entity.stats.Substituted;


import java.util.Comparator;


/**
 * Custom comparator to sort Facts by ascending start time
 */
public class SubstituteMinuteComparator implements Comparator<Substituted> {

    /**
     * Compares Substitutions in order to sort them by ascending start time
     * @param s1 a fact to compare
     * @param s2 a fact to compare
     */
    @Override
    public int compare(Substituted s1, Substituted s2) {
        return s1.getSubMinute().compareTo(s2.getSubMinute());

    }
}


package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.stats.Fact;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Custom comparator to sort Facts by ascending start time
 */
public class TimeStampComparator implements Comparator<Fact> {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    /**
     * Compares facts in order to sort them by ascending start time
     * @param fact1 a fact to compare
     * @param fact2 a fact to compare
     */
    @Override
    public int compare(Fact fact1, Fact fact2) {
        try {
            Date date1 = dateFormat.parse(fact1.getTime());
            Date date2 = dateFormat.parse(fact2.getTime());
            return date1.compareTo(date2);
        } catch (ParseException e) {
            // Handle parsing errors if necessary
        }
        return 0; // Default comparison value if parsing fails

    }
}

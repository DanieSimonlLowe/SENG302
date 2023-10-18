package nz.ac.canterbury.seng302.tab.service.sorters;

import nz.ac.canterbury.seng302.tab.entity.User;

import java.util.Comparator;

/**
 * Custom comparator to sort Users
 * 
 * @author Daniel Lowe
 * @version 1.0.0, March 23 
 */
public class UserAlphaNameSorter implements Comparator<User> {

    /**
     * Compares users in order to sort them
     * @param lhs a user to compare
     * @param rhs a user to compare
     */
    @Override
    public int compare(User lhs, User rhs) {
        int out = lhs.getLastName().compareToIgnoreCase(rhs.getLastName());
        if (out != 0) {
            return out;
        } else {
            return lhs.getFirstName().compareToIgnoreCase(rhs.getFirstName());
        }
    }
}

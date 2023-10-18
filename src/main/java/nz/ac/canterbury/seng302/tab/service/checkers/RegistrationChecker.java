package nz.ac.canterbury.seng302.tab.service.checkers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

/**
 * Checks that all registration details are valid when a user registers
 *
 * @author Daniel Lowe
 * @version 1.0.0, March 23
 */
public class RegistrationChecker {

    private final static int MIN_AGE = 13;

    /**
     * Logger used for debugging
     */
    static final Logger logger = LoggerFactory.getLogger(RegistrationChecker.class);

    /**
     * Empty constructor for RegistrationChecker
     */
    private RegistrationChecker() {}


    /**
     * Checks if a string is only made out of alphabetical charters, - or ' '
     * @param name the string being checked.
     * @return a boolean declaring whether the string is a valid name or not.
     * */
    public static boolean isValidName(String name) {
        boolean isOnlySpecialChar = true;

        if (name.length() < 1 || name.length() > 300) {
            return false;
        }
        for (int i = 0; i<name.length(); i++) {
            int code = name.codePointAt(i);
            if (Character.isLetter(code)) {
                isOnlySpecialChar = false;
            }
            if (!Character.isLetter(code) && code != '-' && code != ' ') {
                return false;
            }
        }
        return !isOnlySpecialChar;
    }

    /**
     * Checks if the email is a valid email or not.
     * @param email the email being checked.
     * @return a boolean declaring whether the string is a valid email or not.
     * */
    public static boolean isValidEmail(String email) {
        email = email.strip();
        if(email.length() > 255) return false;
        int atSymbolIndex = email.indexOf("@");
        if(atSymbolIndex == -1) return false;

        int dotSymbolIndex = email.substring(atSymbolIndex).indexOf(".");
        if(dotSymbolIndex <= 1) return false;

        // Have passed but can't have any more symbols other than the first two.
        String rest = email.substring(atSymbolIndex+dotSymbolIndex);
        if(rest.contains("@")) {
            return false;
        }
        return rest.length() > 1;
    }

    /**
     * Checks if the birthday entered is valid or not.
     * @param date the birthday being checked.
     * @return a boolean declaring whether the date is a valid birthday or not.
     * */
    public static boolean isBirthDateValid(LocalDate date) {
        LocalDate now = LocalDate.now();
        LocalDate earliestDate = LocalDate.of(now.getYear()-MIN_AGE,now.getMonth(),now.getDayOfMonth()).plusDays(1);
        return date.isBefore(earliestDate);
    }

    /**
     * Checks if a password is strong enough.
     * @param password the password being checked.
     * @param fields the other fields in the user.
     * @return A boolean declaring whether the password is strong enough.
     * */
    public static boolean isPasswordValid(String password, String[] fields) {

        if (password.length() < 8) {
            return false;
        }
        for (String field: fields) {
            if (password.toUpperCase().contains(field.toUpperCase())) {
                return false;
            }
        }
        boolean containsLetter = false;
        boolean containsNumber = false;
        boolean containsSpecial = false;
        for (int i = 0; i<password.length(); i++) {
            int code = password.codePointAt(i);
            if (Character.isLetter(code)) {
                containsLetter = true;
            } else if (Character.isDigit(code)) {
                containsNumber = true;
            } else {
                containsSpecial = true;
            }
        }
        return (containsLetter && containsNumber && containsSpecial);
    }

    /**
     * Checks if the given location is valid or not
     * @param address1   street address 1 of the user's location
     * @param address2   street address 1 of the user's location
     * @param suburb      the suburb of the user's location
     * @param postcode    the user's postcode
     * @param city        the user's city
     * @param country     the user's country
     * @return a boolean of whether the location is valid or not
     */
    public static boolean isLocationValid(String address1, String address2, String suburb, String postcode, String city, String country) {
        if (!address1.isBlank() && !address1.matches("^(?!.*[!])(?![\\d\\s'\\/,.-]*$)[\\p{L}\\d\\s'\\/,.-]+$")) {
            logger.info("Address1 ERROR");
            return false;
        } else if (!address2.isBlank() && !address2.matches("^(?!.*[!])(?![\\d\\s'/,.-]*$)[\\p{L}\\d\\s'\\/,.-]+$")) {
            logger.info("Address2 ERROR");
            return false;
        } else if (!suburb.isBlank() && !suburb.matches("^(?!.*[!])(?=.*[\\p{L}])[\\p{L}\\d\\s'\\/,.-]+$")) {
            logger.info("Suburb ERROR");
            return false;//
        } else if (!postcode.isBlank() && !postcode.matches("^(?:[\\p{L}\\p{N}]+-?[\\p{L}\\p{N}]+)+|[\\p{L}\\p{N}\\s]+$")) {
            logger.info("Postcode ERROR");
            return false;
        } else if (!city.matches("^(?=.*[\\p{L}])[\\p{L}\\d\\s'-\\/,.]*$")) {
            logger.info("City ERROR");
            return false;
        } else if (!country.matches("^(?!.*[\\d!])(?=.*[\\p{L}])[ \\p{L}\\s',.-]+$")) {
            logger.info("Country ERROR");
            return false;
        }
        return true;
    }

    /**
     * Gives the number of the invalid location part
     * @param address1   street address 1 of the user's location
     * @param address2   street address 1 of the user's location
     * @param suburb      the suburb of the user's location
     * @param postcode    the user's postcode
     * @param city        the user's city
     * @param country     the user's country
     * @return an integer which indicates which part was invalid
     */
    public static Integer locationInvalidPartNum(String address1, String address2, String suburb, String postcode, String city, String country) {
        if (!address1.isBlank() && !address1.matches("^(?!.*[!])(?![\\d\\s'\\/,.-]*$)[\\p{L}\\d\\s'\\/,.-]+$")) {
            return 1;
        } else if (!address2.isBlank() && !address2.matches("^(?!.*[!])(?![\\d\\s'/,.-]*$)[\\p{L}\\d\\s'\\/,.-]+$")) {
            return 2;
        } else if (!suburb.isBlank() && !suburb.matches("^(?!.*[!])(?=.*[\\p{L}])[\\p{L}\\d\\s'\\/,.-]+$")) {
            return 3;
        } else if (!postcode.isBlank() && !postcode.matches("(?:[\\p{L}\\p{N}]+-[\\p{L}\\p{N}]+)+|[\\p{L}\\p{N}]+")) {
            return 4;
        } else if (!city.matches("^(?=.*[\\p{L}])[\\p{L}\\s\\d'-\\/,.]*$")) {
            return 5;
        } else {
            return 6;
        }
    }


}

package nz.ac.canterbury.seng302.tab.service.checkers;

/**
 * The checker for searching
 * 
 * @author  Daniel Lowe
 * @version 1.0.0, April 23 
 */
public class SearchChecker {
    /**
     * Is used to check if a search is a valid search.
     * @param search the string that is being imput into the search bar.
     * @return if it is a valid search or not.
     * 
     * */
    public static boolean isValidSearch(String search) {

        if (search == null) {
            return false;
        }
        int letterCount = 0;
        for (int i = 0; i<search.strip().length(); i++) {
            if (!Character.isLetter(search.codePointAt(i))) {
                if (search.codePointAt(i) != ' ' && search.codePointAt(i) != '-') {
                    return false;
                }
            } else {
                letterCount += 1;
            }
        }
        return letterCount >= 3;
    }
}

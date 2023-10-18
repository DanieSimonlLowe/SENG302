package nz.ac.canterbury.seng302.tab.exceptions;

/**
 * Exception for an invalid team
 */
public class InvalidTeamException extends Exception{
    public InvalidTeamException(String message) {
        super(message);
    }
}

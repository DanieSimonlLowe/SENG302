package nz.ac.canterbury.seng302.tab.exceptions;

import org.springframework.security.authentication.BadCredentialsException;

/**
 * Exception for when the email address is malformed
 */
public class BadEmailException extends BadCredentialsException {

    /**
     * Constructor for BadEmailException - calls super class BadCredentialsException
     * @param msg the message for the error
     */
    public BadEmailException(String msg) {
        super(msg);
    }
}

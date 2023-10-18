package nz.ac.canterbury.seng302.tab.service.email;

/**
 * Custom EmailService interface, to allow for handling emails.
 * 
 * @author Daniel Lowe
 * @version 1.0.0, April 23
 */
public interface EmailService {

    /**
     * Sends an email with an HTML body
     * @param recipient who gets the mail.
     * @param msgBody the body of the mail message.
     * @param subject the subject of the mail message
     * @return if the message was succesfully sent.
     * */
    boolean sendHTMLMail(String recipient,String msgBody,String subject);
}

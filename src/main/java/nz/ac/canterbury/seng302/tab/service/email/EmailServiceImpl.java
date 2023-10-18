package nz.ac.canterbury.seng302.tab.service.email;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Custom EmailServiceImpl class that implements the {@link EmailService} interface, to allow for handling emails.
 * 
 * @author Daniel Lowe
 * @version 1.0.0, April 23
 */
@Service
public class EmailServiceImpl implements EmailService {

    /** The Mail sender */
    @Autowired
    private JavaMailSender javaMailSender;


    /** The sender email address */
    private static final String FROM = "noreply@TAB.co.nz";

    /**
     * Sends an email with HTML body to the recipient.
     * @param recipient who gets the mail.
     * @param msgBody the body of the mail message.
     * @param subject the subject of the mail message
     * @return if the message was succesfully sent.
     * */
    @Override
    public boolean sendHTMLMail(String recipient, String msgBody, String subject) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setFrom(FROM);
            helper.setTo(recipient);
            helper.setText(msgBody, true);
            helper.setSubject(subject);
            javaMailSender.send(mimeMessage);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

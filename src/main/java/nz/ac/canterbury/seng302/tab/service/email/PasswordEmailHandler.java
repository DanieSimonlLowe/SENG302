package nz.ac.canterbury.seng302.tab.service.email;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.TokenGenerator;
import nz.ac.canterbury.seng302.tab.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * The handler for the password related emails.
 * 
 * @version 1.0.0, April 23 
 */
@Service
public class PasswordEmailHandler {

    /** The registration token service */
    private final TokenService service;


    /** The email service */
    private final EmailService emailService;

    private final String baseUrl;


    /**
     * Constructor for the PasswordEmailHandler
     * @param service         the registration token service
     * @param emailService    the email service
     */
    @Autowired
    public PasswordEmailHandler(TokenService service, EmailService emailService, Environment env) {
        this.service = service;
        this.emailService = emailService;
        this.baseUrl = env.getProperty("tab.baseURL");
    }

    /**
     * Creates and stores a registration token. 
     * Sends the email with the registration token to the user that will redirect them to the login page.
     * @param email the users email
     * @param firstName the users first name
     * @param lastName the users last name
     * @param date the users date of birth
     * @param password the users password
     * @return if a token was successfully sent.
     * */
    public boolean sendResetLink(String email, String firstName, String lastName, LocalDate date, Location location, String password) {
        TokenGenerator token = new TokenGenerator(email, firstName, lastName, date, location, password, "resetPassword");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@TAB.co.nz");

        if (!emailService.sendHTMLMail(email,"<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<style>" + 
                "a{background-color: #3C6E71;color: white;padding: 10px 15px;text-align: center;text-decoration:none;}" + 
                "a:hover{cursor: pointer;background-color: #325C5F;}" + 
                "p{margin-top: 20px;margin-bottom: 20px;}" + 
                "</style>" + 
                "<h2>Password Reset</h2>"+
                "<p>Someone has requested a password reset for your account. Please click the link below to reset your password.</p>"+
                "<a href='"+baseUrl+"/resetPassword?token="+token.getToken()+"'><b>Reset your password</b></a>"+
                "</html>","Reset TAB Password")) {
            return false;
        }
        service.saveForPasswordUpdate(token);
        return true;
    }

    /**
     * Checks whether a given token is valid
     * @param token the user's token
     * @return if the user's token is valid
     * */
    public boolean checkToken(String token) {
        TokenGenerator regToken = service.findByToken(token);
        if (regToken == null || !"resetPassword".equals(regToken.getTokenType())) {
            return false;
        }
        if (regToken.isToOld()) {
            service.delete(regToken);
            return false;
        }
        return true;
    }



}

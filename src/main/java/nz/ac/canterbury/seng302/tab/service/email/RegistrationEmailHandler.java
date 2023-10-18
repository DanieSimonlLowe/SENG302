package nz.ac.canterbury.seng302.tab.service.email;

import nz.ac.canterbury.seng302.tab.entity.FeedAlerts;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.TokenGenerator;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.LocationService;
import nz.ac.canterbury.seng302.tab.service.TokenService;
import nz.ac.canterbury.seng302.tab.service.UserAlertsService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * The handler for the registration emails.
 * 
 * @author  Daniel Lowe
 * @version 1.0.0, April 23 
 */
@Service
public class RegistrationEmailHandler {

    /** The logger */
    Logger logger = LoggerFactory.getLogger(RegistrationEmailHandler.class);

    /** The registration token service */
    private final TokenService service;

    /** The user service */
    private final UserService userService;

    /** The email service */
    private final EmailService emailService;

    /** The location service */
    private final LocationService locationService;

    private final String baseUrl;

    private final UserAlertsService userAlertsService;


    /**
     * Constructor for the RegistrationEmailHandler
     * @param service         the registration token service
     * @param userService     the user service
     * @param emailService    the email service
     * @param locationService the location service
     */
    @Autowired
    public RegistrationEmailHandler(TokenService service,UserService userService, EmailService emailService, LocationService locationService, Environment env, UserAlertsService userAlertsService) {
        this.service = service;
        this.userService = userService;
        this.emailService = emailService;
        this.locationService = locationService;
        this.baseUrl = env.getProperty("tab.baseURL");
        this.userAlertsService = userAlertsService;
        logger.info("base url: "+baseUrl);
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
    public boolean setUpRegistration(String email, String firstName, String lastName, LocalDate date, Location location, String password) {
        TokenGenerator token = new TokenGenerator(email, firstName, lastName, date, location, password, "register");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@TAB.co.nz");

        if (!emailService.sendHTMLMail(email,"<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<style>" + 
                "a{background-color: #3C6E71;color: white;padding: 10px 15px;text-align: center;text-decoration:none;}" + 
                "a:hover{cursor: pointer;background-color: #325C5F;}" + 
                "p{margin-top: 20px;margin-bottom: 20px;}" + 
                "</style>" + 
                "<h2>Confirm your email address to get started with TAB.</h2>"+
                "<p>Once you've confirmed that <b>"+email+"</b> is your email address, you'll be registered in our system and redirected to our login page.</p>"+
                "<a href='"+baseUrl+"/confirmUser?token="+token.getToken()+"'><b>Confirm Email Address</b></a>"+
                "<p>If you don't expect this email, you can safely ignore it.</p>" +
                "</html>","TAB Registration Token")) {
            return false;
        }
        service.save(token);
        return true;
    }

    /**
     * Registers a user with the correct token.
     * @param token the user's token
     * @return if the user was successfully logged in.
     * */
    public boolean registerUser(String token) {
        TokenGenerator regToken = service.findByToken(token);
        if (regToken == null || !"register".equals(regToken.getTokenType())) {
            return false;
        }
        if (regToken.isToOld()) {
            locationService.delete(regToken.getLocation());
            service.delete(regToken);
            return false;
        }
        User user = regToken.generateUser();
        if (!emailService.sendHTMLMail(user.getEmail(), """
                <!DOCTYPE html>
                <html lang="en">
                <style>p{margin-top: 20px;margin-bottom: 20px;}</style><h3>Your account was successfully registered!</h3><p>You're now fully registered with TAB, and can login.</p></html>""","TAB Registration has been added")) {
            return false;
        }

        try {
            service.delete(regToken);
            userService.save(user);
            userAlertsService.save(new FeedAlerts(user.getId()));
        } catch (ObjectOptimisticLockingFailureException e) {
            logger.warn("Token was already deleted");
        }

        return true;
    }


    /**
     * generates a token so that the user can update them self, and sends a confirmation email to the users email, so they can be updated.
     * @param user the user that is being updated.
     * @return if the email was successfully sent.
     * */
    public boolean updateUser(User user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@TAB.co.nz");
        user.deactivate();
        if (!emailService.sendHTMLMail(user.getEmail(),"<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<style>" +
                "a{background-color: #3C6E71;color: white;padding: 10px 15px;text-align: center;text-decoration:none;}" +
                "a:hover{cursor: pointer;background-color: #325C5F;}" +
                "p{margin-top: 20px;margin-bottom: 20px;}" +
                "</style>" +
                "<h2>Confirm your new email address.</h2>"+
                "<p>Once you've confirmed that <b>"+user.getEmail()+"</b> is your email address, your account will be updated and you'll be redirected to our login page.</p>"+
                "<a href='"+baseUrl+"/updateUser?token="+user.getUpdateToken()+"'><b>Confirm Email Address</b></a>"+
                "<p>If you don't expect this email, you can safely ignore it.</p>"+
                "</html>","TAB Registration Token")) {
            return false;
        }
        userService.save(user);
        return true;
    }
}

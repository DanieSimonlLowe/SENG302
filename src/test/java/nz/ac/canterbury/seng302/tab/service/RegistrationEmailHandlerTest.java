package nz.ac.canterbury.seng302.tab.service;

import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.TokenGenerator;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.LocationRepository;
import nz.ac.canterbury.seng302.tab.repository.TokenRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.email.EmailService;
import nz.ac.canterbury.seng302.tab.service.email.RegistrationEmailHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.test.context.support.WithAnonymousUser;

import java.time.Instant;
import java.time.LocalDate;

@SpringBootTest
@Transactional
@WithAnonymousUser
public class RegistrationEmailHandlerTest {

    RegistrationEmailHandler emailHandler;

    @Resource
    TokenRepository registrationTokenRepository;

    @Resource
    LocationRepository locationRepository;

    @Resource
    UserRepository userRepository;

    static MockHttpServletRequest request;

    @Resource
    TokenService service;

    @Resource
    UserAlertsService userAlertsService;

    @Resource
    UserService userService;

    @Resource
    LocationService locationService;

    @Resource
    Environment env;

    @BeforeAll
    static void setup() {
        request = new MockHttpServletRequest();
    }

    EmailService emailService;
    @BeforeEach
    void before() {
        emailService = Mockito.mock(EmailService.class);

        emailHandler = new RegistrationEmailHandler(service,userService,emailService,locationService,env, userAlertsService);
    }

    void setEmailReturn(boolean bool) {
        Mockito.when(emailService.sendHTMLMail(Mockito.anyString(),Mockito.anyString(),Mockito.anyString())).thenReturn(bool);
    }

    @Test
    void setUpRegistration_validEmail() {
        setEmailReturn(true);
        LocalDate date = LocalDate.now();
        Location location = new Location("", "", "", "", "a","b");
        locationRepository.save(location);
        boolean sendEmail = emailHandler.setUpRegistration("dlo54@uclive.ac.nz", "dan", "low", date, location, "pas");
        Assertions.assertTrue(sendEmail);
        TokenGenerator token = registrationTokenRepository.findByEmailIgnoreCase("dlo54@uclive.ac.nz");
        Assertions.assertEquals(token.getFirstName(),"dan");
        Assertions.assertEquals(token.getLastName(),"low");
        Assertions.assertEquals(token.getDate(),date);
        Assertions.assertEquals(token.getLocation(),location);
    }

    @Test
    void setUpRegistration_invalidEmail() {
        setEmailReturn(false);
        LocalDate date = LocalDate.now();
        Location location = new Location("", "", "", "", "a","b");
        locationRepository.save(location);
        boolean sendEmail = emailHandler.setUpRegistration("asddsadsdasadsadsafdsagfd1234","dan","low", date, location,"pas");
        Assertions.assertFalse(sendEmail);
        TokenGenerator token = registrationTokenRepository.findByEmailIgnoreCase("asddsadsdasadsadsafdsagfd1234");
        Assertions.assertNull(token);
    }

    @Test
    void registerUser_ValidToken() {
        setEmailReturn(true);
        LocalDate date = LocalDate.now();
        Location location = new Location("", "", "", "", "a","b");
        locationRepository.save(location);
        emailHandler.setUpRegistration("dlo54@uclive.ac.nz","dan","low", date, location,"pas");
        TokenGenerator token = registrationTokenRepository.findByEmailIgnoreCase("dlo54@uclive.ac.nz");
        emailHandler.setUpRegistration("dlo54@uclive.ac.nz","dan","low", date, location,"pas");
        boolean hasSuc = emailHandler.registerUser(token.getToken());
        Assertions.assertTrue(hasSuc);
        User user = userRepository.findByEmailIgnoreCase("dlo54@uclive.ac.nz");
        Assertions.assertEquals(user.getFirstName(),"dan");
        Assertions.assertEquals(user.getLastName(),"low");
        Assertions.assertEquals(user.getDate(),date);
        Assertions.assertEquals(user.getLocation(),location);

    }

    @Test
    void registerUser_InvalidToken() {
        setEmailReturn(false);
        boolean hasSuc = emailHandler.registerUser("not token");
        Assertions.assertFalse(hasSuc);
    }

    @Test
    void registerUser_TokenToOld() {
        setEmailReturn(true);
        LocalDate date = LocalDate.now();
        Location location = new Location("", "", "", "", "a","b");
        locationRepository.save(location);
        emailHandler.setUpRegistration("dlo54@uclive.ac.nz","dan","low", date, location,"pas");
        TokenGenerator token = registrationTokenRepository.findByEmailIgnoreCase("dlo54@uclive.ac.nz");
        token.setTimeStamp(Instant.now().toEpochMilli()-token.getLifeSpan());

        boolean hasSuc = emailHandler.registerUser(token.getToken());
        Assertions.assertFalse(hasSuc);
        User user = userRepository.findByEmailIgnoreCase("dlo54@uclive.ac.nz");
        Assertions.assertNull(user);

        TokenGenerator currToken = registrationTokenRepository.findByEmailIgnoreCase("dlo54@uclive.ac.nz");
        Assertions.assertNull(currToken);
    }

    @Test
    void updateUser_validEmail() {
        setEmailReturn(true);
        LocalDate date = LocalDate.now();
        Location location = new Location("", "", "", "", "a","b");
        locationRepository.save(location);
        User user1 =  new TokenGenerator("dlo54@uclive.ac.nz","dan","low", date, location,"pas", "register").generateUser();
        boolean sendEmail = emailHandler.updateUser(user1);
        Assertions.assertTrue(sendEmail);

        User user2 = userRepository.findByEmailIgnoreCase("dlo54@uclive.ac.nz");
        Assertions.assertNotNull(user2);
        Assertions.assertFalse(user2.getIsActive());
    }

    @Test
    void updateUser_invalidEmail() {
        setEmailReturn(false);
        LocalDate date = LocalDate.now();
        Location location = new Location("", "", "", "", "a","b");
        locationRepository.save(location);
        User user1 =  new TokenGenerator("dlo54uclive.ac.nz","dan","low", date, location,"pas","register").generateUser();
        boolean sendEmail = emailHandler.updateUser(user1);
        Assertions.assertFalse(sendEmail);

        User user2 = userRepository.findByEmailIgnoreCase("dlo54uclive.ac.nz");
        Assertions.assertNull(user2);
    }
}
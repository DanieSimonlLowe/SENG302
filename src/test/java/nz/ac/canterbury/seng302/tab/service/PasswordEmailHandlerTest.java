package nz.ac.canterbury.seng302.tab.service;

import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.TokenGenerator;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.email.EmailService;
import nz.ac.canterbury.seng302.tab.service.email.PasswordEmailHandler;
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

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@Transactional
@WithAnonymousUser
class PasswordEmailHandlerTest {

    PasswordEmailHandler passwordEmailHandler;


    User user;

    Location location;

    LocalDate date;

    static MockHttpServletRequest request;


    TokenService tokenService;


    EmailService emailService;

    @BeforeAll
    static void setup() {
        request = new MockHttpServletRequest();

    }

    void setEmailReturn(boolean bool) {
        Mockito.when(emailService.sendHTMLMail(Mockito.anyString(),Mockito.anyString(),Mockito.anyString())).thenReturn(bool);
    }

    void setReturnToken(TokenGenerator token) {
        Mockito.when(tokenService.findByToken(any())).thenReturn(token);
    }

    @BeforeEach
    void setupCreation() {
        location = new Location("", "", "", "", "Wellington","NZ");
        date  = LocalDate.now();
        user = new TokenGenerator("email","first","last", LocalDate.now(), location, "password1", "register").generateUser();
        emailService = Mockito.mock(EmailService.class);
        tokenService = Mockito.mock(TokenService.class);
        Environment env = Mockito.mock(Environment.class);
        Mockito.when(env.getProperty("tab.baseURL")).thenReturn("base");

        passwordEmailHandler = new PasswordEmailHandler(tokenService,emailService, env);
    }



    @Test
    void resetPasswordLink_validEmail_sendEmail() {
        setEmailReturn(true);
        boolean emailSent = passwordEmailHandler.sendResetLink("cal135@email.ac.nz", "Celia", "Allen", date, location, "password1");
        Assertions.assertTrue(emailSent);
        Mockito.verify(tokenService,Mockito.times(1)).saveForPasswordUpdate(any());
    }


    @Test
    void resetPasswordLink_invalidEmail_noEmail() {
        setEmailReturn(false);
        boolean emailSent = passwordEmailHandler.sendResetLink("unknownEmailTest@email.com","Celia","Allen", date, location,"password1");
        Assertions.assertFalse(emailSent);
        Mockito.verify(tokenService,Mockito.times(0)).saveForPasswordUpdate(any());
    }


    @Test
    void resetPasswordLink_validToken() {
        TokenGenerator token = new TokenGenerator("email", "firstName", "lastName", date, location, "password", "resetPassword");
        setReturnToken(token);
        boolean validToken = passwordEmailHandler.checkToken(token.getToken());
        Assertions.assertTrue(validToken);
    }

    @Test
    void resetPasswordLink_InvalidToken() {
        setReturnToken(null);
        boolean validToken = passwordEmailHandler.checkToken("not token");
        Assertions.assertFalse(validToken);
    }

    @Test
    void resetPasswordLink_expiredToken_noPasswordUpdate() {

        TokenGenerator token = new TokenGenerator("email", "firstName", "lastName", date, location, "password", "resetPassword");
        token.setTimeStamp(Instant.now().toEpochMilli()- token.getLifeSpan()-1);
        setReturnToken(token);
        boolean validToken = passwordEmailHandler.checkToken(token.getToken());
        Assertions.assertFalse(validToken);


    }



    @Test
    void checkValid_ValidNewPassword_SuccessfulUpdate() {
        Assertions.assertEquals("true", user.resetPasswordDetails("test123!!", "test123!!"));
        Assertions.assertFalse(user.samePassword("password1"));
        Assertions.assertTrue(user.samePassword("test123!!"));
    }


    @Test
    void checkValid_invalidPass_noPassUpdate() {
        Assertions.assertEquals("invalid", user.resetPasswordDetails("newPass", "newPass"));
        Assertions.assertFalse(user.samePassword("newPass"));
        Assertions.assertTrue(user.samePassword("password1"));
    }

    @Test
    void checkValid_diffNewPass_noPassUpdate() {
        Assertions.assertEquals("noMatch", user.resetPasswordDetails("passwordTest#1", "passwordTest#2"));
    }

}


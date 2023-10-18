package nz.ac.canterbury.seng302.tab.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;

public class TokenGeneratorTest {

    @Test
    public void isToOld_New_resetPassword_TokenTest_() {
        TokenGenerator tokenGenerator = new TokenGenerator("", "", "", LocalDate.now(), null, "password", "resetPassword");
        Assertions.assertFalse(tokenGenerator.isToOld());
    }

    @Test
    public void isToOld_allMostOld_resetPassword_TokenTest_() {
        TokenGenerator tokenGenerator = new TokenGenerator("", "", "", LocalDate.now(), null, "password", "resetPassword");
        tokenGenerator.setTimeStamp(Instant.now().toEpochMilli() - 3600000L);
        Assertions.assertFalse(tokenGenerator.isToOld());
    }

    @Test
    public void isToOld_JustOld_resetPassword_TokenTest_() {
        TokenGenerator tokenGenerator = new TokenGenerator("", "", "", LocalDate.now(), null, "password", "resetPassword");
        tokenGenerator.setTimeStamp(Instant.now().toEpochMilli() - 3600001L);
        Assertions.assertTrue(tokenGenerator.isToOld());
    }

    @Test
    public void isToOld_New_Register_TokenTest_() {
        TokenGenerator tokenGenerator = new TokenGenerator("", "", "", LocalDate.now(), null, "password", "register");
        Assertions.assertFalse(tokenGenerator.isToOld());
    }

    @Test
    public void isToOld_allMostOld_Register_TokenTest_() {
        TokenGenerator tokenGenerator = new TokenGenerator("", "", "", LocalDate.now(), null, "password", "register");
        tokenGenerator.setTimeStamp(Instant.now().toEpochMilli() - 7200000L);
        Assertions.assertFalse(tokenGenerator.isToOld());
    }

    @Test
    public void isToOld_JustOld_Register_TokenTest_() {
        TokenGenerator tokenGenerator = new TokenGenerator("", "", "", LocalDate.now(), null, "password", "register");
        tokenGenerator.setTimeStamp( Instant.now().toEpochMilli() - 7200001L);
        Assertions.assertTrue(tokenGenerator.isToOld());
    }
}

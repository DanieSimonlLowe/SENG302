package nz.ac.canterbury.seng302.tab.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.stream.Stream;

public class UpdatePasswordTest {
    String oldPassword;
    User user;

    private static Stream<Arguments> provideStringsForDiffRetypedPassword() {
        return Stream.of(
                Arguments.of("test1234!!","test1235!!"),
                Arguments.of("bigboy25!", "smallboy14!"),
                Arguments.of("", "notnull123!")
        );
    }
    @BeforeEach
    void createUserDetails() {
        oldPassword = "test123!";
        user = new TokenGenerator("email","first","last", LocalDate.now(),new Location("", "", "", "", "city","country"),oldPassword, "register").generateUser();
    }
    @ParameterizedTest
    @MethodSource("provideStringsForDiffRetypedPassword")
    void checkDetails_BadDetails_diffRetypedPassword(String retypedPass1, String retypedPass2) {
        Assertions.assertEquals(user.updatePasswordDetails("test123!",retypedPass1, retypedPass2),"noMatch");
    }

    @ParameterizedTest
    @ValueSource(strings = {"password", "test123!email"})
    void checkDetails_BadDetails_InvalidNewPassword(String newPass) {
        Assertions.assertEquals(user.updatePasswordDetails("test123!", newPass, newPass),"invalid");
    }

    @Test
    void checkDetails_ValidDetails_SuccessfulUpdate() {
        Assertions.assertEquals(user.updatePasswordDetails( "test123!", "test123!!", "test123!!"), "true");
        Assertions.assertFalse( user.samePassword(oldPassword));
        Assertions.assertTrue( user.samePassword("test123!!"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"wrongpassword123!", "differentpassword123!", "", "\uD83E\uDD56", "heythere\uD83D\uDE03"})
    void inputtingWrongOldPassword_updatingPassword_errorMessageShow(String testPassword) {
        Assertions.assertEquals(user.updatePasswordDetails( testPassword, "test123!!", "test123!!"), "incorrect");
        Assertions.assertTrue( user.samePassword(oldPassword));
    }

}

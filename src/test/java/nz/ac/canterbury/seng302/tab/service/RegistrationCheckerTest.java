package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.service.checkers.RegistrationChecker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

public class RegistrationCheckerTest {

    @ParameterizedTest
    @ValueSource(strings = {"abc","Z","Tom","Barthelmeh","âÆÇÑŒ","d-d","A z Z a"})
    void normalName(String password) {
        Assertions.assertTrue(RegistrationChecker.isValidName(password));
    }

    @ParameterizedTest
    @ValueSource(strings = {""," - ","- -","-"})
    void emptyName(String password) {
        Assertions.assertFalse(RegistrationChecker.isValidName(password));
    }


    @ParameterizedTest
    @ValueSource(strings = {"_","=","<","?",";","2"})
    void invalidCharName(String password) {
        Assertions.assertFalse(RegistrationChecker.isValidName(password));
    }

    @ParameterizedTest
    @ValueSource(strings = {"av@gmail.com","tester.project@company.com","A12V@lamplamr.nz","A12V_asd'asd'@maze.ru"})
    void normalEmail(String email) {
        Assertions.assertTrue(RegistrationChecker.isValidEmail(email));
    }

    @ParameterizedTest
    @ValueSource(strings = {""," @ . ","av@g.","avg.com", "avg@com","danielsimonlowe@gmailcom"})
    void invalidEmail(String email) {
        Assertions.assertFalse(RegistrationChecker.isValidEmail(email));
    }

    @ParameterizedTest
    @ValueSource(strings = {"â@g.com","A12V@sdŒ.nz","A12V@sdŒ.ac.nz"})
    void weirdCharEmail(String email) {
        Assertions.assertTrue(RegistrationChecker.isValidEmail(email));
    }


    @Test
    void dateTooYoung() {
        Assertions.assertFalse(RegistrationChecker.isBirthDateValid(LocalDate.now().minusYears(12).minusDays(364)));
        Assertions.assertFalse(RegistrationChecker.isBirthDateValid(LocalDate.now().plusYears(20)));
    }

    @Test
    void dateJustRight() {
        Assertions.assertTrue(RegistrationChecker.isBirthDateValid(LocalDate.now().minusYears(13)));
        Assertions.assertTrue(RegistrationChecker.isBirthDateValid(LocalDate.now().minusYears(13).minusDays(1)));
        LocalDate now = LocalDate.now();
        Assertions.assertTrue(RegistrationChecker.isBirthDateValid( LocalDate.of(now.getYear()-13,now.getMonth(),now.getDayOfMonth()) ) );
    }

    @Test
    void dateVeryOverMin() {
        Assertions.assertTrue(RegistrationChecker.isBirthDateValid(LocalDate.now().minusYears(20)));
        Assertions.assertTrue(RegistrationChecker.isBirthDateValid(LocalDate.now().minusYears(60)));
        Assertions.assertTrue(RegistrationChecker.isBirthDateValid(LocalDate.now().minusYears(2000)));
    }

    @Test
    void containsOtherFieldsPassword() {
        Assertions.assertFalse(RegistrationChecker.isPasswordValid("stufffluff1?", new String[]{"fluff"}));
        Assertions.assertFalse(RegistrationChecker.isPasswordValid("stufffluff1?", new String[]{"Fluff"}));
        Assertions.assertFalse(RegistrationChecker.isPasswordValid("stufffluff1?", new String[]{"stuff"}));
        Assertions.assertTrue(RegistrationChecker.isPasswordValid("stufffluff1?", new String[]{}));

        Assertions.assertFalse(RegistrationChecker.isPasswordValid("stuffflufffasigsasf1?", new String[]{"dgad","fgas","fashh","ffa"}));
        Assertions.assertFalse(RegistrationChecker.isPasswordValid("stuffflufffasigsasf1?", new String[]{"dgad","fgas","fashh","ffa","sadasd"}));
        Assertions.assertTrue(RegistrationChecker.isPasswordValid("stuffflufffsigsasf1?", new String[]{"dgad","fgas","fashh","ffa","sadasd"}));
    }


    @Test
    void passwordTooShort() {
        Assertions.assertFalse(RegistrationChecker.isPasswordValid("!q1", new String[]{}));
        Assertions.assertTrue(RegistrationChecker.isPasswordValid("!q145678", new String[]{}));
    }

    @Test
    void passwordCharacterTypes() {
        Assertions.assertFalse(RegistrationChecker.isPasswordValid("!1234567", new String[]{}));
        Assertions.assertFalse(RegistrationChecker.isPasswordValid("q1234567", new String[]{}));
        Assertions.assertFalse(RegistrationChecker.isPasswordValid("!qabcdef", new String[]{}));
        Assertions.assertTrue(RegistrationChecker.isPasswordValid("%a=123sda4", new String[]{}));
    }

    @Test
    void changePassword_weakPassword_displayError() {
        Assertions.assertFalse(RegistrationChecker.isPasswordValid("", new String[]{}));
        Assertions.assertFalse(RegistrationChecker.isPasswordValid("password", new String[]{}));
        Assertions.assertFalse(RegistrationChecker.isPasswordValid("NewPassword", new String[]{}));
        Assertions.assertFalse(RegistrationChecker.isPasswordValid("Monkeys72", new String[]{}));
        Assertions.assertFalse(RegistrationChecker.isPasswordValid("!Unicorn", new String[]{}));
        Assertions.assertFalse(RegistrationChecker.isPasswordValid("My Secure Password.", new String[]{}));
    }

    @Test
    void isLocationValid_OnlyNess() {
        Assertions.assertTrue(RegistrationChecker.isLocationValid("2 Homestead Ln, Ilam, Christchurch 8041, New Zealand", "", "","1234","curt","NZ"));
    }

    @Test
    void isLocationValid_NoAdress1() {
        Assertions.assertTrue(RegistrationChecker.isLocationValid("", "", "","1234","curt","NZ"));
    }

    @Test
    void isLocationValid_NoCity() {
        Assertions.assertFalse(RegistrationChecker.isLocationValid("68b whyndham", "", "","1234","","NZ"));
    }

    @Test
    void isLocationValid_NoPostCode() {
        Assertions.assertTrue(RegistrationChecker.isLocationValid("68b whyndham", "", "","","curt","NZ"));
    }


    @ParameterizedTest
    @ValueSource(strings = {"A-","-","--","_",">","-A","123-L12-"})
    void isLocationValid_InvalidPostCode(String postcode) {
        Assertions.assertFalse(RegistrationChecker.isLocationValid("68b whyndham", "", "",postcode,"curt","NZ"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1-1","1","123456789","123-123-123","111A","A1A","21343149852765429843764529876954665438765436"})
    void isLocationValid_ValidPostCode(String postcode) {
        Assertions.assertTrue(RegistrationChecker.isLocationValid("68b whyndham", "", "",postcode,"curt","NZ"));
    }

    @Test
    void isLocationValid_NoCountry() {
        Assertions.assertFalse(RegistrationChecker.isLocationValid("68b whyndham", "", "","1234","curt",""));
    }

    @ParameterizedTest
    @ValueSource(strings = {"12 macklarn", "2 Homestead Ln, Ilam, Christchurch 8041, New Zealand", "あ , あ", "a", "12 st."})
    void isLocationValid_Address1_valid(String address1) {
        Assertions.assertTrue(RegistrationChecker.isLocationValid(address1, "", "","1234","curt","NZ"));
    }

    @ParameterizedTest
    @ValueSource(strings = {" , ", " . ", "12", "-", "!"})
    void isLocationValid_Address1_invalid(String address1) {
        Assertions.assertFalse(RegistrationChecker.isLocationValid(address1, "", "","1234","curt","NZ"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"12 macklarn", "2 Homestead Ln, Ilam, Christchurch 8041, New Zealand", "あ , あ", "a", "12 st."})
    void isLocationValid_Address2_valid(String address2) {
        Assertions.assertTrue(RegistrationChecker.isLocationValid("12 macklarn", address2, "","1234","curt","NZ"));
    }

    @ParameterizedTest
    @ValueSource(strings = { " , ", " . ", "12", "-", "!"})
    void isLocationValid_Address2_invalid(String address2) {
        Assertions.assertFalse(RegistrationChecker.isLocationValid("12 macklarn", address2, "","1234","curt","NZ"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"macklarn", "Homestead Ln.", "あsuburb", "a", "sd st.", "asd, sd"})
    void isLocationValid_suburb_valid(String suburb) {
        Assertions.assertTrue(RegistrationChecker.isLocationValid("12 macklarn", "", suburb,"1234","curt","NZ"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"!", " , ", " . ", "12", "-", "!", "as!"})
    void isLocationValid_suburb_invalid(String suburb) {
        Assertions.assertFalse(RegistrationChecker.isLocationValid("12 macklarn", "", suburb,"1234","curt","NZ"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"macklarn", "Homestead Ln.", "あsuburb", "a", "sd st.", "asd, sd", "Quận 12"})
    void isLocationValid_City_valid(String city) {
        Assertions.assertTrue(RegistrationChecker.isLocationValid("12 macklarn", "", "","1234",city,"NZ"));
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "!", " , ", " . ", "12", "-", "!", "as!"})
    void isLocationValid_city_invalid(String city) {
        Assertions.assertFalse(RegistrationChecker.isLocationValid("12 macklarn", "", "","1234",city,"NZ"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"macklarn", "Homestead Ln.", "あsuburb", "a", "sd st.", "asd, sd"})
    void isLocationValid_country_valid(String country) {
        Assertions.assertTrue(RegistrationChecker.isLocationValid("12 macklarn", "", "","1234","city",country));
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "!", " , ", " . ", "12", "-", "!", "12 asd", "asd123", "as!"})
    void isLocationValid_country_invalid(String country) {
        Assertions.assertFalse(RegistrationChecker.isLocationValid("12 macklarn", "", "","1234","city",country));
    }

    //Starts the part tests.


    @Test
    void locationInvalidPartNum_OnlyNess() {
        Assertions.assertEquals(6,RegistrationChecker.locationInvalidPartNum("2 Homestead Ln, Ilam, Christchurch 8041, New Zealand", "", "","1234","curt","NZ"));
    }

    @Test
    void locationInvalidPartNum_NoAdress1() {
        Assertions.assertEquals(6,RegistrationChecker.locationInvalidPartNum("", "", "","1234","curt","NZ"));
    }

    @Test
    void locationInvalidPartNum_NoCity() {
        Assertions.assertEquals(5,RegistrationChecker.locationInvalidPartNum("68b whyndham", "", "","1234","","NZ"));
    }

    @Test
    void locationInvalidPartNum_NoPostCode() {
        Assertions.assertEquals(6,RegistrationChecker.locationInvalidPartNum("68b whyndham", "", "","","curt","NZ"));
    }

    @Test
    void locationInvalidPartNum_NoCountry() {
        Assertions.assertEquals(6,RegistrationChecker.locationInvalidPartNum("68b whyndham", "", "","1234","curt",""));
    }

    @ParameterizedTest
    @ValueSource(strings = {"12n macklarn", "2 Homestead Ln, Ilam, Christchurch 8041, New Zealand", "あ , あ", "a", "12 st."})
    void locationInvalidPartNum_Address1_valid(String address1) {
        Assertions.assertEquals(6,RegistrationChecker.locationInvalidPartNum(address1, "", "","1234","curt","NZ"));
    }

    @ParameterizedTest
    @ValueSource(strings = {" , ", " . ", "12", "-", "!"})
    void locationInvalidPartNum_Address1_invalid(String address1) {
        Assertions.assertEquals(1,RegistrationChecker.locationInvalidPartNum(address1, "", "","1234","curt","NZ"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"12n macklarn", "2 Homestead Ln, Ilam, Christchurch 8041, New Zealand", "あ , あ", "a", "12 st."})
    void locationInvalidPartNum_Address2_valid(String address2) {
        Assertions.assertEquals(6,RegistrationChecker.locationInvalidPartNum("12 macklarn", address2, "","1234","curt","NZ"));
    }

    @ParameterizedTest
    @ValueSource(strings = {" , ", " . ", "12", "-", "!"})
    void locationInvalidPartNum_Address2_invalid(String address2) {
        Assertions.assertEquals(2,RegistrationChecker.locationInvalidPartNum("12 macklarn", address2, "","1234","curt","NZ"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"macklarn", "Homestead Ln.", "あsuburb", "a", "sd st.", "asd, sd", "PZRA-1 SNT"})
    void locationInvalidPartNum_suburb_valid(String suburb) {
        Assertions.assertEquals(6,RegistrationChecker.locationInvalidPartNum("12 macklarn", "", suburb,"1234","curt","NZ"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"!", " , ", " . ", "12", "-", "!", "as!"})
    void locationInvalidPartNum_suburb_invalid(String suburb) {
        Assertions.assertEquals(3,RegistrationChecker.locationInvalidPartNum("12 macklarn", "", suburb,"1234","curt","NZ"));
    }



    @ParameterizedTest
    @ValueSource(strings = {"macklarn", "Homestead Ln.", "あsuburb", "a", "sd st.", "asd, sd", "Nayoro-shi", "Ejura/Sekyedumase"})
    void locationInvalidPartNum_City_valid(String city) {
        Assertions.assertEquals(6,RegistrationChecker.locationInvalidPartNum("12 macklarn", "", "","1234",city,"NZ"));
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "!", " , ", " . ", "12", "-", "!", "as!"})
    void locationInvalidPartNum_city_invalid(String city) {
        Assertions.assertEquals(5,RegistrationChecker.locationInvalidPartNum("12 macklarn", "", "","1234",city,"NZ"));
    }

}

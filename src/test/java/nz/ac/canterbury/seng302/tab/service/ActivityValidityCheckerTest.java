package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.service.checkers.ActivityValidityChecker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static nz.ac.canterbury.seng302.tab.service.checkers.ActivityValidityChecker.*;

public class ActivityValidityCheckerTest {

    @Test
    void isValidType_TypeNotExist() {
        String invalidType = "notAnActivity";
        Assertions.assertFalse(isValidType(invalidType, null));
    }

    @Test
    void isValidType_FriendlyNoTeam() {
        String Friendly = "friendly";
        Assertions.assertFalse(isValidType(Friendly, null));
    }

    @Test
    void isValidType_GameNoTeam() {
        String type = "game";
        Assertions.assertFalse(isValidType(type, null));
    }

    @Test
    void isValidType_FriendlyANDTeam() {
        String type = "friendly";
        Location location = new Location("apart1","apart1","suburb", "1051", "Chch", "NZ");
        Team team = new Team("name", location, "sport");
        Assertions.assertTrue(isValidType(type, team));
    }

    @Test
    void isValidType_GameANDTeam() {
        String Game = "game";
        Location location = new Location("apart1","apart1","suburb", "1051", "Chch", "NZ");
        Team team = new Team("name", location, "sport");
        Assertions.assertTrue(isValidType(Game, team));
    }

    @Test
    void isValidType_trainingWeirdCase() {
        String Training = "TraIniNG";
        Assertions.assertTrue(isValidType(Training, null));

    }

    @Test
    void isValidType_otherUpperCase() {
        String other = "OTHER";
        Assertions.assertTrue(isValidType(other, null));
    }

    @Test
    void isValidType_competitionLowerCase() {
        String competition = "competition";
        Assertions.assertTrue(isValidType(competition, null));
    }

    @Test
    void isValidStartEnd_EndBefore() {
        LocalDateTime start = LocalDateTime.of(2020, 3, 17, 7, 30);
        LocalDateTime end = LocalDateTime.of(2017, 7, 15, 11, 22);
        Assertions.assertFalse(isValidStartEnd(start, end));
    }

    @Test
    void isValidStartEnd_Same() {
        LocalDateTime start = LocalDateTime.of(2020, 3, 17, 7, 30);
        Assertions.assertFalse(isValidStartEnd(start, start));
    }

    @Test
    void isValidStartEnd_Valid() {
        LocalDateTime start = LocalDateTime.of(2003, 3, 17, 7, 30);
        LocalDateTime end = LocalDateTime.of(2017, 7, 15, 11, 22);
        Assertions.assertTrue(isValidStartEnd(start, end));
    }

    @Test
    void isValidDescription_Empty() {
        String desc = "";
        Assertions.assertFalse(isValidDescription(desc));
    }

    @Test
    void isValidDescription_Whitespace() {
        String desc = "     ";
        Assertions.assertFalse(isValidDescription(desc));
    }

    @Test
    void isValidDescription_Language() {
        String desc = "das ist hässlisch, おい Спасибо";
        Assertions.assertTrue(isValidDescription(desc));
    }

    @Test
    void isValidDescription_JustNumber() {
        String desc = "84877350";
        Assertions.assertFalse(isValidDescription(desc));
    }

    @Test
    void isValidDescription_JustNonAlpha() {
        String desc = "#^(*#$)@";
        Assertions.assertFalse(isValidDescription(desc));
    }

    @Test
    void isValidDescription_LongString() {
        String desc = "cq2l66SIpTxkXAvWJ06uyst9auvYvRvUn1ZxkwABj7qKTi79TGnVn2kLjECNTEEmvQeXMTwLDFzXqzkeFYQytB5Bqoj1BCtDgDtZAiCQMDD7NWSaeyKLGBsMnqCgkr8bj9ub5ADnwGkBo552JyfqVmf";
        Assertions.assertFalse(isValidDescription(desc));
    }

    @Test
    void isValidDescription_ValidString() {
        String desc = "\"A sentence with valid punctuation,\" she said. \"What a surprise!\"";
        Assertions.assertTrue(isValidDescription(desc));
    }


    @ParameterizedTest
    @ValueSource(strings = {"game", "friendly"})
    public void needsTeamTest_doesNeedTeam(String type) {
        Assertions.assertTrue(ActivityValidityChecker.needsTeam(type));
    }

    @ParameterizedTest
    @ValueSource(strings = {"training", "competition","other","nonTeam"})
    public void needsTeamTest_doesNotNeedTeam(String type) {
        Assertions.assertFalse(ActivityValidityChecker.needsTeam(type));
    }
}

package nz.ac.canterbury.seng302.tab.entity.stats;

import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.ActivityType;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.exceptions.InvalidActivityException;
import nz.ac.canterbury.seng302.tab.exceptions.MismatchGameScoreTypeException;
import nz.ac.canterbury.seng302.tab.repository.ActivityStatRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class GameScoreTest {
    private Location location;
    private User user;

    @BeforeEach
    void setup() {
        location = new Location("", "", "", "", "Memphis", "United States of America");
        user = new User("","","", LocalDate.now(),location,"");
    }


    @ParameterizedTest
    @ValueSource(strings = {"1","2","23123","23","0"})
    void createGameScore_individual_validScore(String score) {
        Activity activity = new Activity(ActivityType.GAME,null, null,"", LocalDateTime.now(),LocalDateTime.now(),user,location);

        Executable executable = () -> {
            GameScore gameScore = new GameScore(activity, score, "1");
        };
        Assertions.assertDoesNotThrow(executable);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1-1","2-512-123-12-3-5-1-23-45-1-2-4-5-6-1-2-3","23123-1","23-9-0-0-01-1-1","0-2-5-12"})
    void createGameScore_multi_validScore(String score) {
        Activity activity = new Activity(ActivityType.FRIENDLY,null, null,"", LocalDateTime.now(),LocalDateTime.now(),user,location);

        Executable executable = () -> {
            GameScore gameScore = new GameScore(activity, score, "1-1");
        };
        Assertions.assertDoesNotThrow(executable);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1a","2-","-23123"})
    void createGameScore_individual_invalidScore(String score) {
        Activity activity = new Activity(ActivityType.GAME,null, null,"", LocalDateTime.now(),LocalDateTime.now(),user,location);

        Executable executable = () -> {
            GameScore gameScore = new GameScore(activity, score, "1");
        };
        Assertions.assertThrowsExactly(NumberFormatException.class,executable);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1a-2","2-=a","-2-3-123","  - ","1                      -1-2-3","1-2-3-4-5a-6-7-8-9","1-2-3-4-5-6-7-8-9a","1a-2-3-4-5-6-7-8-9"})
    void createGameScore_multi_invalidScore(String score) {
        Activity activity = new Activity(ActivityType.GAME,null, null,"", LocalDateTime.now(),LocalDateTime.now(),user,location);

        Executable executable = () -> {
            GameScore gameScore = new GameScore(activity, score, "1-1");
        };
        Assertions.assertThrowsExactly(NumberFormatException.class,executable);
    }

    @Test
    void createGameScore_nullScore() {
        Activity activity = new Activity(ActivityType.GAME,null, null,"", LocalDateTime.now(),LocalDateTime.now(),user,location);

        Executable executable = () -> {
            GameScore gameScore = new GameScore(activity, null, "1-1");
        };
        Assertions.assertThrowsExactly(NullPointerException.class,executable);
    }

    @Test
    void createGameScore_emptyScore() {
        Activity activity = new Activity(ActivityType.GAME,null, null,"", LocalDateTime.now(),LocalDateTime.now(),user,location);

        Executable executable = () -> {
            GameScore gameScore = new GameScore(activity, "                    ", "1-1");
        };
        Assertions.assertThrowsExactly(NullPointerException.class,executable);
    }

    @Test
    void createGameScore_nullActivity() {
        Activity activity = new Activity(ActivityType.GAME,null, null,"", LocalDateTime.now(),LocalDateTime.now(),user,location);

        Executable executable = () -> {
            GameScore gameScore = new GameScore(null, "1-1", "1-1");
        };
        Assertions.assertThrowsExactly(InvalidActivityException.class,executable);
    }

    @Test
    void createGameScore_wrongActivityType() {
        Activity activity = new Activity(ActivityType.OTHER,null, null,"", LocalDateTime.now(),LocalDateTime.now(),user,location);

        Executable executable = () -> {
            GameScore gameScore = new GameScore(activity, "1-1", "1-1");
        };
        Assertions.assertThrowsExactly(InvalidActivityException.class,executable);
    }

    @Test
    void createGameScore_diffScoreTypes() {
        Activity activity = new Activity(ActivityType.GAME,null, null,"", LocalDateTime.now(),LocalDateTime.now(),user,location);

        Executable executable = () -> {
            GameScore gameScore = new GameScore(activity, "1-1", "1");
        };
        Assertions.assertThrowsExactly(MismatchGameScoreTypeException.class,executable);
    }

    @Test
    void canSave_test() {
        Activity activity = Mockito.mock(Activity.class);
        Mockito.when(activity.getType()).thenReturn(ActivityType.GAME);
        GameScore gameScore1 = new GameScore(activity,"1","2");
        ActivityStatRepository activityStatRepository = Mockito.mock(ActivityStatRepository.class);

        Assertions.assertFalse(gameScore1.canSave(activityStatRepository));
    }


    @Test
    void setScores_test() {
        Activity activity = Mockito.mock(Activity.class);
        Mockito.when(activity.getType()).thenReturn(ActivityType.GAME);
        GameScore gameScore1 = new GameScore(activity,"1","2");
        GameScore gameScore2 = new GameScore(activity, "3", "4");

        gameScore1.setScores(gameScore2);
        Assertions.assertEquals("3",gameScore1.getScore1());
        Assertions.assertEquals("4",gameScore1.getScore2());

    }
}

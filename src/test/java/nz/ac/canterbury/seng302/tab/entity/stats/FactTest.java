package nz.ac.canterbury.seng302.tab.entity.stats;

import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.ActivityType;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.exceptions.InvalidActivityException;
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

public class FactTest {
    private Location location;
    private User user;

    @BeforeEach
    void setup() {
        location = new Location("", "", "", "", "Memphis", "United States of America");
        user = new User("","","", LocalDate.now(),location,"");
    }

    @ParameterizedTest
    @ValueSource(strings = {"a","asfdfadgafgh asdf12 !@#", " a '"})
    void createFact_validDesc(String desc) {
        Activity activity = new Activity(ActivityType.GAME,null, null,"", LocalDateTime.now(),LocalDateTime.now(),user,location);

        Executable executable = () -> {
            Fact fact = new Fact(activity, desc, null);
        };
        Assertions.assertDoesNotThrow(executable);
    }

    @ParameterizedTest
    @ValueSource(strings = {"","    ", " "})
    void createFact_invalidDesc(String desc) {
        Activity activity = new Activity(ActivityType.GAME,null, null,"", LocalDateTime.now(),LocalDateTime.now(),user,location);

        Executable executable = () -> {
            Fact fact = new Fact(activity, desc, null);
        };
        Assertions.assertThrowsExactly(NullPointerException.class,executable);
    }

    @Test
    void createFact_noDesc() {
        Activity activity = new Activity(ActivityType.GAME,null, null,"", LocalDateTime.now(),LocalDateTime.now(),user,location);

        Executable executable = () -> {
            Fact fact = new Fact(activity, null, null);
        };
        Assertions.assertThrowsExactly(NullPointerException.class,executable);
    }

    @Test
    void createFact_invalidActivity() {
        Activity activity = new Activity(ActivityType.TRAINING,null, null,"", LocalDateTime.now(),LocalDateTime.now(),user,location);

        Executable executable = () -> {
            Fact fact = new Fact(activity, "null", null);
        };
        Assertions.assertThrowsExactly(InvalidActivityException.class,executable);
    }

    @Test
    void createFact_noActivity() {
        Executable executable = () -> {
            Fact fact = new Fact(null, "null", null);
        };
        Assertions.assertThrowsExactly(InvalidActivityException.class,executable);
    }

//    @Test
//    void createFact_dateOutRange() {
//        Activity activity = Mockito.mock(Activity.class);
//        Mockito.when(activity.getType()).thenReturn(ActivityType.GAME);
//        Mockito.when(activity.isInTimeRange(Mockito.any())).thenReturn(false);
//        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
//        String formattedTime = activity.getEndDate().format(dateTimeFormatter);
//
//        Executable executable = () -> {
//            Fact fact = new Fact(activity, "null", "12:12:12");
//        };
//        Assertions.assertThrowsExactly(TimeOutOfRangeException.class,executable);
//    }

//    @Test
//    void createFact_dateInRange() {
//        Activity activity = Mockito.mock(Activity.class);
//        Mockito.when(activity.getType()).thenReturn(ActivityType.GAME);
//        Mockito.when(activity.isInTimeRange(Mockito.any())).thenReturn(true);
////        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
////        String formattedTime = activity.getStartDate().plusSeconds(1).format(dateTimeFormatter);
//
//        Executable executable = () -> {
//            Fact fact = new Fact(activity, "null", "00:00:00");
//        };
//        Assertions.assertDoesNotThrow(executable);
//    }

    @Test
    void canSave_test() {
        Activity activity = Mockito.mock(Activity.class);
        Mockito.when(activity.getType()).thenReturn(ActivityType.GAME);
        Mockito.when(activity.isInTimeRange(Mockito.any())).thenReturn(true);
        Fact fact = new Fact(activity, "null", null);

        ActivityStatRepository activityStatRepository = Mockito.mock(ActivityStatRepository.class);

        Assertions.assertTrue(fact.canSave(activityStatRepository));
    }
}

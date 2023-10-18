package nz.ac.canterbury.seng302.tab.entity.stats;

import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.ActivityType;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.exceptions.InvalidActivityException;
import nz.ac.canterbury.seng302.tab.exceptions.SameUserException;
import nz.ac.canterbury.seng302.tab.exceptions.TimeOutOfRangeException;
import nz.ac.canterbury.seng302.tab.repository.ActivityStatRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;

public class SubstitutedTest {
    private Team team;
    private User user1;
    private User user2;
    private Activity activity;
    private int minute;
    @BeforeEach
    void setup() {
        team = Mockito.mock(Team.class);
        user1 = Mockito.mock(User.class);
        user2 = Mockito.mock(User.class);
        activity = Mockito.mock(Activity.class);
        minute = 10;
    }

    @Test
    void create_nullActivity() {
        Executable executable = () -> {
            Substituted substituted = new Substituted(null,user1,user2,minute,team);
        };
        Assertions.assertThrowsExactly(InvalidActivityException.class ,executable);
    }

    @Test
    void create_nullOldUser() {
        Executable executable = () -> {
            Substituted substituted = new Substituted(activity,null,user2,minute,team);
        };
        Assertions.assertThrowsExactly(NullPointerException.class ,executable);
    }

    @Test
    void create_nullNewUser() {
        Executable executable = () -> {
            Substituted substituted = new Substituted(activity,user1,null,minute,team);
        };
        Assertions.assertThrowsExactly(NullPointerException.class ,executable);
    }

    @Test
    void create_nullTime() {
        Executable executable = () -> {
            Substituted substituted = new Substituted(activity,user1,user2,null,team);
        };
        Assertions.assertThrowsExactly(NullPointerException.class ,executable);
    }

    @Test
    void create_invalidActivityType() {
        Mockito.when(activity.getType()).thenReturn(ActivityType.OTHER);
        Executable executable = () -> {
            Substituted substituted = new Substituted(activity,user1,user2,minute,team);
        };
        Assertions.assertThrowsExactly(InvalidActivityException.class ,executable);
    }

    @Test
    void create_dateOutRange() {
        Mockito.when(activity.getType()).thenReturn(ActivityType.GAME);
        Mockito.when(activity.isInTimeRange(minute)).thenReturn(false);

        Executable executable = () -> {
            Substituted substituted = new Substituted(activity,user1,user2,minute,team);
        };
        Assertions.assertThrowsExactly(TimeOutOfRangeException.class ,executable);
    }

    @Test
    void create_samePlayer() {
        Mockito.when(activity.getType()).thenReturn(ActivityType.GAME);
        Mockito.when(activity.isInTimeRange(minute)).thenReturn(true);
        Mockito.when(user1.getId()).thenReturn(1L);
        Mockito.when(user2.getId()).thenReturn(1L);

        Executable executable = () -> {
            Substituted substituted = new Substituted(activity,user1,user2,minute,team);
        };
        Assertions.assertThrowsExactly(SameUserException.class ,executable);
    }

    @Test
    void create_valid() {
        Mockito.when(activity.getType()).thenReturn(ActivityType.GAME);
        Mockito.when(activity.isInTimeRange(minute)).thenReturn(true);
        Mockito.when(user1.getId()).thenReturn(1L);
        Mockito.when(user2.getId()).thenReturn(2L);

        Executable executable = () -> {
            Substituted substituted = new Substituted(activity,user1,user2,minute,team);
        };
        Assertions.assertDoesNotThrow(executable);
    }

    @Test
    void canSave_test() {
        Mockito.when(activity.getType()).thenReturn(ActivityType.GAME);
        Mockito.when(activity.isInTimeRange(minute)).thenReturn(true);
        Mockito.when(user1.getId()).thenReturn(1L);
        Mockito.when(user2.getId()).thenReturn(2L);

        ActivityStatRepository activityStatRepository = Mockito.mock(ActivityStatRepository.class);
        Substituted substituted = new Substituted(activity,user1,user2,minute,team);

        Assertions.assertTrue(substituted.canSave(activityStatRepository));
    }
}

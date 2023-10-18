//package nz.ac.canterbury.seng302.tab.entity.stats;
//
//import nz.ac.canterbury.seng302.tab.entity.Activity;
//import nz.ac.canterbury.seng302.tab.entity.ActivityType;
//import nz.ac.canterbury.seng302.tab.entity.Location;
//import nz.ac.canterbury.seng302.tab.entity.User;
//import nz.ac.canterbury.seng302.tab.exceptions.InvalidActivityException;
//import nz.ac.canterbury.seng302.tab.exceptions.TimeOutOfRangeException;
//import nz.ac.canterbury.seng302.tab.repository.ActivityStatRepository;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.function.Executable;
//import org.mockito.Mockito;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//
//public class IndividualScoreTest {
//    private Location location;
//    private User user;
//
//    @BeforeEach
//    public void setup() {
//        location = new Location("", "", "", "", "Memphis", "United States of America");
//        user = new User("","","", LocalDate.now(),location,"");
//    }
//
//    @Test
//    public void creation_noActivity_test() {
//
//        Executable executable = () -> {
//            IndividualScore individualScore = new IndividualScore(null,null,null,1f);
//        };
//        Assertions.assertThrowsExactly(InvalidActivityException.class ,executable);
//    }
//
//    @Test
//    public void creation_invalidActivityType_test() {
//        Activity activity = new Activity(ActivityType.TRAINING,null, null,"", LocalDateTime.now(),LocalDateTime.now(),user,location);
//
//        Executable executable = () -> {
//            IndividualScore individualScore = new IndividualScore(activity,null,null,1f);
//        };
//        Assertions.assertThrowsExactly(InvalidActivityException.class ,executable);
//    }
//
//    @Test
//    public void creation_noTime_test() {
//        Activity activity = new Activity(ActivityType.TRAINING,null, null,"", LocalDateTime.now(),LocalDateTime.now(),user,location);
//
//        Executable executable = () -> {
//            IndividualScore individualScore = new IndividualScore(activity,null,user,1f);
//        };
//        Assertions.assertThrowsExactly(InvalidActivityException.class ,executable);
//    }
//
//    @Test
//    public void creation_noUser_test() {
//        Activity activity = new Activity(ActivityType.TRAINING,null, null,"", LocalDateTime.now(),LocalDateTime.now(),user,location);
//
//        Executable executable = () -> {
//            IndividualScore individualScore = new IndividualScore(activity,2,null,1f);
//        };
//        Assertions.assertThrowsExactly(InvalidActivityException.class ,executable);
//    }
//
//    @Test
//    public void creation_valid_test() {
//        Activity activity = new Activity(ActivityType.GAME,null, null,"", LocalDateTime.now(),LocalDateTime.now().plusDays(1),user,location);
//
//        Executable executable = () -> {
//            IndividualScore individualScore = new IndividualScore(activity,2,user,1f);
//        };
//        Assertions.assertDoesNotThrow(executable);
//    }
//
//    @Test
//    public void creation_outOfRange_test() {
//        Activity activity = Mockito.mock(Activity.class);
//        Mockito.when(activity.getType()).thenReturn(ActivityType.GAME);
//        Mockito.when(activity.isInTimeRange(Mockito.any())).thenReturn(false);
//
//        Executable executable = () -> {
//            IndividualScore individualScore = new IndividualScore(activity,10000000,user,1f);
//        };
//        Assertions.assertThrowsExactly(TimeOutOfRangeException.class ,executable);
//    }
//
//
//    @Test
//    public void canSave_test() {
//        Activity activity = Mockito.mock(Activity.class);
//        Mockito.when(activity.getType()).thenReturn(ActivityType.GAME);
//        Mockito.when(activity.isInTimeRange(Mockito.any())).thenReturn(true);
//        IndividualScore individualScore = new IndividualScore(activity,2,user,1f);
//
//        ActivityStatRepository activityStatRepository = Mockito.mock(ActivityStatRepository.class);
//
//        Assertions.assertTrue(individualScore.canSave(activityStatRepository));
//    }
//}

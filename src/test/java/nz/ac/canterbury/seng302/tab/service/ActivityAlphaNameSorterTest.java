package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.service.sorters.ActivityAlphaNameSorter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ActivityAlphaNameSorterTest {
    static ActivityAlphaNameSorter sorter;
    static Activity activity1;
    static Activity activity2;
    static Activity activity3;

    @BeforeAll
    static void setup() {
        sorter = new ActivityAlphaNameSorter();
        Location location = new Location("", "", "", "", "Memphis", "United States of America");
        Team team = new Team("name", location, "sport");
        User user = new User("email", "firstName", "lastName", LocalDate.now(), location, "passwordHash");

        String startTimeEarly = "2016-03-04 11:30";
        String startTimeLate = "2017-03-04 11:30";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime startEarly = LocalDateTime.parse(startTimeEarly, formatter);
        LocalDateTime startLate = LocalDateTime.parse(startTimeLate, formatter);

        activity1 = new Activity(ActivityType.OTHER, team, null, "description1", startEarly, LocalDateTime.now(), user, location);
        activity2 = new Activity(ActivityType.TRAINING, team, null, "description2", startLate, LocalDateTime.now(), user, location);
        activity3 = new Activity(ActivityType.TRAINING, team, null, "description2", startLate, LocalDateTime.now(), user, location);
    }

    @Test
    void diff_start_time_left_lower() {
        Assertions.assertTrue(sorter.compare(activity1, activity2) < 0);
    }

    @Test
    void diff_start_time_right_lower() {
        Assertions.assertTrue(sorter.compare(activity2, activity1) > 0);
    }

    @Test
    void same_start_time_left_lower() {
        Assertions.assertEquals(0, sorter.compare(activity2, activity3));
    }

}
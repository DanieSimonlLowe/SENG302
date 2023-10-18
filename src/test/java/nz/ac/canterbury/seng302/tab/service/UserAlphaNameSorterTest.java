package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.sorters.UserAlphaNameSorter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class UserAlphaNameSorterTest {
    static UserAlphaNameSorter sorter;
    static User user1;
    static User user2;

    @BeforeAll
    static void setup() {
        sorter = new UserAlphaNameSorter();
        user1 = new User("e","a","l", LocalDate.now(),new Location("", "", "", "", "a","v"),"pass");
        user2 = new User("e","a","l", LocalDate.now(),new Location("", "", "", "", "a","v"),"pass");
    }

    @Test
    void diff_first_name_left_lower() {
        user1.setLastName("b");
        user2.setLastName("a");
        Assertions.assertEquals(sorter.compare(user1,user2), 1);
    }

    @Test
    void diff_first_name_right_lower() {
        user1.setLastName("a");
        user2.setLastName("b");
        Assertions.assertEquals(sorter.compare(user1,user2), -1);
    }

    @Test
    void same_first_name_last_name_right_lower() {
        user1.setLastName("a");
        user1.setFirstName("a");
        user2.setLastName("a");
        user2.setFirstName("b");
        Assertions.assertEquals(sorter.compare(user1,user2), -1);
    }

    @Test
    void same_first_name_last_name_left_lower() {
        user1.setLastName("a");
        user1.setFirstName("b");
        user2.setLastName("a");
        user2.setFirstName("a");
        Assertions.assertEquals(sorter.compare(user1,user2), 1);
    }
}
package nz.ac.canterbury.seng302.tab.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NumPostsTest {
    @Test
    void test() {
        Club club = Mockito.mock(Club.class);
        User user = Mockito.mock(User.class);

        NumPosts numPosts = new NumPosts(club, user);

        Assertions.assertEquals(1, numPosts.getCount());
        Assertions.assertEquals(user, numPosts.getUser());
        Assertions.assertEquals(club, numPosts.getClub());
    }

    @Test
    void testIncrement() {
        Club club = Mockito.mock(Club.class);
        User user = Mockito.mock(User.class);

        NumPosts numPosts = new NumPosts(club, user);
        numPosts.increment();

        Assertions.assertEquals(2, numPosts.getCount());
    }
}

package nz.ac.canterbury.seng302.tab.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NumCommentsTest {

    private NumComments numComments;

    private User user;

    private Club club;

    @BeforeEach
    void setup() {
        user = Mockito.mock(User.class);
        club = Mockito.mock(Club.class);
    }

    @Test
    void validNumCommentsTest() {
        numComments = new NumComments(club, user);

        Assertions.assertEquals(1, numComments.getCount());
        Assertions.assertEquals(user, numComments.getUser());
        Assertions.assertEquals(club, numComments.getClub());
    }

    @Test
    void validNumComments_IncrementTest() {
        numComments = new NumComments(club, user);
        numComments.increment();

        Assertions.assertEquals(2, numComments.getCount());
    }

}

package nz.ac.canterbury.seng302.tab.entity;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class LineUpPlayerTest {

    @Test
    void create_test() {
        User user = Mockito.mock(User.class);
        LineUp lineUp = Mockito.mock(LineUp.class);
        LineupPlayer lineupPlayer = new LineupPlayer(user,lineUp,1);
        Assertions.assertEquals(user,lineupPlayer.getUser());
        Assertions.assertEquals(1,lineupPlayer.getPosition());
        Assertions.assertEquals(lineUp,lineupPlayer.getLineUp());
    }

    @Test
    void setPosition() {
        User user = Mockito.mock(User.class);
        LineUp lineUp = Mockito.mock(LineUp.class);
        LineupPlayer lineupPlayer = new LineupPlayer(user,lineUp,1);
        lineupPlayer.setPosition(3);
        Assertions.assertEquals(3,lineupPlayer.getPosition());

    }
}

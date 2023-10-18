package nz.ac.canterbury.seng302.tab.entity;

import nz.ac.canterbury.seng302.tab.entity.stats.Substituted;
import nz.ac.canterbury.seng302.tab.exceptions.InvalidSubstitutionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;


public class LineUpTest {


    @Test
    void createTest() {
        Formation formation = Mockito.mock(Formation.class);
        Activity activity = Mockito.mock(Activity.class);
        LineUp lineUp = new LineUp(formation,activity);

        Assertions.assertEquals(formation,lineUp.getFormation());
        Assertions.assertEquals(activity,lineUp.getActivity());
    }


    @Test
    void applySubstitution_Throws_noUsers() {
        Formation formation = Mockito.mock(Formation.class);
        Activity activity = Mockito.mock(Activity.class);
        LineUp lineUp = new LineUp(formation,activity);

        lineUp.setPlayer(new ArrayList<>());

        Substituted substituted = Mockito.mock(Substituted.class);

        Assertions.assertThrowsExactly(InvalidSubstitutionException.class,() -> lineUp.applySubstitution(substituted));
        Mockito.verify(substituted,Mockito.never()).getOldPlayer();
        Mockito.verify(substituted,Mockito.never()).getNewPlayer();
    }

    @Test
    void applySubstitution_Throws_oldNotExist() {
        Formation formation = Mockito.mock(Formation.class);
        Activity activity = Mockito.mock(Activity.class);
        LineUp lineUp = new LineUp(formation,activity);

        User user1 = Mockito.mock(User.class);
        User user2 = Mockito.mock(User.class);
        Mockito.when(user1.getId()).thenReturn(1L);
        Mockito.when(user2.getId()).thenReturn(2L);

        ArrayList<LineupPlayer> list = new ArrayList<>();
        list.add(Mockito.mock(LineupPlayer.class));
        Mockito.when(list.get(0).getUser()).thenReturn(user1);
        lineUp.setPlayer(list);

        Substituted substituted = Mockito.mock(Substituted.class);
        Mockito.when(substituted.getOldPlayer()).thenReturn(user2);
        Mockito.when(substituted.getNewPlayer()).thenReturn(user1);

        Assertions.assertThrowsExactly(InvalidSubstitutionException.class,() -> lineUp.applySubstitution(substituted));

        Mockito.verify(list.get(0),Mockito.never()).setPosition(Mockito.anyInt());
    }

    @Test
    void applySubstitution_Throws_newNotExist() {
        Formation formation = Mockito.mock(Formation.class);
        Activity activity = Mockito.mock(Activity.class);
        LineUp lineUp = new LineUp(formation,activity);

        User user1 = Mockito.mock(User.class);
        User user2 = Mockito.mock(User.class);
        Mockito.when(user1.getId()).thenReturn(1L);
        Mockito.when(user2.getId()).thenReturn(2L);

        ArrayList<LineupPlayer> list = new ArrayList<>();
        LineupPlayer lineupPlayer = Mockito.mock(LineupPlayer.class);
        list.add(lineupPlayer);
        Mockito.when(lineupPlayer.getUser()).thenReturn(user2);
        lineUp.setPlayer(list);

        Substituted substituted = Mockito.mock(Substituted.class);
        Mockito.when(substituted.getOldPlayer()).thenReturn(user2);
        Mockito.when(substituted.getNewPlayer()).thenReturn(user1);

        Assertions.assertThrowsExactly(InvalidSubstitutionException.class,() -> lineUp.applySubstitution(substituted));

        Mockito.verify(lineupPlayer,Mockito.never()).setPosition(Mockito.anyInt());
    }


    @Test
    void applySubstitution_Exist() {
        Formation formation = Mockito.mock(Formation.class);
        Activity activity = Mockito.mock(Activity.class);
        LineUp lineUp = new LineUp(formation,activity);

        User user1 = Mockito.mock(User.class);
        User user2 = Mockito.mock(User.class);
        Mockito.when(user1.getId()).thenReturn(1L);
        Mockito.when(user2.getId()).thenReturn(2L);

        ArrayList<LineupPlayer> list = new ArrayList<>();
        list.add(Mockito.mock(LineupPlayer.class));
        list.add(Mockito.mock(LineupPlayer.class));
        Mockito.when(list.get(0).getUser()).thenReturn(user1);
        Mockito.when(list.get(0).getPosition()).thenReturn(1);
        Mockito.when(list.get(1).getUser()).thenReturn(user2);
        Mockito.when(list.get(1).getPosition()).thenReturn(2);
        lineUp.setPlayer(list);

        Substituted substituted = Mockito.mock(Substituted.class);
        Mockito.when(substituted.getOldPlayer()).thenReturn(user2);
        Mockito.when(substituted.getNewPlayer()).thenReturn(user1);

        Assertions.assertDoesNotThrow(() -> lineUp.applySubstitution(substituted));

        Mockito.verify(list.get(0),Mockito.times(1)).setPosition(2);
        Mockito.verify(list.get(1),Mockito.times(1)).setPosition(1);
    }

    @Test
    void applySubstitution_bench() {
        Formation formation = Mockito.mock(Formation.class);
        Activity activity = Mockito.mock(Activity.class);
        LineUp lineUp = new LineUp(formation,activity);

        User user1 = Mockito.mock(User.class);
        User user2 = Mockito.mock(User.class);
        Mockito.when(user1.getId()).thenReturn(1L);
        Mockito.when(user2.getId()).thenReturn(2L);

        ArrayList<LineupPlayer> list = new ArrayList<>();
        list.add(Mockito.mock(LineupPlayer.class));
        list.add(Mockito.mock(LineupPlayer.class));
        Mockito.when(list.get(0).getUser()).thenReturn(user1);
        Mockito.when(list.get(0).getPosition()).thenReturn(1);
        Mockito.when(list.get(1).getUser()).thenReturn(user2);
        Mockito.when(list.get(1).getPosition()).thenReturn(-1);
        lineUp.setPlayer(list);

        Substituted substituted = Mockito.mock(Substituted.class);
        Mockito.when(substituted.getOldPlayer()).thenReturn(user2);
        Mockito.when(substituted.getNewPlayer()).thenReturn(user1);

        Assertions.assertDoesNotThrow(() -> lineUp.applySubstitution(substituted));

        Mockito.verify(list.get(0),Mockito.times(1)).setPosition(-1);
        Mockito.verify(list.get(1),Mockito.times(1)).setPosition(1);
    }

    @Test
    void applySubstitution_otherBench() {
        Formation formation = Mockito.mock(Formation.class);
        Activity activity = Mockito.mock(Activity.class);
        LineUp lineUp = new LineUp(formation,activity);

        User user1 = Mockito.mock(User.class);
        User user2 = Mockito.mock(User.class);
        Mockito.when(user1.getId()).thenReturn(1L);
        Mockito.when(user2.getId()).thenReturn(2L);

        ArrayList<LineupPlayer> list = new ArrayList<>();
        list.add(Mockito.mock(LineupPlayer.class));
        list.add(Mockito.mock(LineupPlayer.class));
        Mockito.when(list.get(0).getUser()).thenReturn(user1);
        Mockito.when(list.get(0).getPosition()).thenReturn(-1);
        Mockito.when(list.get(1).getUser()).thenReturn(user2);
        Mockito.when(list.get(1).getPosition()).thenReturn(1);
        lineUp.setPlayer(list);

        Substituted substituted = Mockito.mock(Substituted.class);
        Mockito.when(substituted.getOldPlayer()).thenReturn(user2);
        Mockito.when(substituted.getNewPlayer()).thenReturn(user1);

        Assertions.assertDoesNotThrow(() -> lineUp.applySubstitution(substituted));

        Mockito.verify(list.get(0),Mockito.times(1)).setPosition(1);
        Mockito.verify(list.get(1),Mockito.times(1)).setPosition(-1);
    }

    @Test
    void applySubstitutions_test() {
        Formation formation = Mockito.mock(Formation.class);
        Activity activity = Mockito.mock(Activity.class);
        LineUp lineUp = new LineUp(formation,activity);

        User user1 = Mockito.mock(User.class);
        User user2 = Mockito.mock(User.class);
        Mockito.when(user1.getId()).thenReturn(1L);
        Mockito.when(user2.getId()).thenReturn(2L);

        ArrayList<LineupPlayer> list = new ArrayList<>();
        list.add(Mockito.mock(LineupPlayer.class));
        list.add(Mockito.mock(LineupPlayer.class));
        Mockito.when(list.get(0).getUser()).thenReturn(user1);
        Mockito.when(list.get(0).getPosition()).thenReturn(1);
        Mockito.when(list.get(1).getUser()).thenReturn(user2);
        Mockito.when(list.get(1).getPosition()).thenReturn(2);
        lineUp.setPlayer(list);

        Substituted substituted1 = Mockito.mock(Substituted.class);
        Mockito.when(substituted1.getOldPlayer()).thenReturn(user2);
        Mockito.when(substituted1.getNewPlayer()).thenReturn(user1);
        Substituted substituted2 = Mockito.mock(Substituted.class);
        Mockito.when(substituted2.getOldPlayer()).thenReturn(user1);
        Mockito.when(substituted2.getNewPlayer()).thenReturn(user2);
        ArrayList<Substituted> subs = new ArrayList<>();
        subs.add(substituted1);
        subs.add(substituted2);

        Assertions.assertDoesNotThrow(() -> lineUp.applySubstitutions(subs));

        Mockito.verify(list.get(0),Mockito.times(2)).setPosition(2);
        Mockito.verify(list.get(1),Mockito.times(2)).setPosition(1);
    }


    @Test
    void getPlayersInOrder_test() {
        Formation formation = Mockito.mock(Formation.class);
        Activity activity = Mockito.mock(Activity.class);
        LineUp lineUp = new LineUp(formation,activity);

        User user1 = Mockito.mock(User.class);
        User user2 = Mockito.mock(User.class);
        User user3 = Mockito.mock(User.class);


        ArrayList<LineupPlayer> list = new ArrayList<>();
        list.add(Mockito.mock(LineupPlayer.class));
        list.add(Mockito.mock(LineupPlayer.class));
        list.add(Mockito.mock(LineupPlayer.class));

        Mockito.when(list.get(0).getPosition()).thenReturn(2);
        Mockito.when(list.get(0).getUser()).thenReturn(user2);

        Mockito.when(list.get(1).getPosition()).thenReturn(1);
        Mockito.when(list.get(1).getUser()).thenReturn(user1);

        Mockito.when(list.get(2).getPosition()).thenReturn(3);
        Mockito.when(list.get(2).getUser()).thenReturn(user3);

        lineUp.setPlayer(list);


        List<User> users =  lineUp.getPlayersInOrder();
        Assertions.assertEquals(3,users.size());
        Assertions.assertEquals(user1,users.get(0));
        Assertions.assertEquals(user2,users.get(1));
        Assertions.assertEquals(user3,users.get(2));

    }

    @Test
    void isValidSubstitution_hasOldNew() {
        Formation formation = Mockito.mock(Formation.class);
        Activity activity = Mockito.mock(Activity.class);
        LineUp lineUp = new LineUp(formation,activity);

        User user1 = Mockito.mock(User.class);
        User user2 = Mockito.mock(User.class);

        Mockito.when(user1.getId()).thenReturn(1L);
        Mockito.when(user2.getId()).thenReturn(2L);

        ArrayList<LineupPlayer> list = new ArrayList<>();
        list.add(Mockito.mock(LineupPlayer.class));
        list.add(Mockito.mock(LineupPlayer.class));
        Mockito.when(list.get(0).getUser()).thenReturn(user1);
        Mockito.when(list.get(1).getUser()).thenReturn(user2);
        lineUp.setPlayer(list);

        Substituted substituted = Mockito.mock(Substituted.class);

        Mockito.when(substituted.getOldPlayer()).thenReturn(user1);
        Mockito.when(substituted.getNewPlayer()).thenReturn(user2);

        Assertions.assertTrue(lineUp.isValidSubstitution(substituted));
    }

    @Test
    void isValidSubstitution_hasNew() {
        Formation formation = Mockito.mock(Formation.class);
        Activity activity = Mockito.mock(Activity.class);
        LineUp lineUp = new LineUp(formation,activity);

        User user1 = Mockito.mock(User.class);
        User user2 = Mockito.mock(User.class);
        User user3 = Mockito.mock(User.class);

        Mockito.when(user1.getId()).thenReturn(1L);
        Mockito.when(user2.getId()).thenReturn(2L);
        Mockito.when(user3.getId()).thenReturn(3L);

        ArrayList<LineupPlayer> list = new ArrayList<>();
        list.add(Mockito.mock(LineupPlayer.class));
        list.add(Mockito.mock(LineupPlayer.class));
        Mockito.when(list.get(0).getUser()).thenReturn(user1);
        Mockito.when(list.get(1).getUser()).thenReturn(user2);
        lineUp.setPlayer(list);

        Substituted substituted = Mockito.mock(Substituted.class);

        Mockito.when(substituted.getOldPlayer()).thenReturn(user3);
        Mockito.when(substituted.getNewPlayer()).thenReturn(user2);

        Assertions.assertFalse(lineUp.isValidSubstitution(substituted));
    }

    @Test
    void isValidSubstitution_hasOld() {
        Formation formation = Mockito.mock(Formation.class);
        Activity activity = Mockito.mock(Activity.class);
        LineUp lineUp = new LineUp(formation,activity);

        User user1 = Mockito.mock(User.class);
        User user2 = Mockito.mock(User.class);
        User user3 = Mockito.mock(User.class);

        Mockito.when(user1.getId()).thenReturn(1L);
        Mockito.when(user2.getId()).thenReturn(2L);
        Mockito.when(user3.getId()).thenReturn(3L);

        ArrayList<LineupPlayer> list = new ArrayList<>();
        list.add(Mockito.mock(LineupPlayer.class));
        list.add(Mockito.mock(LineupPlayer.class));
        Mockito.when(list.get(0).getUser()).thenReturn(user1);
        Mockito.when(list.get(1).getUser()).thenReturn(user2);
        lineUp.setPlayer(list);

        Substituted substituted = Mockito.mock(Substituted.class);

        Mockito.when(substituted.getNewPlayer()).thenReturn(user3);
        Mockito.when(substituted.getOldPlayer()).thenReturn(user2);

        Assertions.assertFalse(lineUp.isValidSubstitution(substituted));
    }


    @Test
    void isValidSubstitution_hasNone() {
        Formation formation = Mockito.mock(Formation.class);
        Activity activity = Mockito.mock(Activity.class);
        LineUp lineUp = new LineUp(formation,activity);

        User user1 = Mockito.mock(User.class);
        User user2 = Mockito.mock(User.class);
        User user3 = Mockito.mock(User.class);

        Mockito.when(user1.getId()).thenReturn(1L);
        Mockito.when(user2.getId()).thenReturn(2L);
        Mockito.when(user3.getId()).thenReturn(3L);

        ArrayList<LineupPlayer> list = new ArrayList<>();
        list.add(Mockito.mock(LineupPlayer.class));
        list.add(Mockito.mock(LineupPlayer.class));
        Mockito.when(list.get(0).getUser()).thenReturn(user1);
        Mockito.when(list.get(1).getUser()).thenReturn(user2);
        lineUp.setPlayer(list);

        Substituted substituted = Mockito.mock(Substituted.class);

        Mockito.when(substituted.getNewPlayer()).thenReturn(user3);
        Mockito.when(substituted.getOldPlayer()).thenReturn(user3);

        Assertions.assertFalse(lineUp.isValidSubstitution(substituted));
    }
}



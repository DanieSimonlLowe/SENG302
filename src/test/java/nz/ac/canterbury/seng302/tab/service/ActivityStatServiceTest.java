package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.stats.ActivityStat;
import nz.ac.canterbury.seng302.tab.entity.stats.GameScore;
import nz.ac.canterbury.seng302.tab.repository.ActivityStatRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;

import java.util.Optional;

public class ActivityStatServiceTest {
    private ActivityStatRepository activityStatRepository;
    private ActivityStatService activityStatService;

    @BeforeEach
    void setup() {
        activityStatRepository = Mockito.mock(ActivityStatRepository.class);
        activityStatService = new ActivityStatService(activityStatRepository);

    }

    @Test
    void save_canSave_true_Test() {
        ActivityStat activityStat = Mockito.mock(ActivityStat.class);
        Mockito.when(activityStat.canSave(activityStatRepository)).thenReturn(true);
        Executable executable = () -> activityStatService.save(activityStat);
        Assertions.assertDoesNotThrow(executable);
        Mockito.verify(activityStatRepository, Mockito.times(1)).save(activityStat);
    }

    @Test
    void save_canSave_false_Test() {
        ActivityStat activityStat = Mockito.mock(ActivityStat.class);
        Mockito.when(activityStat.canSave(activityStatRepository)).thenReturn(false);

        Executable executable = () -> activityStatService.save(activityStat);
        Assertions.assertThrowsExactly(IllegalStateException.class,executable);

        Mockito.verify(activityStatRepository, Mockito.never()).save(activityStat);
    }

    @Test
    void getActivityGameScores_test() {
        GameScore gameScore = Mockito.mock(GameScore.class);

        Mockito.when(activityStatRepository.findGameScoreByActivityId(2L)).thenReturn(Optional.of(gameScore));

        Optional<GameScore> out = activityStatService.getActivityGameScores(2L);
        Assertions.assertTrue(out.isPresent());
        Assertions.assertEquals(gameScore,out.get());
    }

    @Test
    void saveGameScore_canSave_true_noOld_Test() {
        GameScore gameScore = Mockito.mock(GameScore.class);
        Mockito.when(gameScore.canSave(activityStatRepository)).thenReturn(true);

        Activity activity = Mockito.mock(Activity.class);
        Mockito.when(activity.getId()).thenReturn(2L);
        Mockito.when(gameScore.getActivity()).thenReturn(activity);


        Mockito.when(activityStatRepository.findGameScoreByActivityId(2L)).thenReturn(Optional.empty());

        Executable executable = () -> activityStatService.saveGameScore(gameScore);
        Assertions.assertDoesNotThrow(executable);

        Mockito.verify(activityStatRepository, Mockito.times(1)).save(gameScore);
    }

    @Test
    void saveGameScore_canSave_true_hasOld_Test() {
        GameScore gameScore = Mockito.mock(GameScore.class);
        Mockito.when(gameScore.canSave(activityStatRepository)).thenReturn(true);

        Activity activity = Mockito.mock(Activity.class);
        Mockito.when(activity.getId()).thenReturn(2L);
        Mockito.when(gameScore.getActivity()).thenReturn(activity);

        GameScore old = Mockito.mock(GameScore.class);
        Mockito.when(activityStatRepository.findGameScoreByActivityId(2L)).thenReturn(Optional.of(old));

        Executable executable = () -> activityStatService.saveGameScore(gameScore);
        Assertions.assertDoesNotThrow(executable);

        Mockito.verify(old, Mockito.times(1)).setScores(gameScore);
        Mockito.verify(activityStatRepository, Mockito.times(1)).save(old);
    }
}

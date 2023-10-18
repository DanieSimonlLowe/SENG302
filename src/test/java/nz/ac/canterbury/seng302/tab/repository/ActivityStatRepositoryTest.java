package nz.ac.canterbury.seng302.tab.repository;

import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.stats.GameScore;
import nz.ac.canterbury.seng302.tab.entity.stats.Fact;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.List;

@SpringBootTest
@Transactional
class ActivityStatRepositoryTest {
    @Resource
    ActivityStatRepository activityStatRepository;

    @Resource
    ActivityRepository activityRepository;

    @Test
    void findGameScoreByActivityId_test() {
        Activity activity = activityRepository.findAll().iterator().next();
        GameScore gameScore = new GameScore(activity, "1", "2");
        activityStatRepository.save(gameScore);

        Optional<GameScore> out = activityStatRepository.findGameScoreByActivityId(activity.getId());

        Assertions.assertTrue(out.isPresent());
        Assertions.assertEquals(out.get().getId(), gameScore.getId());
    }

    @Test
    void findAllFactByActivityId_test() {
        List<Fact> factList = activityStatRepository.findAllFactByActivityId(1L);
        Assertions.assertEquals(3,factList.size());
        Assertions.assertEquals("test2",factList.get(0).getDescription());
        Assertions.assertEquals("test",factList.get(1).getDescription());
        Assertions.assertEquals("test3",factList.get(2).getDescription());
    }
}

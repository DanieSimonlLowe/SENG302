package nz.ac.canterbury.seng302.tab.repository;

import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.tab.entity.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@Transactional
public class LineupPlayerRepositoryTest {
    @Resource
    private LineupPlayerRepository lineupPlayerRepository;

    @Resource
    private LineupRepository lineupRepository;

    @Resource
    private UserRepository userRepository;

    @Resource
    private ActivityRepository activityRepository;

    @Resource
    private FormationRepository formationRepository;

    @Resource
    private LocationRepository locationRepository;

    @Resource
    private TeamRepository teamRepository;
    @Test
    void findUserIdsByLineupId() {
        Location location = new Location("", "", "", "", "test2", "test2");
        locationRepository.save(location);

        Team team = new Team("TeamNameForTest", location, "SportNameForTest");
        team = teamRepository.save(team);

        User user1 = new User("","","", LocalDate.now(),location,"");
        user1 = userRepository.save(user1);

        User user2 = new User("","","", LocalDate.now(),location,"");
        user2 = userRepository.save(user2);

        Formation formation = new Formation("2", "basketball_court", team);
        formation = formationRepository.save(formation);

        Activity activity = new Activity(ActivityType.OTHER, team, null, "description1", LocalDateTime.now(), LocalDateTime.now(), user1, location);
        activity = activityRepository.save(activity);

        LineUp lineup = new LineUp(formation, activity);
        lineup = lineupRepository.save(lineup);

        LineupPlayer lineupPlayer1 = new LineupPlayer(user1, lineup, 0);
        lineupPlayerRepository.save(lineupPlayer1);

        LineupPlayer lineupPlayer2 = new LineupPlayer(user2, lineup, 0);

        List<Long> lineupUserIds = lineupPlayerRepository.getUserIdsByLineUpId(lineup.getId());
        Assertions.assertTrue(lineupUserIds.contains(lineupPlayer1.getUser().getId()));
        Assertions.assertFalse(lineupUserIds.contains(lineupPlayer2.getUser().getId()));
    }
}

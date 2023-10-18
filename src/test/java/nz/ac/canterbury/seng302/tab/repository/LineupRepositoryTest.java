package nz.ac.canterbury.seng302.tab.repository;

import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.tab.entity.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@SpringBootTest
@Transactional
public class LineupRepositoryTest {
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
    void findByActivityId() {
        Location location = new Location("", "", "", "", "test2", "test2");
        locationRepository.save(location);

        Team team = new Team("TeamNameForTest", location, "SportNameForTest");
        team = teamRepository.save(team);

        User user = new User("","","", LocalDate.now(),location,"");
        user = userRepository.save(user);

        Formation formation = new Formation("2", "basketball_court", team);
        formation = formationRepository.save(formation);

        Activity activity = new Activity(ActivityType.OTHER, team, null, "description1", LocalDateTime.now(), LocalDateTime.now(), user, location);
        activity = activityRepository.save(activity);

        LineUp lineup = new LineUp(formation, activity);
        lineupRepository.save(lineup);

        Optional<LineUp> lineUpFromDB =  lineupRepository.findByActivityId(activity.getId());
        Assertions.assertTrue(lineUpFromDB.isPresent());
    }
}

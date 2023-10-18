package nz.ac.canterbury.seng302.tab.repository;

import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.tab.entity.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional
public class FormationRepositoryTest {
    @Resource
    private FormationRepository formationRepository;

    @Resource
    private UserRepository userRepository;

    @Resource
    private LocationRepository locationRepository;

    @Resource
    private TeamRepository teamRepository;

    @Test
    void saveFormation_test() {
        Location location = new Location("", "", "", "", "test2", "test2");
        locationRepository.save(location);

        Team team = new Team("TeamNameForTest", location, "SportNameForTest");
        teamRepository.save(team);


        Formation formation = new Formation("1-1","football_pitch", team);
        formationRepository.save(formation);

        Optional<Formation> saved = formationRepository.findById(formation.getId());
        Assertions.assertFalse(saved.isEmpty());

        Assertions.assertEquals(2,saved.get().getPlayersPerSection().size());
        short value = saved.get().getPlayersPerSection().get(0);
        Assertions.assertEquals(1, value);
        value = saved.get().getPlayersPerSection().get(1);
        Assertions.assertEquals(1, value);
        Assertions.assertEquals("1-1", saved.get().getPlayersPerSectionString());
    }
}

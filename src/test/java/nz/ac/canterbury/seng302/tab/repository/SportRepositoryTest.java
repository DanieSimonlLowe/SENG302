package nz.ac.canterbury.seng302.tab.repository;

import jakarta.annotation.Resource;
import nz.ac.canterbury.seng302.tab.entity.Sport;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SportRepositoryTest {
    @Resource
    private SportRepository sportRepository;


    @Test
    void findById() {
        // given
        Sport sport = new Sport("Gymnastics");
        sportRepository.save(sport);
        // when
        Optional<Sport> foundSport = sportRepository.findById(sport.getId());
        // then
        assertTrue(foundSport.isPresent());
    }

    @Test
    void findAll() {
        int intialSize = sportRepository.findAll().size();
        Sport snowboarding = new Sport("Snowboarding");
        Sport surfing = new Sport("Surfing");
        Sport hiking = new Sport("Hiking");
        Sport cycling = new Sport("Cycling");

        sportRepository.save(snowboarding);
        sportRepository.save(surfing);
        sportRepository.save(hiking);
        sportRepository.save(cycling);
        assertEquals(intialSize + 4, sportRepository.findAll().size());
    
    }

    @Test
    void findBySportName() {
        // given
        Sport sport = new Sport("Hang gliding");
        sportRepository.save(sport);
        // when
        Sport foundSport = sportRepository.findBySportName("Hang gliding");
        Sport foundSportAgain = sportRepository.findBySportName(sport.getSportName());
        // then
        assertNotNull(foundSport);
        assertNotNull(foundSportAgain);
    }

}

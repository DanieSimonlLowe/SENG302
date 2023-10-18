package nz.ac.canterbury.seng302.tab.repository;


import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.tab.entity.Location;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class LocationRepositoryTest {

    @Resource
    private LocationRepository locationRepository;

    @Test
    void findById() {
        Location location = new Location("", "", "", "", "Memphis", "United States of America");
        locationRepository.save(location);
        Optional<Location> foundLocation = locationRepository.findById(location.getId());
        assertTrue(foundLocation.isPresent());
    }


}

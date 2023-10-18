package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.repository.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LocationServiceTest {

    @Mock
    LocationRepository mockLocationRepository;

    @InjectMocks
    final
    LocationService locationService = Mockito.spy(LocationService.class);

    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void test_addingLocation() {
        Location location = new Location("", "", "", "", "Memphis", "United States of America");
        locationService.addLocation(location);
        Mockito.verify(mockLocationRepository).save(location);
    }

    @Test
    void test_deleteLocation() {
        Location location = new Location("", "", "", "", "Memphis", "United States of America");
        locationService.addLocation(location);
        locationService.delete(location);
        Mockito.verify(mockLocationRepository).delete(location);
    }

    @Test
    void test_findLocation() {
        locationService.getLocation(1);
        Mockito.verify(mockLocationRepository).findById(1);
    }
}

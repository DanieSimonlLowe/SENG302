package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The service for saving and querying locations to and from the database.
 * 
 * @author  Nathan Harper   
 * @version 1.0.0, March 23 
 */
@Service
public class LocationService {

    /** The location repository */
    @Autowired
    private LocationRepository locationRepository;

    /**
     * Getter for the location
     * @param id the id of the location to get
     * @return the location
     */
    public Location getLocation(long id) {
        return locationRepository.findById(id);
    }

    /**
     * Saves a location to persistence
     * @param location the location to save
     * @return the saved location
     */
    public Location addLocation(Location location) {
        return locationRepository.save(location);
    }

    /**
     * Deletes a location from persistence
     * @param location the location to delete
     */
    public void delete(Location location) {
        locationRepository.delete(location);
    }
}

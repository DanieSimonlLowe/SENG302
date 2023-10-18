package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.Location;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * LocationRepository repository accessor using Spring's @link{CrudRepository}.
 * Repository to query data from the database
 */
@Repository
public interface LocationRepository extends CrudRepository<Location, Long> {

    /**
     * Finds a location by a given id
     * @param id the id to search the locations by
     * @return the location with the given id
     */
    Location findById(long id);
}

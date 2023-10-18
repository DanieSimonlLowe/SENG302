package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.Formation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FormationRepository extends CrudRepository<Formation, Long> {
    /**
     * Finds Formation by their id
     * @param id the id
     * @return a list of the user
     */
    Optional<Formation> findById(Long id);

    @Query(value = "SELECT formation.* " +
            "FROM formation " +
            "JOIN team ON formation.team_id = team.id " +
            "WHERE team.id = :teamId "
            , nativeQuery = true)
    List<Formation> findFormationsByTeamId(@Param("teamId") long teamId);

}

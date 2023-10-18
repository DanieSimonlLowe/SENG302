package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Formation;
import nz.ac.canterbury.seng302.tab.repository.FormationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * The service for saving and querying formations to and from the database.
 */
@Service
public class FormationService {
    private final FormationRepository formationRepository;


    public FormationService(FormationRepository formationRepository) {
        this.formationRepository = formationRepository;
    }

    public void save(Formation formation) {
        formationRepository.save(formation);
    }

    public Optional<Formation> getFormationById(long id) {
        return formationRepository.findById(id);
    }

    public List<Formation> getFormationsByTeamId(long id) {
        return formationRepository.findFormationsByTeamId(id);
    }
}

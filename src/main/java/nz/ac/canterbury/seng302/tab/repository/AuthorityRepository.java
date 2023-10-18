package nz.ac.canterbury.seng302.tab.repository;

import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.tab.entity.Authority;
import org.springframework.data.repository.CrudRepository;

public interface AuthorityRepository extends CrudRepository<Authority, Long> {
    /**
     * Saves the Authority to the database
     * @param Authority must not be {@literal null}.
     * @return the saved Authority will never be {@literal null}.
     * @param <S> the type of the Authority to save
     */
    @Transactional
    <S extends Authority> S save(S Authority);
}

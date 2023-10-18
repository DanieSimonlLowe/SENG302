package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.TokenGenerator;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


/**
 * RegistrationTokenRepository repository accessor using Spring's @link{CrudRepository}.
 * Repository to query data from the database
 */
@Repository
public interface TokenRepository extends CrudRepository<TokenGenerator, Long> {

    /**
     * Finds a registration token by the string token 
     * @param token the token to search by 
     * @return the registration token with the given string token
     */
    TokenGenerator findByToken(String token);

    /**
     * Finds a registration token by the string email 
     * @param email the email to search by 
     * @return the registration token with the given email
     */
    TokenGenerator findByEmailIgnoreCase(String email);

    /**
     * Finds a registration token by its id 
     * @param id the id to search by 
     * @return the registration token with the given id
     */
    TokenGenerator findById(long id);
}

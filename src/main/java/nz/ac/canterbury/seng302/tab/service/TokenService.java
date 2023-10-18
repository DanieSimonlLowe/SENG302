package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.TokenGenerator;
import nz.ac.canterbury.seng302.tab.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The service for the registration token.
 * 
 * @author  Daniel Lowe
 * @version 1.0.0, April 23 
 */
@Service
public class TokenService {

    /** The registration token repository */
    private final TokenRepository repository;

    /** The location service */
    private final LocationService locationService;

    /**
     * Constructor for the RegistrationTokenService
     * @param repository      the registration token repository
     * @param locationService the location service
     */
    @Autowired
    public TokenService(TokenRepository repository, LocationService locationService) {
        this.repository = repository;
        this.locationService = locationService;
    }

    /**
     * Saves the given token to the repository, and deletes it if it has expired
     * @param token the token to save
     */
    public void save(TokenGenerator token) {
        repository.save(token);
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        if (repository.findById(token.getId()) != null) {
                            locationService.delete(token.getLocation());
                            repository.delete(token);

                        }
                    }
                },
                token.getLifeSpan()
        );
    }

    /**
     * Saves the given token to the repository, and deletes it if it has expired
     * @param token the token to save
     */
    public void saveForPasswordUpdate(TokenGenerator token) {
        repository.save(token);
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        if (repository.findById(token.getId()) != null) {
                            repository.delete(token);
                        }
                    }
                },
                token.getLifeSpan()
        );
    }

    /**
     * Deletes the token from the repository
     * @param token the token to delete
     */
    public void delete(TokenGenerator token) {
        repository.delete(token);
    }

    /**
     * Finds a RegistrationToken by the string token
     * @param token the token to search by
     * @return a RegistrationToken
     */
    public TokenGenerator findByToken(String token) {
        return repository.findByToken(token);
    }

    /**
     * Finds a RegistrationToken by the email
     * @param email the email to search by
     * @return a RegistrationToken
     */
    public TokenGenerator findByEmail(String email) {
        return repository.findByEmailIgnoreCase(email);
    }

}

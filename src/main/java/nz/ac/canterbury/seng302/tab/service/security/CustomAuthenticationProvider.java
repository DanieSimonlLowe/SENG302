package nz.ac.canterbury.seng302.tab.service.security;

import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.exceptions.BadEmailException;
import nz.ac.canterbury.seng302.tab.service.checkers.RegistrationChecker;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;


/**
 * Custom Authentication Provider class, to allow for handling authentication in any way we see fit.
 * In this case using our existing {@link User}
 * 
 * @author Nathan Harper
 * @version 1.0.0, March 23
 */
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    /** Autowired user service for custom authentication using our own user objects */
    @Autowired
    private UserService userService;

    /**
     * Default constructor for CustomAuthenticationProvider
     */
    public CustomAuthenticationProvider() {
        super();
    }

    /**
     * Custom authentication implementation
     * @param authentication An implementation object that must have non-empty email (name) and password (credentials)
     * @return A new {@link UsernamePasswordAuthenticationToken} if email and password are valid with users authorities
     */
    @Override
    public Authentication authenticate(Authentication authentication) {

        String email = String.valueOf(authentication.getPrincipal());
        String password = String.valueOf(authentication.getCredentials());

        if (password == null || password.isEmpty()) {
            throw new BadCredentialsException("Bad Credentials");
        }

        if (email == null || email.isEmpty() || !RegistrationChecker.isValidEmail(email)) {
            throw new BadEmailException("Bad email");
        }

        User u = userService.getUserByEmail(email);
        if (u == null) {
            throw new BadCredentialsException("Invalid username");
        } else {
            if (!u.samePassword(password)) {
                throw new BadCredentialsException("Invalid password");
            }
        }
        return new UsernamePasswordAuthenticationToken(u.getEmail(), null, u.getAuthorities());
    }

    /** {@inheritDoc} */
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}

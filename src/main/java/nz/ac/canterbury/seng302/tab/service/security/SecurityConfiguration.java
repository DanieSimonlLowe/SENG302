package nz.ac.canterbury.seng302.tab.service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;


/**
 * Custom Security Configuration
 * Such functionality was previously handled by WebSecurityConfigurerAdapter but has now been moved here.
 * 
 * @author Nathan Harper
 * @version 1.0.0, March 23 
 */
@Configuration
@EnableWebSecurity
@ComponentScan("nz.ac.canterbury.seng302.tab.service")
public class SecurityConfiguration {

    /**
     * Our custom Authentication Provider {@link CustomAuthenticationProvider}
     */
    @Autowired
    private CustomAuthenticationProvider authProvider;

    /** Login URL string constant */
    private static final String LOGIN_URL = "/login";

    /**
     * Constant of types of files allowed past security.
     */
    private static final RequestMatcher[] REQUESTMATCHERS = {
            AntPathRequestMatcher.antMatcher("/h2/**"),
            AntPathRequestMatcher.antMatcher("/css/**"),
            AntPathRequestMatcher.antMatcher("/images/**"),
            AntPathRequestMatcher.antMatcher("/javascript/**"),
            AntPathRequestMatcher.antMatcher("/webjars/**"),
            AntPathRequestMatcher.antMatcher("/fragments/**"),
            AntPathRequestMatcher.antMatcher("/profilePics/**"),
            AntPathRequestMatcher.antMatcher("/public/**"),
            AntPathRequestMatcher.antMatcher("/activities/**"),
            AntPathRequestMatcher.antMatcher("/users/**"),
            AntPathRequestMatcher.antMatcher("/feed/**"),
            AntPathRequestMatcher.antMatcher("/userAlerts/**"),
            AntPathRequestMatcher.antMatcher("/userAlertNumber/**")

    };

    @Autowired
    private FetchHeaderFilter fetchHeaderFilter;


    /**
     * Create an Authentication Manager with our {@link CustomAuthenticationProvider}
     * @param http http security configuration object from Spring
     * @return a new authentication manager
     * @throws Exception if the AuthenticationManager can not be built
     */
    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authProvider);
        return authenticationManagerBuilder.build();
    }

    /**
     *
     * @param http http security configuration object from Spring (beaned in)
     * @return Custom SecurityFilterChain
     * @throws Exception if the SecurityFilterChain can not be built
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //Allow h2 console through security. Note: Spring 6 broke the nicer way to do this (i.e. how the authorisation is
        //handled below)
        //See https://github.com/spring-projects/spring-security/issues/12546
        http.authorizeHttpRequests(auth -> auth.requestMatchers(REQUESTMATCHERS).permitAll())
                .headers(headers -> headers.frameOptions().disable())
                .csrf(csrf -> csrf.ignoringRequestMatchers(REQUESTMATCHERS))
                .addFilterBefore(fetchHeaderFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests()

                //Allow "/", "/register", "/login", and "/search" to anyone (permitAll)
                .requestMatchers("/", "/register", LOGIN_URL, "../profilePics/**", "/search", "/allProfiles", "/otherProfile",
                        "/home", "/searchTeams", "/teamProfile", "/allTeams", "/confirmUser", "/api/location-request",
                        "/lostPassword", "/resetPassword", "/error", "/updateUser", "/allClubs", "/searchClubs", "/clubProfile", "/users/**")
                .permitAll()
                //Only allow logged-in users reach certain pages
                .requestMatchers("/form", "/viewActivity", "/teamMembers", "/clubForm")
                .hasRole("USER")
                //Only allow admins to reach the "/admin" page
                .requestMatchers("/admin")
                //note that the "ROLE_" prefix is not needed as we are calling "hasRole()"
                .hasRole("ADMIN")
                //any other request requires authentication
                .anyRequest()
                .authenticated()
                .and()
                //Define logging in, a POST "/login" endpoint now exists under the hood, after login redirect to user page
                .formLogin().loginPage(LOGIN_URL).loginProcessingUrl(LOGIN_URL).defaultSuccessUrl("/profile", true)
                .and()
                //Define logging out, a POST "/logout" endpoint now exists under the hood, redirect to "/login", invalidate
                //session and remove cookie
                .logout().logoutUrl("/logout").logoutSuccessUrl("/login").invalidateHttpSession(true)
                .deleteCookies("JSESSIONID");
        return http.build();
    }
}

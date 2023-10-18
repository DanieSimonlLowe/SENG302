package nz.ac.canterbury.seng302.tab;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.entity.TokenGenerator;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.SportService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import java.time.LocalDate;

/**
 * TAB (not that TAB) entry-point
 * Note @link{SpringBootApplication} annotation
 */
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class})
public class TabApplication {

	/**
	 * Main entry point, runs the Spring application
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(TabApplication.class, args);
	}

	/**
	 * Adds temporary users to persistence
	 * @param userService the user service
	 */
	public static void addTempUsers(UserService userService) {


		String[] firstNames = {"Tom", "Anu", "Liam", "Nathan", "Celia", "Daniel", "Morgan"};
		String[] lastNames = {"Barthelmeh", "Ahuja", "Cuthbert", "Harper", "Allen", "Lowe", "English"};

		for(int i=0; i<firstNames.length; ++i) {
			String email = firstNames[i] + '.' +  lastNames[i] + "@hotmail.com";
			LocalDate date = LocalDate.now();
			String password = "testPassword1!";
			Location location = new Location("", "", "", "", "Christchurch",  "NZ");
			User user = new TokenGenerator(email, firstNames[i], lastNames[i], date, location, password, "register").generateUser();
			userService.save(user);
		}
	}

	/**
	 * Adds an initial list of sports to persistence
	 * @param sportService the sport service
	 */
	public static void addInitialSports(SportService sportService) {
		String[] sports = {"Golf", "Rugby", "Baseball", "Basketball", "Table Tennis", "Volleyball", "Tennis", "Hockey", "Cricket", "Football"};
		for (String sport : sports) {
			sportService.add(new Sport(sport));
		}
	}


}

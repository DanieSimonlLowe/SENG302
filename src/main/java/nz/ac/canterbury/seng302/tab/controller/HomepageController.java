package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;


/**
 * This the basic spring boot controller for the homepage, note the @link{Controller} annotation which defines this.
 * This controller defines endpoints as functions with specific HTTP mappings
 */
@Controller
public class HomepageController {
    /** The logger */
    final Logger logger = LoggerFactory.getLogger(HomepageController.class);

    /** The UserService used for database logic */
    private final UserService userService;

    /** The TeamService used for the database logic */
    private final TeamService teamService;

    /**
     * Constructor for the homepage controller
     * @param teamService the team service
     * @param userService the user service
     */
    @Autowired
    HomepageController(UserService userService, TeamService teamService) {
        this.userService = userService;
        this.teamService = teamService;
    }

    /**
     * Adds the teams managed by the current user to the Thymeleaf model for use on the navbar
     * @return the list of teams managed by the current user
     */
    @ModelAttribute("dropdownTeams")
    public List<Team> addDropdownTeams() {
        if (checkIsLoggedIn()) {
            User currentUser =  userService.getUserByEmail((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            return teamService.getTeamsThatUserManagesFromUserId(currentUser.getId());
        }
        return List.of();
    }

    /**
     * Checks if a user is logged in
     * @return boolean whether the user is logged in
     */
    @ModelAttribute("loggedIn")
    private boolean checkIsLoggedIn() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return !username.equals("anonymousUser") && !username.equals("anonymous");
    }

    /**
     * Redirects GET default url '/' to '/homepage'
     * @return redirect to /homepage
     */
    @GetMapping("/")
    public String home() {
        logger.info("GET /");
        return "homepage";
    }
}

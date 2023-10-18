package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Is used to set up the page before the other controllers interact with it.
 * */
@ControllerAdvice
public class GlobalControllerAdvice {



    /** The UserService for database logic */
    private UserService userService;

    /**
     * Constructor for the global controller
     * @param userService the user service
     */
    @Autowired
    public GlobalControllerAdvice(UserService userService) {
        this.userService = userService;

    }

    /**
     * Sets up the page before other controllers are run.
     * @param model is used to interact with thymeleaf
     * */
    @ModelAttribute
    public void setupPage(Model model) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getUserByEmail(username);

        if (user != null) {
            model.addAttribute("userProfileImage", user.getProfilePicName());
            model.addAttribute("loggedIn",true);
            model.addAttribute("userId",user.getId());
        }

    }
}

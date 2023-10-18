package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

class GlobalControllerAdviceTest {

    UserService userService;
    GlobalControllerAdvice globalControllerAdvice;

    Authentication authentication;

    @BeforeEach
    void setup() {
        userService = Mockito.mock(UserService.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        authentication = Mockito.mock(Authentication.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);

        globalControllerAdvice = new GlobalControllerAdvice(userService);
    }

    @Test
    void setupPage_notLoggedIn_test() {
        Mockito.when(authentication.getPrincipal()).thenReturn("a");
        Mockito.when(userService.getUserByEmail("a")).thenReturn(null);

        Model model = Mockito.mock(Model.class);
        globalControllerAdvice.setupPage(model);

        Mockito.verify(model,Mockito.never()).addAttribute(Mockito.any(),Mockito.any());

    }

    @Test
    void setupPage_loggedIn_test() {
        Mockito.when(authentication.getPrincipal()).thenReturn("a");
        User user = Mockito.mock(User.class);
        Mockito.when(userService.getUserByEmail("a")).thenReturn(user);
        Mockito.when(user.getProfilePicName()).thenReturn("b");

        Model model = Mockito.mock(Model.class);
        globalControllerAdvice.setupPage(model);

        Mockito.verify(model,Mockito.times(1)).addAttribute("userProfileImage",user.getProfilePicName());
        Mockito.verify(model,Mockito.times(1)).addAttribute("loggedIn",true);

    }
}

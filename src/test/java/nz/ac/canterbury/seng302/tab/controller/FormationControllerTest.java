package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.FormationService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.ui.Model;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@SpringBootTest
@AutoConfigureMockMvc
@WithAnonymousUser
public class FormationControllerTest {


    @MockBean
    TeamService teamService;

    @MockBean
    UserService userService;

    @MockBean
    FormationService formationService;

    private FormationController formationController;


    Authentication authentication;

    @BeforeEach
    void setup() {


        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        authentication = Mockito.mock(Authentication.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);

        Mockito.when(authentication.getPrincipal()).thenReturn("email");

        formationController = new FormationController(teamService,userService,formationService);
    }



    @Test
    void getFormationsPage_blueSkyTest() {
        Team team = Mockito.mock(Team.class);

        Mockito.when(team.getName()).thenReturn("name");

        Mockito.when(teamService.getTeam(1)).thenReturn(Optional.of(team));

        Model model = Mockito.spy(Model.class);

        String out = formationController.createFormation(1L, model);
        Assertions.assertEquals("formations",out);


        Mockito.verify(model).addAttribute("team",team);
        Mockito.verify(model).addAttribute("teamId",1L);
        Mockito.verify(model).addAttribute("loggedIn",true);
    }


    @Test
    void getFormationsPage_loggedOutTest() {
        Team team = Mockito.mock(Team.class);
        Mockito.when(team.getName()).thenReturn("name");


        Mockito.when(teamService.getTeam(1)).thenReturn(Optional.of(team));



        Model model = Mockito.spy(Model.class);

        Mockito.when(authentication.getPrincipal()).thenReturn("anonymousUser");


        String out = formationController.createFormation(1L,  model);
        Assertions.assertEquals("formations",out);


        Mockito.verify(model).addAttribute("team",team);
        Mockito.verify(model).addAttribute("teamId",1L);
        Mockito.verify(model).addAttribute("loggedIn",false);
    }

    private static Stream<Arguments> saveFormation_blueSkyTest_values() {
        return Stream.of(
                Arguments.of(1,"1-2"),
                Arguments.of(1,"1-1-1"),
                Arguments.of(1,"2-1")
        );
    }

    private static Stream<Arguments> saveFormation_invalidFormation_values() {
        return Stream.of(
                Arguments.of(1,"-"),
                Arguments.of(1,"-1-3-1"),
                Arguments.of(1,"g"),
                Arguments.of(1,"1-2-3-4-5-6-7-8-9-10"),
                Arguments.of(1,"1-4-16-2")
        );
    }

    @ParameterizedTest
    @MethodSource("saveFormation_blueSkyTest_values")
    void saveFormation_blueSkyTest(long teamId, String formationInput) {
        User user = Mockito.mock(User.class);
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(user.getId()).thenReturn(1L);
        List<Team> teams = new ArrayList<>();
        Team team = Mockito.mock(Team.class);
        Mockito.when(team.getId()).thenReturn(teamId);
        teams.add(team);
        Mockito.when(teamService.getTeamsThatUserManagesFromUserId(1L)).thenReturn(teams);


        RedirectAttributes ra =  Mockito.mock(RedirectAttributes.class);

        String out = formationController.saveFormation(formationInput, "football_pitch", teamId,ra);
        Assertions.assertEquals("redirect:/teamProfile?id="+teamId,out);
        Mockito.verify(userService, Mockito.times(1)).getUserByEmail("email");
        Mockito.verify(teamService, Mockito.times(1)).getTeamsThatUserManagesFromUserId(1L);
        Mockito.verify(formationService, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(ra, Mockito.times(1)).addFlashAttribute("message","Formation was saved successfully.");
    }

    @ParameterizedTest
    @MethodSource("saveFormation_blueSkyTest_values")
    void saveFormation_cantEditTest(long teamId, String formationInput) {
        User user = Mockito.mock(User.class);
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(user.getId()).thenReturn(1L);
        List<Team> teams = new ArrayList<>();
        Team team = Mockito.mock(Team.class);
        Mockito.when(team.getId()).thenReturn(teamId+1);
        teams.add(team);
        Mockito.when(teamService.getTeamsThatUserManagesFromUserId(1L)).thenReturn(teams);


        RedirectAttributes ra =  Mockito.mock(RedirectAttributes.class);

        String out = formationController.saveFormation(formationInput,"football_pitch", teamId,ra);
        Assertions.assertEquals("redirect:/formations?team="+teamId,out);
        Mockito.verify(userService, Mockito.times(1)).getUserByEmail("email");
        Mockito.verify(teamService, Mockito.times(1)).getTeamsThatUserManagesFromUserId(1L);
        Mockito.verify(formationService, Mockito.times(0)).save(Mockito.any());
        Mockito.verify(ra, Mockito.times(1)).addFlashAttribute("message","Formation could not be saved as you are not the manager.");
    }


    @ParameterizedTest
    @MethodSource("saveFormation_invalidFormation_values")
    void saveFormation_InvalidFormationTest(long teamId, String formationInput) {
        User user = Mockito.mock(User.class);
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(user.getId()).thenReturn(1L);
        List<Team> teams = new ArrayList<>();
        Team team = Mockito.mock(Team.class);
        Mockito.when(team.getId()).thenReturn(teamId);
        teams.add(team);
        Mockito.when(teamService.getTeamsThatUserManagesFromUserId(1L)).thenReturn(teams);

        RedirectAttributes ra =  Mockito.mock(RedirectAttributes.class);

        String out = formationController.saveFormation(formationInput,"football_pitch", teamId,ra);
        Assertions.assertEquals("redirect:/formations?team="+teamId,out);
        Mockito.verify(userService, Mockito.times(1)).getUserByEmail("email");
        Mockito.verify(teamService, Mockito.times(1)).getTeamsThatUserManagesFromUserId(1L);
        Mockito.verify(formationService, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void get_createFormation_nonExistentTeam_homeRedirection() {
        Mockito.when(teamService.getTeam(1)).thenReturn(Optional.empty());
        String out = formationController.createFormation(1L,  Mockito.mock(Model.class));
        Assertions.assertEquals("redirect:/",out);
    }
}

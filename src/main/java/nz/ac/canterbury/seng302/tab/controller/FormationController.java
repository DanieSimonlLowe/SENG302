package nz.ac.canterbury.seng302.tab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.entity.json.FormationJSONObject;
import nz.ac.canterbury.seng302.tab.entity.json.ResponsePlayerObject;
import nz.ac.canterbury.seng302.tab.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


/**
 * a controller for formations.
 * */
@Controller
public class FormationController {

    /** The TeamService for database logic */
    private final TeamService teamService;

    /** The UserService for database logic */
    private final UserService userService;

    /** The FormationService for database logic */
    private final FormationService formationService;

    /** Message string constant */
    private static final String MESSAGE = "message";

    /** Formation input string constant */
    private static final String FORMATION_INPUT = "formationInput";

    /** Redirect formations string constant */
    private static final String REDIRECT_FORMATIONS = "redirect:/formations?team=";


    /** The logger */
    final Logger logger = LoggerFactory.getLogger(FormationController.class);


    /**
     * Constructor for the formation controller
     * @param teamService      the team service
     * @param userService      the user service
     * @param formationService the formation service
     */
    @Autowired
    public FormationController(TeamService teamService, UserService userService, FormationService formationService) {
        this.teamService = teamService;
        this.userService = userService;
        this.formationService = formationService;
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
     * Gets the formation page of a team. this is used to view, edit and create formations for a team.
     * @param teamId the id of the team being viewed.
     * @return the name of the html tag that is displayed
     * */
    @GetMapping("/formations")
    public String createFormation(@RequestParam(name="team") long teamId,
                                  Model model) {
        Team team;
        try{
            team = teamService.getTeam(teamId).orElseThrow();
        } catch (Exception e) {
            return "redirect:/";
        }
        model.addAttribute("team",team);
        model.addAttribute("loggedIn", checkIsLoggedIn());
        model.addAttribute("teamId", teamId);

        List<User> userList = userService.findUsersWhoAreInTeam(team);
        List<String> images = new ArrayList<>();
        for (User user: userList) {
            images.add(user.getProfilePicName());
        }
        model.addAttribute("playersImages",images);

        String defaultSport = "rugby_field";
        if (team.getSport() != null) {
            String sport = team.getSport().toLowerCase();
            if (Arrays.asList("basketball","netball","volleyball").contains(sport)) {
                defaultSport = sport + "_court";
            } else if (Arrays.asList("baseball","rugby","softball").contains(sport)) {
                defaultSport = sport + "_field";
            } else if (Arrays.asList("football","hockey").contains(sport)) {
                defaultSport = sport + "_pitch";
            }
        }
        model.addAttribute("defaultSport", defaultSport);
        List<String> formationImages = Arrays.asList("baseball_field", "basketball_court", "football_pitch", "hockey_pitch", "netball_court", "rugby_field", "softball_field", "volleyball_court");
        List<String> formationSports = Arrays.asList("Baseball", "Basketball", "Football", "Hockey", "Netball", "Rugby", "Softball", "Volleyball");
        model.addAttribute("formationImages", formationImages);
        model.addAttribute("formationSports", formationSports);

        return "formations";
    }

    /**
     * creates a formation with the stated values for which is linked to the team
     * @param teamId the id of the team being viewed.
     * @param formationInput a string of the form "1-2-3" which is put in the formation to create the formation.
     * @return the page that the user is redirected to.
     * */
    @PostMapping("/formations/create")
    public String saveFormation(@RequestParam("formation") String formationInput,
                                @RequestParam("sport") String formationSport,
                                @RequestParam("teamId") long teamId,
                                RedirectAttributes ra) {

        //checks that the user can edit the team
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getUserByEmail(username);
        List<Team> teams = teamService.getTeamsThatUserManagesFromUserId(user.getId());
        Optional<Team> teamOptional =  teams.stream().filter(team -> team.getId() == teamId).findAny();
        if (teamOptional.isEmpty()) {
            ra.addFlashAttribute(MESSAGE,"Formation could not be saved as you are not the manager.");
            ra.addFlashAttribute(FORMATION_INPUT, formationInput);
            return REDIRECT_FORMATIONS+teamId;
        }

        // Validation
        Pattern formationRegex = Pattern.compile("^\\d+(?:-\\d+){0,8}$");
        Matcher matcher = formationRegex.matcher(formationInput);
        if(! matcher.find()) { // Formation doesn't match the regex
            ra.addFlashAttribute(MESSAGE,"Formation could not be saved as it is invalid.");
            ra.addFlashAttribute(FORMATION_INPUT, formationInput);
            return REDIRECT_FORMATIONS+teamId;
        }

        // The number of players can't be greater than 15 per column and there can only be a maximum of 8 columns
        String[] columns = formationInput.split("-");

        if(columns.length > 8) {
            ra.addFlashAttribute(MESSAGE,"A formation can have a maximum of 8 columns of players");
            ra.addFlashAttribute(FORMATION_INPUT, formationInput);
            return REDIRECT_FORMATIONS+teamId;
        }

        for(String val : columns) {
            int num = Integer.parseInt(val);
            if(num > 15) {
                ra.addFlashAttribute(MESSAGE,"Each column in a formation can have a maximum of 15 players");
                ra.addFlashAttribute(FORMATION_INPUT, formationInput);
                return REDIRECT_FORMATIONS+teamId;
            }
        }

        Team team = teamOptional.get();

        try {
            Formation formation = new Formation(formationInput, formationSport, team);
            formationService.save(formation);
            team.addFormation(formation);
            teamService.updateTeam(team);
            ra.addFlashAttribute(MESSAGE,"Formation was saved successfully.");
            return "redirect:/teamProfile?id="+teamId;
        } catch (NumberFormatException | NullPointerException exception) {
            ra.addFlashAttribute(MESSAGE,"Formation could not be saved as it is invalid.");
            ra.addFlashAttribute(FORMATION_INPUT, formationInput);
            return REDIRECT_FORMATIONS+teamId;
        }
    }

    /**
     * Gets the formations for a given team
     * @param id the id of the team
     * @return a Formation response object
     */
    @GetMapping("/formations/{id}")
    public ResponseEntity<String> getTeamsFormations(@PathVariable Long id) {
        // Check logged-in user is a coach or manager of the team
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getUserByEmail(email);
        List<Long> teamIds =  teamService.getTeamNamesWhoUserManageOrCoach(user.getId()).stream().map(Team::getId).toList();
        if (!teamIds.contains(id)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not a coach or manager of the team");
        }

        try {
            Team team = teamService.getTeam(id).orElseThrow();
            List<ResponsePlayerObject> userList = userService.findUsersWhoAreInTeam(team).stream().map(dbUser -> new ResponsePlayerObject(dbUser.getId(), dbUser.getFirstName(), dbUser.getProfilePicName())).toList();
            List<Formation> formationList = formationService.getFormationsByTeamId(id);
            FormationJSONObject response = new FormationJSONObject(id, userList, formationList);
            ObjectMapper objectMapper = new ObjectMapper();

            String jsonString = objectMapper.writeValueAsString(response);
            logger.info("Sending json string {}", jsonString);
            return ResponseEntity.status(HttpStatus.OK).body(jsonString);
        } catch(Exception e) {
            logger.error(e.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request");
        }
    }
}

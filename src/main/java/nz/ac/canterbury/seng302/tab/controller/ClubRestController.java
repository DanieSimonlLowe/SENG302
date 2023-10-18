package nz.ac.canterbury.seng302.tab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.entity.json.ResponseTeamSportObject;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class ClubRestController {

    /** The logger */
    final Logger logger = LoggerFactory.getLogger(ClubRestController.class);

    /** The TeamService for database logic */
    private final TeamService teamService;

    /** The UserService for database logic */
    private final UserService userService;


    /**
     * Constructor for the club rest controller
     * @param teamService the team service
     * @param userService the user service
     */
    @Autowired
    public ClubRestController(TeamService teamService,
                              UserService userService) {
        this.teamService = teamService;
        this.userService = userService;
    }


    /**
     * Gets a list of the user's teams that have the same sport as the given team
     * @param teamId the id of the selected team, whose sport is the one to filter the returned sports by.
     * @return a ResponseEntity containing the list of teams as json
     */
    @GetMapping("/clubs/getTeams/{teamId}")
    public ResponseEntity<String> getTeams(@PathVariable Long teamId) {
        try {
            Team team = teamService.getTeam(teamId).orElseThrow();
            String sport = team.getSport();
            String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userService.getUserByEmail(username);
            List<Team> teamsBySport = teamService.getTeamsBySportName(user.getId(),sport);
            if (teamsBySport.isEmpty()) {
                List<ResponseTeamSportObject> responseTeamSportObjects = List.of(new ResponseTeamSportObject(
                        team.getId(),
                        team.getSport(),
                        team.getName()
                ));
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonString = objectMapper.writeValueAsString(responseTeamSportObjects);
                return ResponseEntity.status(HttpStatus.OK).body(jsonString);
            }
            List<ResponseTeamSportObject> responseTeamSportObjects = teamsBySport.stream().map(teamSport -> new ResponseTeamSportObject(
                    teamSport.getId(),
                    teamSport.getSport(),
                    teamSport.getName()
            )).toList();

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(responseTeamSportObjects);
            return ResponseEntity.status(HttpStatus.OK).body(jsonString);
        } catch (Exception e) {
            logger.info(String.valueOf(e));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request.");
        }
    }
}

package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.exceptions.InvalidTeamException;
import nz.ac.canterbury.seng302.tab.exceptions.NotFoundException;
import nz.ac.canterbury.seng302.tab.service.*;
import nz.ac.canterbury.seng302.tab.service.sorters.TeamSorter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * TeamMemberController for handling api requests to do with team members
 */
@Controller
public class TeamMemberController {

    /** The logger */
    final Logger logger = LoggerFactory.getLogger(TeamMemberController.class);

    /** The TeamService for team database logic */
    private final TeamService teamService;

    /** The User Service for user logic */
    private final UserService userService;

    /** The Team Member Service for team member logic */
    private final TeamMemberService teamMemberService;

    /** The TableController for common table methods */
    private final TableController tableController;

    /** The Formation service for common table methods */
    private final FormationService formationService;

    /** The clubService for getting the club onto the team */
    private final ClubService clubService;

    /** The activityService for getting the team activities */
    private final ActivityService activityService;

    /** Model attribute string constant */
    private static final String IS_MANAGER = "isManager";

    /** Page size constant */
    private static final int PAGE_SIZE = 10;

    /** Url string constant */
    private static final String REDIRECT_HOME = "redirect:/";

    /**
     * Constructor for TeamMemberController class
     *
     * @param teamService       The team service, for handling team related data
     * @param userService       The user service, for handling user related data
     * @param teamMemberService The team member service, for handling team member related data
     * @param tableController   The table controller, for common table methods
     * @param formationService  The formation service, for handling formation related data
     * @param activityService   The activity service, for getting the activities for the team
     */
    @Autowired
    public TeamMemberController(
            TeamService teamService, UserService userService,
            TeamMemberService teamMemberService,
            TableController tableController,
            FormationService formationService,
            ClubService clubService,
            ActivityService activityService) {
        this.teamService = teamService;
        this.userService = userService;
        this.teamMemberService = teamMemberService;
        this.tableController = tableController;
        this.formationService = formationService;
        this.clubService = clubService;
        this.activityService = activityService;
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
     * Gets the joining team form to be displayed
     * @return thymeleaf joinTeam
     */
    @GetMapping("/joinTeam")
    public String getJoinTeam() {
        logger.info("GET /joinTeam");
        return "joinTeam";
    }

    /**
     * Adds the logged-in user to the team associated with the invitation token
     * @param invitationToken the invitation token that corresponds with a team in the repository
     * @param redirectAttributes  attributes to add to the page that the user is redirected to
     * @return redirect to the user profile page or the joinTeam page if the token is invalid
     */
    @PostMapping("/joinTeam")
    public String joinTeam(@RequestParam(name="invitationToken") String invitationToken,
                           RedirectAttributes redirectAttributes) {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getUserByEmail(email);
        TeamMember teamMember = teamMemberService.addTeamMember(user, invitationToken);
        if(teamMember == null) {
            redirectAttributes.addFlashAttribute("error", "The token is not associated with a team or you already belong to this team.");
            return "redirect:/joinTeam";
        }
        user.followTeam(teamMember.getTeamMemberId().getTeam());
        userService.save(user);
        return "redirect:/teamProfile?id=" + teamMember.getTeamMemberId().getTeam().getId();
    }

    /**
     * Gets the temporary profile page of a team
     * @param id    the id of the team to be displayed
     * @param isVisibleMembers if the members section is expanded or not
     * @param managerIds the ids of the temporary managers
     * @param coachIds the ids of the temporary coaches
     * @param memberIds the ids of the temporary members
     * @param userId the id of the user to be changed
     * @param newRole the new role of the user to be changed
     * @param redirectAttributes  attributes to add to the page that the user is redirected to
     * @param model (map-like) representation of results to be used by thymeleaf
     * @return teamProfileView
     */
    @GetMapping("/teamProfileTemp")
    public String getTeamProfileTemp(@RequestParam(name = "id") Long id,
                                     @RequestParam(name = "isVisibleMembers", defaultValue = "true") String isVisibleMembers,
                                     @RequestParam(name = "isVisibleFormations", defaultValue = "false") String isVisibleFormations,
                                     @RequestParam(name = "managerIds") List<Long> managerIds,
                                     @RequestParam(name = "coachIds") List<Long> coachIds,
                                     @RequestParam(name = "memberIds") List<Long> memberIds,
                                     @RequestParam(name = "userId") Long userId,
                                     @RequestParam(name = "newRole") String newRole,
                                     RedirectAttributes redirectAttributes,
                                     Model model) {
        logger.info("GET /teamProfileTemp");
        model.addAttribute(IS_MANAGER, checkIsManager(id));
        model.addAttribute("isMember", checkIsInTeam(id));

        Team team;
        try {
            team = teamService.getTeam(id).orElseThrow();
        } catch (NoSuchElementException e) {
            logger.error(e.toString());
            return REDIRECT_HOME;
        }

        managerIds.remove(userId);
        coachIds.remove(userId);
        memberIds.remove(userId);

        List<TeamMember> managers;
        List<TeamMember> coaches;
        List<TeamMember> members;

        TeamMember teamMember;
        User user;

        try {
            managers = teamMemberService.getTeamMembersFromUserIdsAndTeam(managerIds, team);
            coaches = teamMemberService.getTeamMembersFromUserIdsAndTeam(coachIds, team);
            members = teamMemberService.getTeamMembersFromUserIdsAndTeam(memberIds, team);

            user = userService.getUser(userId).orElseThrow(NotFoundException::new);
            TeamMemberId teamMemberId = new TeamMemberId(user, team);
            teamMember = teamMemberService.getTeamMember(teamMemberId).orElseThrow(NotFoundException::new);
        } catch (NotFoundException e) {
            logger.warn("User or team member doesn't exist in the database");
            redirectAttributes.addFlashAttribute("status", "A specified user or team member doesn't exist in the database");
            return "redirect:/teamProfile?id="+id;
        }

        switch (newRole) {
            case "Manager" -> {
                managers.add(teamMember);
                managerIds.add(userId);
            }
            case "Coach" -> {
                coaches.add(teamMember);
                coachIds.add(userId);
            }
            case "Member" -> {
                members.add(teamMember);
                memberIds.add(userId);
            }
            default -> {
                return REDIRECT_HOME;
            }
        }

        List<Activity> myTeamActivities;
        myTeamActivities = activityService.getActivitiesByTeamId(id);

        model.addAttribute("activities", myTeamActivities);

        model.addAttribute("displayId", id.toString());
        model.addAttribute("isVisibleMembers", isVisibleMembers);
        model.addAttribute("isVisibleFormations", isVisibleFormations);

        model.addAttribute("managers", managers);
        model.addAttribute("coaches", coaches);
        model.addAttribute("members", members);

        model.addAttribute("managerIds", managerIds);
        model.addAttribute("coachIds", coachIds);
        model.addAttribute("memberIds", memberIds);

        model.addAttribute("isLoggedIn", true);
        model.addAttribute("canFollowTeam", !teamService.isFollowingTeam(user,team));
        model.addAttribute("followingTeam", teamService.isFollowingTeam(user,team));

        setTeamModelAttributes(model, team);

        return "teamProfileViewTemp";
    }

    /**
     * Sets the team member roles for a given team in the database
     * @param id the id of the team
     * @param managerIds the ids of the managers
     * @param coachIds the ids of the coaches
     * @param memberIds the ids of the members
     * @return the team profile page with the changes saved
     */
    @PostMapping("/teamMembers")
    public String setTeamRoles(@RequestParam(name = "id") Long id,
                               @RequestParam(name = "managerIds") List<Long> managerIds,
                               @RequestParam(name = "coachIds") List<Long> coachIds,
                               @RequestParam(name = "memberIds") List<Long> memberIds,
                               @RequestParam(name = "selectedTab", defaultValue = "profile-tab") String selectedTab,
                               RedirectAttributes redirectAttributes) {
        logger.info("POST /teamMembers");
        Team team;
        try {
            team = teamService.getTeam(id).orElseThrow();
        } catch (NoSuchElementException e) {
            logger.error(e.toString());
            return REDIRECT_HOME;
        }
        redirectAttributes.addAttribute("isVisibleMembers", "true");
        if (checkIsManager(id)) {
            List<TeamMember> managers;
            List<TeamMember> coaches;
            List<TeamMember> members;

            try {
                managers = teamMemberService.getTeamMembersFromUserIdsAndTeam(managerIds, team);
                coaches = teamMemberService.getTeamMembersFromUserIdsAndTeam(coachIds, team);
                members = teamMemberService.getTeamMembersFromUserIdsAndTeam(memberIds, team);

                teamMemberService.updateTeamMemberRoles(managers, Role.MANAGER);
                teamMemberService.updateTeamMemberRoles(coaches, Role.COACH);
                teamMemberService.updateTeamMemberRoles(members, Role.MEMBER);
            } catch (InvalidTeamException e) {
                logger.warn("Invalid team, reverting changes");
                redirectAttributes.addFlashAttribute("status", "Unable to change roles: " + e.getMessage());
                return "redirect:/teamProfile?id="+id;
            } catch (NotFoundException e) {
                logger.warn("User or team member doesn't exist in the database");
                redirectAttributes.addFlashAttribute("status", "A specified user or team member doesn't exist in the database");
                return "redirect:/teamProfile?id="+id;
            }

        } else {
            redirectAttributes.addFlashAttribute("status", "You are not a manager in this team!");
        }
        redirectAttributes.addFlashAttribute("status", "Changes saved");
        redirectAttributes.addFlashAttribute("selectedTab", selectedTab);
        return "redirect:/teamProfile?id="+id;
    }

    /**
     * Gets the page showing the teams the current logged-in user is in
     * @param page current page
     * @param model (map-like) representation of results to be used by thymeleaf
     * @return the myTeams page
     */
    @GetMapping("/myTeams")
    public String getMyTeams(@RequestParam(value="page", defaultValue = "1") Integer page,
                             Model model) {
        logger.info("GET /myTeams");

        // Check the teams the current user is in
        User currentUser = userService.getUserByEmail((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        List<TeamMember> teamMembers = teamMemberService.getAllTeamsFromUser(currentUser);
        List<Team> teamsList = teamMembers.stream().map(teamMember -> teamMember.getTeamMemberId().getTeam()).toList();
        ArrayList<Team> teams = new ArrayList<>(teamsList);

        // Sort teams if length greater than 1
        if (teams.size() > 1) { TeamSorter.sort(teams); }

        // Set up pageable object
        Page<Team> teamsPage = tableController.convertToPage(page, PAGE_SIZE, teams);

        // Set model attributes
        tableController.setModelAttributes(model, teamsPage, "myTeams", page);

        // For each team, get their corresponding club
        List<Club> filteredTeamClubs = new ArrayList<>(teams.size());
        for(Team filteredTeam : teams) {
            Optional<Club> teamClubIfExists = clubService.getClubFromTeam(filteredTeam.getId());
            if(teamClubIfExists.isPresent()) {
                filteredTeamClubs.add(teamClubIfExists.get());
            } else {
                filteredTeamClubs.add(null);
            }
        }
        model.addAttribute("teamClubs", filteredTeamClubs);

        // Add empty table message if the table is empty
        if (teams.isEmpty()) { model.addAttribute("noTeamsDisplay", "You are not currently in a team"); }

        return "myTeams";
    }

    /**
     * Sets the model attributes for team members initially, given a team, using entries in the database
     * @param model (map-like) representation of results to be used by thymeleaf
     * @param team the team to get the entries for
     */
    public void setTeamMemberModelAttributes(Model model, Team team) {
        List<TeamMember> managers =  teamMemberService.getAllManagersFromTeam(team);
        List<TeamMember> coaches = teamMemberService.getAllCoachesFromTeam(team);
        List<TeamMember> members = teamMemberService.getAllMembersFromTeam(team);

        List<Long> managerIds = teamMemberService.getAllManagersFromTeam(team)
                .stream()
                .map(manager -> manager.getTeamMemberId().getUser().getId())
                .collect(Collectors.toList());

        List<Long> coachIds = teamMemberService.getAllCoachesFromTeam(team)
                .stream()
                .map(coach -> coach.getTeamMemberId().getUser().getId())
                .collect(Collectors.toList());

        List<Long> memberIds = teamMemberService.getAllMembersFromTeam(team)
                .stream()
                .map(member -> member.getTeamMemberId().getUser().getId())
                .collect(Collectors.toList());


        model.addAttribute("managers", managers);
        model.addAttribute("coaches", coaches);
        model.addAttribute("members", members);

        model.addAttribute("managerIds", managerIds);
        model.addAttribute("coachIds", coachIds);
        model.addAttribute("memberIds", memberIds);

    }

    /**
     * Sets the model attributes of a given team
     * @param model (map-like) representation of results to be used by thymeleaf
     * @param team the team to get the entries for
     */
    public void setTeamModelAttributes(Model model, Team team) {
        model.addAttribute("inviteToken", team.getTeamToken());
        model.addAttribute("teamId", team.getId());
        model.addAttribute("displayName", team.getName());
        model.addAttribute("displayLocation", team.getLocationEditString());
        model.addAttribute("displaySport", team.getSport());
        model.addAttribute("displayLocationString", team.getLocationString());
        model.addAttribute("image", team.getProfilePicName());
        List<Formation> formations = formationService.getFormationsByTeamId(team.getId());
        model.addAttribute("teamFormations", formations);
        model.addAttribute("formationsPresent", !formations.isEmpty());
    }

    /**
     * Checks if the logged-in user is a manager of the given team
     * @return boolean of whether the logged-in user is a manager of the given team
     */
    public boolean checkIsManager(Long teamId) {
        Team team;
        try {
            team = teamService.getTeam(teamId).orElseThrow();
        } catch (NoSuchElementException e) {
            logger.error(e.toString());
            return false;
        }

        if (checkIsLoggedIn()) {
            Long currentUserId = userService.getUserByEmail((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
            List<Long> usersIds = teamMemberService.getAllManagersFromTeam(team).stream().map(manager -> manager.getTeamMemberId().getUser().getId()).toList();
            return (usersIds.contains(currentUserId));
        }
        return false;
    }

    /**
     * Checks if the current logged-in user is part of a team or not.
     * @param teamId The id of the team being checked for membership
     * @return boolean of if user is in team
     */
    public boolean checkIsInTeam(Long teamId) {
        Team team;
        try {
            team = teamService.getTeam(teamId).orElseThrow();
        } catch (NoSuchElementException e) {
            logger.error(e.toString());
            return false;
        }
        if (checkIsLoggedIn()) {
            Long currentUserId  = userService.getUserByEmail((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
            List<Long> userIds = teamMemberService.getAllTeamMembersFromTeam(team).stream().map(teamMember -> teamMember.getTeamMemberId().getUser().getId()).toList();
            return (userIds.contains(currentUserId));
        }
        return false;
    }
}

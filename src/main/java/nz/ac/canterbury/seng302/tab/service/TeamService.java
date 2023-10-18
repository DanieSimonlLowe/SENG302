package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.exceptions.InvalidTeamException;
import nz.ac.canterbury.seng302.tab.exceptions.NotFoundException;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The service for saving and querying sports to and from the database.
 */
@Service
public class TeamService {

    /** The logger */
    final Logger logger = LoggerFactory.getLogger(TeamService.class);

    /** The team repository */
    private final TeamRepository teamRepository;

    /** The sports service */
    private final SportService sportService;

    /** The user service */
    private final UserService userService;

    /** The team member service */
    private final TeamMemberService teamMemberService;

    @Autowired
    public TeamService(TeamRepository teamRepository, SportService sportService, UserService userService, TeamMemberService teamMemberService) {
        this.teamRepository = teamRepository;
        this.sportService = sportService;
        this.userService = userService;
        this.teamMemberService = teamMemberService;
    }

    /**
     * Gets all Teams from persistence
     * @return all Teams currently saved in persistence
     */
    public List<Team> getTeams() {
        return teamRepository.findAll();
    }

    /**
     * Gets all Teams from persistence whose city is one of the given cities and/or team plays one of the given sports
     * @param cities search parameter
     * @param sports search parameter
     * @return all Teams currently saved in persistence whose city matches one of the given cities and/or team
     * matches one of the given sports
     */
    public List<Team> getTeamsFiltered(List<String> cities, List<Long> sports) {
        if (cities.isEmpty() && sports.isEmpty()) {
            return teamRepository.findAll();
        } else if (sports.isEmpty()) {
            return teamRepository.findByCity(cities);
        } else if (cities.isEmpty()) {
            return teamRepository.findBySport(sports);
        } else {
            return teamRepository.findBySportAndCity(sports, cities);
        }
    }

    /**
     * Gets all Teams from persistence whose city is one of the given cities and/or team plays one of the given sports
     * and has a name or location like the given keyword
     * @param keyword search parameter
     * @param cities search parameter
     * @param sports search parameter
     * @return all Teams currently saved in persistence whose city matches one of the given cities and/or team
     * matches one of the given sports and name or location matches the given keyword
     */
    public List<Team> getTeamsFilteredSearch(String keyword, List<String> cities, List<Long> sports) {
        keyword = keyword.toUpperCase();
        if (cities.isEmpty() && sports.isEmpty()) {
            return teamRepository.findByKeyword(keyword);
        } else if (sports.isEmpty()) {
            return teamRepository.findByCityAndKeyword(cities, keyword);
        } else if (cities.isEmpty()) {
            return teamRepository.findBySportAndKeyword(sports, keyword);
        } else {
            return teamRepository.findBySportAndCityAndKeyword(sports, cities, keyword);
        }
    }

    /**
     * Gets a team from persistence
     * @param id of team to retrieve
     * @return team with matching id
     */
    public Optional<Team> getTeam(long id) { return teamRepository.findById(id); }

    /**
     * Adds a team to persistence.
     * While it does that it also sets the team's owner.
     * @param team object to persist
     * @return the saved team object
     */
    public Team addTeam(Team team) {
        String sportName = team.getSport();
        Sport sport = new Sport(sportName);
        if(!sportService.existsBySportName(sport.getSportName())) { sportService.add(sport); }
        User manager = userService.getUserByEmail((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        Team newTeam = teamRepository.save(team);
        TeamMember teamMember = teamMemberService.addTeamMember(manager, team.getTeamToken());
        try {
            teamMemberService.changeRole(teamMember, Role.MANAGER);
        } catch (NotFoundException e) {
            logger.warn("Team does not exist");
        } catch (InvalidTeamException e) {
            logger.warn("Unable to add manager: " + e.getMessage());
        }
        return newTeam;
    }


    /**
     * Updates a team in persistence.
     * @param team object to persist
     * @return the saved team object
     */
    public Team updateTeam(Team team) {
        String sportName = team.getSport();
        Sport sport = new Sport(sportName);
        if(!sportService.existsBySportName(sport.getSportName())) { sportService.add(sport); }
        return teamRepository.save(team);
    }

    /**
     * Gets all teams from persistence that have a name, location or sport like the keyword
     * @param keyword to search for
     * @return the matching teams
     */
    public List<Team> getByKeyword(String keyword) { return teamRepository.findByKeyword(keyword.toUpperCase()); }


    /**
     * Gets all teams from persistence
     * @param page represents the current page that the user is on
     * @param size represents how many table rows can be used before a new page is needed
     * @return A page of all teams in persistence
     */
    public Page<Team> getPageTeams(int page, int size) {
        return teamRepository.findAll(PageRequest.of(page, size));
    }

    /**
     * Saves the modified team to persistence
     * @param team is the team a new profile picture has been given to.
     */
    public void addProfilePic(Team team) {teamRepository.save(team);}

    /**
     * Gets all distinct cities that the given teams are located in from persistence
     * @param teams list of teams
     * @return list of distinct cities that the given teams are located in
     */
    public List<String> getCitiesFromTeams(List<Team> teams) {
        List<Long> teamIDs = new ArrayList<>();
        for (Team team : teams) {
            teamIDs.add(team.getId());
        }
        return teamRepository.findDistinctCityFromTeams(teamIDs);
    }

    /**
     * Gets all teams managed by the user associated with a specific id
     * @param id the id of the manager
     * @return A list of all teams managed by the owner
     */

    public List<Team> getTeamsThatUserManagesFromUserId(long id) {
        final int[] roles = {Role.MANAGER.ordinal()};
        return teamRepository.findTeamsThatUserHasRoles(id, roles);
    }


    /**
     * gets all teams that the user manages or coaches.
     * @param id the id of the user
     * @return list of teams.
     * */
    public List<Team> getTeamNamesWhoUserManageOrCoach(long id) {
        final int[] roles = {Role.COACH.ordinal(), Role.MANAGER.ordinal()};
        return teamRepository.findTeamsThatUserHasRoles(id, roles);
    }

    public List<Team> getTeamsBySportName(long id,String keyword) {
        return teamRepository.findTeamsBySportName(id, keyword);
    }

    public List<Formation> getFormationsByTeamId(long id) {
        return teamRepository.findFormationsByTeamId(id);
    }

    /**
     * gets all teams that are not part of any club
     * @return a list of all teams that are not part of any club.
     * */
    public List<Team> getFreeTeams() {
        return teamRepository.findFreeTeams();
    }


    /**
     * gets all teams that the user manages or coaches and that are not part of any club.
     * @param id the id of the user
     * @return list of teams.
     * */
    public List<Team> getFreeTeamsThatUserManagesOrCoachesFromUserId(long id) {
        final int[] roles = {Role.MANAGER.ordinal(), Role.COACH.ordinal()};
        return teamRepository.getFreeTeamNamesWhoUserManageOrCoach(id, roles);
    }

    /**
     * Gets the list of teams that the user follows
     * @param user the user whose followed teams are returned
     * @return a list of teams that the user follows
     */
    public List<Team> getFollowedTeams(User user) {
        return teamRepository.findTeamsByFollowersId(user.getId());

    }

    /**
     * Checks whether the given user is following the given team.
     * @param user the user who may be following the team
     * @param team the team the user may be following
     * @return a boolean of whether the user is following the team
     */
    public boolean isFollowingTeam(User user, Team team) {
        return getFollowedTeams(user).contains(team);
    }


    /**
     * gets the percent of users who are in a team that are also in a team that the user is in
     * @param user the user that is being checked against.
     * @param team the team that is being checked against.
     * @return a float from 0 to 1 that is the percent of users who share a team with the user.
     * */
    public float getPercentOfUsersWhoShareTeam(User user, Team team) {
        int total = teamMemberService.getAllTeamMembersFromTeam(team).size();
        if (total < 1) {
            return 0;
        }
        return teamRepository.countUsersInTeamWhoAreInTeamWithUser(team.getId(),user.getId()) / (float)total;
    }


    /**
     * gets the 3 teams with the highest user suggestion metric for the input user
     * @param user the user that the users are being suggested to
     * @return a list of length 3 that contains the 3 list with the three teams with the highest suggestion metric in no particular order.
     * */
    public List<Team> getRecommendedTeams(User user) {
        List<Pair<Float,Team>> bestSoFar = new ArrayList<>();
        float minWeight = Float.MAX_VALUE;
        int minWeightPos = -1;

        for (Team team : teamRepository.findAll()) {

            List<TeamMember> teamMembers = teamMemberService.getAllTeamMembersFromTeam(team);
            if (teamMembers.stream().anyMatch(teamMember -> teamMember.getTeamMemberId().getUser().getId().equals(user.getId()) )) {
                continue;
            }

            float weight = team.getSuggestionMetric(user,this);

            if (bestSoFar.size() < 3) {
                if (weight < minWeight) {
                    minWeight = weight;
                    minWeightPos = bestSoFar.size();
                }
                bestSoFar.add(new Pair<>(weight,team));

            } else if (weight > minWeight) {
                bestSoFar.remove(minWeightPos);
                if (bestSoFar.get(0).getLeft() > bestSoFar.get(1).getLeft()) {
                    minWeight = bestSoFar.get(1).getLeft();
                    minWeightPos = 1;
                } else {
                    minWeight = bestSoFar.get(0).getLeft();
                    minWeightPos = 0;
                }
                bestSoFar.add(new Pair<>(weight,team));
                if (weight < minWeight) {
                    minWeight = weight;
                    minWeightPos = 2;
                }
            }
        }

        List<Team> teams = new ArrayList<>();
        for (Pair<Float,Team> pair : bestSoFar) {
            teams.add(pair.getRight());
        }
        return teams;
    }
}

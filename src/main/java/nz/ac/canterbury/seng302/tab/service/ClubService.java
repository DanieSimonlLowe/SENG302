package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Club;
import nz.ac.canterbury.seng302.tab.entity.Role;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.ClubRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The service for saving and querying clubs to and from the database.
 */
@Service
public class ClubService {
    ClubRepository clubRepository;

    UserRepository userRepository;


    UserService userService;
    @Autowired
    public ClubService(ClubRepository clubRepository,
                       UserRepository userRepository,
                       UserService userService) {
        this.clubRepository = clubRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public void save(Club club) {
        clubRepository.save(club);
    }

    /**
     * Returns an optional club that the team is a part of
     * @param id The id of the team
     * @return A club
     */
    public Optional<Club> getClubFromTeam(Long id) {
        return clubRepository.findClubByTeamId(id);
    }

    /**
     * Checks if a given team (id) is already part of another club.
     * @param id the id of the team
     * @return true if the team is part of a club, false if not.
     */
    public boolean checkIfTeamAlreadyInClub(Long id, long clubId) {
        List<Long> team = clubRepository.findTeamClubByTeamId(id, clubId);
        return !team.isEmpty();
    }

    /**
     * Checks if a given team (id) is already part of another club.
     * @param id the id of the team
     * @return true if the team is part of a club, false if not.
     */
    public boolean checkIfTeamAlreadyInClubNoClubId(Long id) {
        List<Long> team = clubRepository.findTeamClubNoClubIdByTeamId(id);
        return !team.isEmpty();
    }

    /**
     * Checks whether a given team can join a club, based on the team's selected sport.
     * @param clubId the id of the club the team wants to join
     * @param team the team that wants to join
     * @return true or false whether the team can join the club
     */
    public boolean checkIfCanAddTeam(Long clubId, Team team) {
        List<String> clubSport = clubRepository.findClubTeamSport(clubId);
        if (!clubSport.isEmpty()) {
            return clubSport.get(0).equalsIgnoreCase(team.getSport());
        } else {
            return true;
        }
    }

    public void addProfilePic(Club club) {
        clubRepository.save(club);
    }
    public Optional<Club> getClub(long id) {
        return clubRepository.findById(id);
    }

    public boolean managerCheck(long id) {
        Integer[] roles = {Role.COACH.ordinal(), Role.MANAGER.ordinal()};

        User user = userService.getUserByEmail((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        if (user == null) {
            return false;
        }

        return clubRepository.findIfTeamMemberHasRole(roles, user.getId(), id).isPresent();
    }

    public Optional<Club> getClubByManager(long id) {
        return clubRepository.findClubsByUserId(id);
    }

    public List<Club> getClubs() {
        return clubRepository.findAll();
    }

    /**
     * Gets all clubs from persistence whose city is one of the given cities and/or club plays one of the given sports
     * @param cities search parameter
     * @param sports search parameter
     * @return all clubs currently saved in persistence whose city matches one of the given cities and/or club
     * matches one of the given sports
     */
    public List<Club> getClubsFiltered(List<String> cities, List<Long> sports) {
        if (cities.isEmpty() && sports.isEmpty()) {
            return clubRepository.findAll();
        } else if (sports.isEmpty()) {
            return clubRepository.findByCity(cities);
        } else if (cities.isEmpty()) {
            return clubRepository.findBySport(sports);
        } else {
            return clubRepository.findBySportAndCity(sports, cities);
        }
    }

    /**
     * Gets all clubs from persistence whose city is one of the given cities and/or club plays one of the given sports
     * and has a name or location like the given keyword
     * @param keyword search parameter
     * @param cities search parameter
     * @param sports search parameter
     * @return all clubs currently saved in persistence whose city matches one of the given cities and/or clubs
     * matches one of the given sports and name or location matches the given keyword
     */
    public List<Club> getClubsFilteredSearch(String keyword, List<String> cities, List<Long> sports) {
        keyword = keyword.toUpperCase();
        if (cities.isEmpty() && sports.isEmpty()) {
            return clubRepository.findByKeyword(keyword);
        } else if (sports.isEmpty()) {
            return clubRepository.findByCityAndKeyword(cities, keyword);
        } else if (cities.isEmpty()) {
            return clubRepository.findBySportAndKeyword(sports, keyword);
        } else {
            return clubRepository.findBySportAndCityAndKeyword(sports, cities, keyword);
        }
    }
    public List<Club> getByKeyword(String keyword) {
        return clubRepository.findByKeyword(keyword.toUpperCase());
    }

    public List<String> getCitiesFromClubs(List<Club> clubs) {
        List<Long> longs = new ArrayList<>();
        for (Club club : clubs) {
            longs.add(club.getId());
        }

        return clubRepository.findDistinctCityFromClubs(longs);
    }

    /**
     * Gets all users from a given club
     * @param clubId the id of the club
     * @return a list of users that are in the club
     */
    public List<User> getAllUsersFromClub(long clubId) {
        List<Long> clubUserIds = clubRepository.findUsersInAClub(clubId);
        return userRepository.findAllById(clubUserIds);
    }

    /**
     * Gets all clubs from a given user
     * @param currentUser the user
     * @return a list of clubs that the user is in
     */
    public List<Club> getAllClubsFromUser(User currentUser) {
        return clubRepository.findAllClubsByUser(currentUser.getId());
    }
}

package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.repository.AuthorityRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * The service for saving and querying users to and from the database.
 */
@Service
public class UserService {

    /** The logger */
    final Logger logger = LoggerFactory.getLogger(UserService.class);

    /** The user repository */
    private final UserRepository userRepository;

    /** The authority repository */
    private final AuthorityRepository authorityRepository;

    /**
     * The constructor for the UserService class
     * @param userRepository the user repository
     * @param authorityRepository the authority repository
     */
    public UserService(UserRepository userRepository, AuthorityRepository authorityRepository) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
    }

    /**
     * Saves the user to the database
     * @param user the user to be saved to the database
     * @return the updated user object
     */
    public User save(User user) {
        Authority authority = new Authority("ROLE_USER");
        authorityRepository.save(authority);
        user.grantAuthority(authority);
        return userRepository.save(user);
    }

    /**
     * Finds all the users in the database
     * @return all the users from the database
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Gets the user with the matching id in the database
     * @param id id of the user
     * @return mathcing user
     */
    public Optional<User> getUser(Long id) { return userRepository.findById(id); }

    /**
     * Searches for users whose first or last name matches the search string
     * @param search the search string to search users against
     * @return a list of users that match the search string
     */
    public List<User> findByFirstOrLastName(String search) {
        List<String> individualNames = Arrays.asList(search.split(" "));

        Set<User> out = new LinkedHashSet<>(userRepository.findUsersByFirstNameContainsIgnoreCase(individualNames.get(0)));
        if (individualNames.size() > 2) {
            for (int i = 1; i < individualNames.size(); i++) {
                out.addAll(userRepository.findUsersByFirstNameContainsIgnoreCase(individualNames.get(i)));
            }
        }
        out.addAll(userRepository.findUsersByLastNameContainsIgnoreCase(individualNames.get(individualNames.size() - 1)));
        return new ArrayList<>(out);
    }

    /**
     * Gets a list of users that like one or more of the given sports
     * @param sports the list of sports
     * @return       the list of users
     */
    public List<User> getUsersByFavSports(List<Long> sports) { return userRepository.findByFavSportsIn(sports); }

    /**
     * Searches for users whose email matches the search string
     * @param email the email to search for
     * @return the user whose email matches the email given
     */
    public User getUserByEmail(String email) {

        User user = userRepository.findByEmailIgnoreCase(email);
        if (user == null) {
            return null;
        } if (user.getIsActive()) {
            return user;
        } else {
            return null;
        }
    }

    /**
     * Searches for users whose first name matches the search string
     * @param firstName the first name search string
     * @return a list of users whose first names match the search string
     */
    public List<User> getUsersByFirstNameContains(String firstName) { return userRepository.findUsersByFirstNameContainsIgnoreCase(firstName); }

    /**
     * Searches for users whose last name matches the search string
     * @param lastName the last name search string
     * @return a list of users whose last names match the search string
     */
    public List<User> getUsersByLastNameContains(String lastName) { return userRepository.findUsersByLastNameContainsIgnoreCase(lastName); }

    /**
     * Gets a list of users that have favourited the given sport from persistence
     * @param sport the sport
     * @return a list of users that have favourited the given sport from persistence
     */
    public List<User> findUsersBySports(Sport sport) { return userRepository.findByFavSports(sport); }

    /**
     * Filter the users based on the filter parameter.
     * @param selectedSports - A list of the sports the user has selected
     * @return A list of User objects that match the filters given.
     */
    public List<User> filterUsers(List<String> locations, List<Long> selectedSports) {

        // List<User> filteredUsers;
        if(selectedSports.isEmpty() && locations.isEmpty()) {
            // No selected sports so get all users
            return userRepository.findAll();
        } else if(selectedSports.isEmpty()) {
            return userRepository.findByLocationIn(locations);
        } else if(locations.isEmpty()) {
            return userRepository.findByFavSportsIn(selectedSports);
        }
        return userRepository.findByFavSportsInAndLocation(locations ,selectedSports);
    }

    /**
     * Filter the users based on the filter parameters.
     * @param keyword        the search string to search the users against
     * @param locations      the locations to filter the users by
     * @param selectedSports the sports to filter the users by
     * @return a list of users that match the search string, and who have been filtered by their location and favourite sports
     */
    public List<User> filterUsersKeyword(String keyword, List<String> locations, List<Long> selectedSports) {
        keyword = keyword.toUpperCase();
        if (locations.isEmpty() && selectedSports.isEmpty()) {
            return findByFirstOrLastName(keyword);
        } else if (selectedSports.isEmpty()) {
            logger.info("Find by location");
            List<User> test = userRepository.findByLocationInAndKeyword(locations, keyword); // Find by location
            logger.info(test.toString());
            return test;
        } else if (locations.isEmpty()) {
            return userRepository.findBySportInAndKeyword(selectedSports, keyword);
        }

        return userRepository.findByFavSportsInAndLocationAndKeyword(locations, selectedSports, keyword);
    }

    /**
     * Gets all distinct cities that the given users are located in from persistence
     * @param users list of users
     * @return list of distinct cities that the given users are located in
     */
    public List<String> getCitiesFromUsers(List<User> users) {
        List<Long> userIDs = new ArrayList<>();
        for (User user : users) {
            userIDs.add(user.getId());
        }
        return userRepository.findDistinctCityFromUsers(userIDs);
    }

    public User findByUpdateToken(String token) {
        return userRepository.findByUpdateToken(token);
    }

    /**
     * gets a list of all the users who are a member of a team.
     * @param team the team that the members are got from.
     * @return a list of users that are in a team
     * */
    public List<User> findUsersWhoAreInTeam(Team team) {
        return userRepository.findUsersWhoAreInTeam(team.getId());
    }

    /**
     * Gets a list of users that are friends with the given user
     * @param user the user
     * @return a list of users that are friends with the given user
     */
    public List<User> getFriends(User user) {
        List<Long> friends = userRepository.findFriends(user.getId());
        List<User> friendList = new ArrayList<>();
        try {
            for (Long friend : friends) {
                friendList.add(userRepository.findById(friend).orElseThrow());
            }
        } catch (NoSuchElementException e) {
            logger.error("User not found");
        }
        return friendList;
    }

    /**
     * Returns whether the two users are friends with each other
     * @param user1 the first user
     * @param user2 the second user
     * @return whether the two users are friends with each other
     */
    public boolean areFriends(User user1, User user2) {
        return getFriends(user1).contains(user2);
    }

    /**
     * Gets a list of users that are followed by the given user
     * @param userId the id of the user
     * @return a list of users that are followed by the given user
     */
    public List<User> findFollowedUsersByUserId(Long userId) {
        return userRepository.findFollowedUsersByUserId(userId);
    }

    /**
     * Gets a list of users from a list of user ids
     * @param userIds the list of user ids
     * @return a list of users from a list of user ids
     */
    public List<User> findUserFromId(List<Long> userIds) {
        return userRepository.findAllById(userIds);
    }


    /**
     * Counts all the teams that two users are both in
     * @param user1 the first user
     * @param user2 the second user
     * @return the number of teams that the users shear in common
     * */
    public int countSameTeamMemberShip(User user1, User user2) {
        return userRepository.countTeamsInCommon(user1.getId(),user2.getId());
    }

    /**
     * Counts all the clubs that two users are both in
     * @param user1 the first user
     * @param user2 the second user
     * @return the number of clubs that the users shear in common
     * */
    public boolean hasSameClubMemberShip(User user1, User user2) {
        return userRepository.hasClubInCommon(user1.getId(),user2.getId()) > 0;
    }

    /**
     * gets the 3 users with the highest user suggestion metric for the input user
     * @param user the user that the users are being suggested to
     * @return User[] an array of length 3 that contains the 3 users with the tree highest suggestion metric in no particular order.
     * */
    public User[] getRecommendedUsers(User user) {
        List<Pair<Float,User>> bestSoFar = new ArrayList<>();
        float minWeight = Float.MAX_VALUE;
        int minWeightPos = -1;

        for (User user1 : findAll()) {

            if (Objects.equals(user1.getId(), user.getId())) {
                continue;
            }
            float weight = user1.getSuggestionMetric(user,this);

            if (bestSoFar.size() < 3) {
                if (weight < minWeight) {
                    minWeight = weight;
                    minWeightPos = bestSoFar.size();
                }
                bestSoFar.add(new Pair<>(weight,user1));

            } else if (weight > minWeight) {
                bestSoFar.remove(minWeightPos);
                if (bestSoFar.get(0).getLeft() > bestSoFar.get(1).getLeft()) {
                    minWeight = bestSoFar.get(1).getLeft();
                    minWeightPos = 1;
                } else {
                    minWeight = bestSoFar.get(0).getLeft();
                    minWeightPos = 0;
                }
                bestSoFar.add(new Pair<>(weight,user1));
                if (weight < minWeight) {
                    minWeight = weight;
                    minWeightPos = 2;
                }
            }
        }

        User[] out = {null,null,null};
        out[0] = bestSoFar.get(0).getRight();
        out[1] = bestSoFar.get(1).getRight();
        out[2] = bestSoFar.get(2).getRight();
        return out;
    }

}

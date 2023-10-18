package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.service.checkers.RegistrationChecker;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Custom User class.
 *
 */
@Entity
@Table(name = "tab_users")
public class User {

    /** The user's id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    /** The user's email */
    @Column(nullable = false)
    private String email;

    /** The user's first name */
    @Column(nullable = false)
    private String firstName;

    /** The user's last name */
    @Column(nullable = false)
    private String lastName;

    /** The user's day of birth */
    @Column(nullable = false)
    private int dateOfBirthDay;

    /** The user's month of birth */
    @Column(nullable = false)
    private int dateOfBirthMonth;

    /** The user's year of birth */
    @Column(nullable = false)
    private int dateOfBirthYear;

    /** * The user's privacy type */
    @Column(nullable = false, columnDefinition = "integer default 0")
    private PrivacyType privacyType = PrivacyType.PRIVATE;

    /** The user's favourite sports */
    @ManyToMany
    @JoinTable(
        name = "sport_fav",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "sport_id"))
    List<Sport> favSports;

    /**
     *  A list of all the teams that the user follows
     * */
    @ManyToMany
    @JoinTable(
            name = "team_following",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "team_id"))
    private List<Team> teamsFollowing = new ArrayList<>();


    /**
     * A list of all the users that the user follows
     * */
    @ManyToMany
    @JoinTable(
            name = "user_following",
            joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name="following_id")
    )
    private List<User> following = new ArrayList<>();

    /** The user's password hash */
    @Column(nullable = false)
    private String passwordHash;

    /** The name of the user's profile picture */
    @Column
    private String profilePicName;

    @Column()
    private String updateToken;

    /** The user's roles */
    @Column()
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private List<Authority> userRoles;

    /** The user's location */
    @OneToOne(cascade=CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name="location_id")
    private Location location;

    /** The user's location as a string */
    @Column
    private String locationString;

    static final String DEFAULT_PROFILE_PICTURE_NAME = "images/default-img.png";

    /**
     * JPA required no-args constructor
     */
    protected User() {}

     /**
     * Non-empty constructor for the User class
     * @param email          the user's email
     * @param firstName      the first name of the user
     * @param lastName       the last name of the user
     * @param date           the user's birthdate
     * @param location       the user's location
     * @param passwordHash   the user's passwordHash
     */
    public User(String email, String firstName, String lastName, LocalDate date, Location location, String passwordHash) {
        // setting the user values.
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        setDate(date);
        this.location = location;
        this.locationString = location.toString();
        this.passwordHash = passwordHash;
        updateToken = null;
        this.profilePicName = DEFAULT_PROFILE_PICTURE_NAME;
    }

    /**
     * Gives the user a new authority
     * @param authority the new authority
     */
    public void grantAuthority(Authority authority) {
        if (userRoles == null) {
            userRoles = new ArrayList<>();
        }
        userRoles.add(authority);
    }

    /**
     * Getter for the user's granted authorities
     * @return the user's granted authorities
     */
    public List<GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        this.userRoles.forEach(authority -> authorities.add(new SimpleGrantedAuthority(authority.getRole())));
        return authorities;
    }

    /**
     * Getter for the user's id
     * @return the user's id
     */
    public Long getId() {
        return id;
    }

    /**
     * Getter for the user's first name
     * @return the user's first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Setter for the user's first name
     * @param firstName the user's first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Getter for the user's last name
     * @return the user's last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Setter for the user's last name
     * @param lastName the user's last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the current date
     * @return the current date
     */
    public LocalDate getDate() {
        LocalDate date = LocalDate.now();
        date = date.withYear(dateOfBirthYear).withMonth(dateOfBirthMonth).withDayOfMonth(dateOfBirthDay);
        return date;
    }

    /**
     * Setter for the user's birthdate
     * @param date the user's birthdate
     */
    public void setDate(LocalDate date) {
        this.dateOfBirthDay = date.getDayOfMonth();
        this.dateOfBirthMonth = date.getMonthValue();
        this.dateOfBirthYear = date.getYear();
    }

    /**
     * Getter for the user's email
     * @return the user's email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setter for the user's email
     * @param email the user's email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Getter for the user's favourite sports
     * @return the user's favourite sports - a list of integer ids
     */
    public List<Sport> getSports() {
        return favSports;
    }

    /**
     * Setter for the user's list of favourite sports
     * @param sports the user's list of favourite sports
     */
    public void setSports(List<Sport> sports) {
        this.favSports = sports;
    }

    /**
     * Checks if the given password matches the user's hashed password
     * @param password the password the user is tyring to use
     * @return boolean of whether the passwords match
     */
    public boolean samePassword(String password) {
        return TokenGenerator.passwordEncoder.matches(password, this.getPasswordHash());
    }

    /**
     * Gets the hash of the user's password
     * @return the hash of the user's password
     */
    protected String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Setter for the name of the user's profile picture
     * @param fileName the name of the picture
     */
    public void setProfilePicName(String fileName) {
        profilePicName = fileName;
    }

    /**
     * Getter for the name of the user's profile picture
     * @return the name of the picture
     */
    public String getProfilePicName() {
        if (profilePicName == null) {
            return DEFAULT_PROFILE_PICTURE_NAME;
        }
        return profilePicName;
    }

    /**
     * Getter for the user's location
     * @return the location of the user
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Setter for the user's location
     * @param address1   street address 1 of the user's location
     * @param address2   street address 1 of the user's location
     * @param suburb      the suburb of the user's location
     * @param postcode    the user's postcode
     * @param city        the user's city
     * @param country     the user's country
     */
    public void editLocation(String address1, String address2, String suburb, String postcode, String city, String country) {
        this.location.setAll(address1, address2, suburb, postcode, city, country);
        this.locationString = this.location.toString();
    }

    /**
     * Gets the team's street address line 1
     *
     * @return team street address line 1
     */
    public String getAddressOne() {
        return location.getAddressOne();
    }

    /**
     * Gets the team's street address line 2
     *
     * @return team street address line 2
     */
    public String getAddressTwo() {
        return location.getAddressTwo();
    }

    /**
     * Gets the team's city
     *
     * @return team city
     */
    public String getSuburb() {
        return location.getSuburb();
    }
    public String getCity() {
        return location.getCity();
    }

    /**
     * Gets the team's postcode
     *
     * @return team postcode
     */
    public String getPostcode() {
        return location.getPostcode();
    }

    /**
     * Gets the team's country code
     *
     * @return team country code
     */
    public String getCountry() {
        return location.getCountry();
    }

    /**
     * Getter for the location of the user as a string
     * @return the location of the user as a string
     */
    public String getLocationString() {
        return locationString;
    }

    /**
     * Getter for the location of the user as an edit string
     * @return the location of the user as an edit string
     */
    public String getLocationEditString() {
        return location.toEditString();
    }

    /**
     * Setter for the user's password hash
     * @param string the user's password 
     */
    public void setPassword(String string) {
        this.passwordHash = TokenGenerator.passwordEncoder.encode(string);
    }

    /**
     * Updates the user's password
     * @param old           the user's old password
     * @param currentRetype the user's new password retyped
     * @param current       the user's new password
     * @return              a string status of the validity of the old and new passwords
     */
    public String updatePasswordDetails(String old, String currentRetype, String current) {
        if (!currentRetype.equals(current)) {
            return "noMatch";
        }
        LocalDate date = getDate();
        String[] userFields = {getFirstName(),getLastName(),getEmail(),date.toString()};
        if (!RegistrationChecker.isPasswordValid(current,userFields)) {
            return "invalid";
        }
        if (!samePassword(old)) {
            return "incorrect";
        }
        setPassword(current);
        return "true";
    }

    /**
     * Resets the user's password
     * @param currentRetype the user's new password retyped
     * @param current       the user's new password
     * @return              a string status of the validity of the new passwords
     */
    public String resetPasswordDetails(String currentRetype, String current) {
        if (!currentRetype.equals(current)) {
            return "noMatch";
        }
        LocalDate date = getDate();
        String[] userFields = {getFirstName(),getLastName(),getEmail(),date.toString()};
        if (!RegistrationChecker.isPasswordValid(current,userFields)) {
            return "invalid";
        }
        setPassword(current);
        return "true";
    }

    public boolean getIsActive() {
        return updateToken == null;
    }

    /** The random generator */
    private static final SecureRandom random = new SecureRandom();
    /** Integer constant for the maximum token length */
    private static final int MAX_TOKEN_LENGTH = 10;
    /**
     * Generates the token that is emailed to the user
     * @return the token, as a string
     */
    private static String generateToken()
    {
        char[] text = new char[MAX_TOKEN_LENGTH];
        for (int i = 0; i < MAX_TOKEN_LENGTH; i++)
        {
            text[i] = ALLOWED_CHARS.charAt(random.nextInt(ALLOWED_CHARS.length()));
        }
        return new String(text);
    }
    private static final String ALLOWED_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    public void deactivate() {
        updateToken = email + Instant.now().toEpochMilli() +generateToken();
    }

    public void activate() {
        updateToken = null;
    }

    public String getUpdateToken() {
        return updateToken;
    }

    /**
     * only use for testing.
     * @param id the new id of the user.
     * */
    public void setId(long id) {
        this.id = id;
    }

    public void setPrivacyType(PrivacyType privacyType) {
        this.privacyType = privacyType;
    }

    public PrivacyType getPrivacyType() {
        return this.privacyType;
    }

    /**
    * This function is used on the profile pages to check what should be visible on the page
    * @param areFriends a boolean that is true if the current user is friends with the user whose profile they are viewing
    */
    public boolean isViewable (boolean areFriends) {
        if (this.getPrivacyType() == PrivacyType.PUBLIC) {
            return true;
        }
        if (this.getPrivacyType() == PrivacyType.FREINDS_ONLY) {
            return areFriends;
        }
        return false;
    }

    /**
     * This function establishes a one-way relationship where the current user
     * follows the specified user. Following a user allows the current user to
     * receive updates and notifications related to the activities of the followed
     * user, such as posts, comments, or other interactions.
     * @param user the user to follow
     * */
    public void follow(User user) {
        following.add(user);
    }

    /**
     * Removes a followed user from the following list, thus unfollowing them
     * @param user the user to be unfollowed
     */
    public void unfollow(User user) {following.remove(user);}

    /**
     * checks if the user follows the inputted user.
     * @param user the user that the user is checking if it follows
     * @return if the user follows the user or not.
     * */
    public boolean isFollowing(User user) {
        return following.stream().anyMatch(u -> (Objects.equals(u.getId(), user.getId())));
    }

    /**
     * This function establishes a one-way relationship where the current user
     * follows the specified team. Following a team allows the current user to
     * receive updates and notifications related to the activities of the followed
     * team, such as posts, comments, or other interactions.
     * @param team the team to follow
     * */
    public void followTeam(Team team) {
        teamsFollowing.add(team);
    }

    /**
     * Removes the one-way relationship for a user following a team
     * @param team the team to unfollow
     */
    public void unfollowTeam(Team team) {teamsFollowing.remove(team);}

    /**
     * Calculates the metric used to suggest users to the user <br> <br>
     * sportFactor = 10 * min(similar sport count,4) <br>
     * locationFactor = 10 if is in same city 0 otherwise <br>
     * teamFactor = 20 * min(sheared team membership count, 2) <br>
     * clubFactor = 10 if shear a club 0 otherwise <br>
     * @param user the user that the suggestion is being made to
     * @param userService the userService used so that the calculation can access the database
     * @return sportFactor + locationFactor + teamFactor + clubFactor
     * */
    public float getSuggestionMetric(User user, UserService userService) {
        long sportSimilarCount = 0;
        if (favSports != null && user.getSports() != null) {
            sportSimilarCount = favSports.stream().filter((Sport s1) -> user.getSports().stream().anyMatch((Sport s2) -> Objects.equals(s1.getId(), s2.getId()))).count();
        }
        float sportFactor = 10F * (float)Math.min(sportSimilarCount,4L);
        float locationFactor = 0;
        if (user.getLocation().isSameCity(location)) {
            locationFactor = 10;
        }
        float teamFactor = 20f * (float)Math.min(2,userService.countSameTeamMemberShip(this,user));

        float clubFactor = 0;
        if (userService.hasSameClubMemberShip(this,user)) {
            clubFactor = 10;
        }

        return sportFactor + locationFactor + teamFactor + clubFactor;
    }
}

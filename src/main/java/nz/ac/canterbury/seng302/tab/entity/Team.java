package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import org.hibernate.annotations.CreationTimestamp;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom Team class.
 *
 */
@Entity
public class Team {

    /** The random generator */
    private static final SecureRandom random = new SecureRandom();

    /** Integer constant for the maximum token length */
    private static final int MAX_TOKEN_LENGTH = 12;

    /** Integer constant for the half of the maximum token length */
    private static final int HALF_TOKEN_LENGTH = MAX_TOKEN_LENGTH/2;

    /** String constant for the allowed letters in the token */
    private static final String ALLOWED_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    /** String constant for the allowed digits in the token */
    private static final String ALLOWED_DIGITS = "0123456789";

    /** The team's id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The name of the team */
    @Column(nullable = false)
    private String name;

    /** The location of the team */
    @OneToOne(cascade=CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name="location_id", nullable = false)
    private Location location;

    /** The location of the team as a string */
    @Column(nullable = false)
    private String locationString;

    /** The sport that the team plays */
    @Column(nullable = false)
    private String sport;

    /** The token for the team */
    @Column(name="invitation_token")
    private String token;

    /** The picture of the team */
    @Column
    private String profilePicName;

    @Column(name = "dateCreated", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime dateCreated;

    @OneToMany(mappedBy = "team")
    private List<Formation> formations;

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
     * JPA required no-args constructor
     */
    public Team() {}

    /**
     * Constructor for a Team
     *
     * @param name the name of the team
     * @param location the location of the team
     * @param sport the sport the team plays
     */
    public Team(String name, Location location, String sport) {
        this.name = name;
        this.location = location;
        this.locationString = location.toString();
        this.sport = sport;
        byte[] array = new byte[MAX_TOKEN_LENGTH];
        random.nextBytes(array);
        this.token = generateToken();
        this.profilePicName = "images/defaultPic.png";
        this.formations = new ArrayList<>();

    }


    /**
     * Setter for profile picture name
     *
     * @param profilePicPath Path of the relevant image
     */
    public void setProfilePicName(String profilePicPath) {
        this.profilePicName = profilePicPath;
    }


    /**
     * Setter for a team id
     *
     * @param teamId identifying number of the team
     */
    public void setId(Long teamId) {
        this.id = teamId;
    }

    /**
     * Setter used to edit all details of a team
     * @param id Team id
     * @param name Team name
     * @param address1   street address 1 of the team's location
     * @param address2   street address 1 of the team's location
     * @param suburb      the suburb of the team's location
     * @param postcode    the team's postcode
     * @param city        the team's city
     * @param country     the team's country
     * @param sport Team sport
     */
    public void editingTeam(Long id, String name, String address1, String address2, String suburb, String postcode, String city, String country, String sport) {
        this.name = name;
        this.location.setAll(address1, address2, suburb, postcode, city, country);
        this.locationString = this.location.toString();
        this.sport = sport;
    }

    /**
     * Gets the team's id
     *
     * @return team id
     */
    public Long getId() { return id; }

    /**
     * Gets the team's name
     *
     * @return team name
     */
    public String getName() { return name; }

    /**
     * Gets the team's token
     *
     * @return team token
     */
    public String getTeamToken() { return token; }


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
     * Gets the sport the team plays
     *
     * @return team sport
     */
    public String getSport() { return sport; }

    /**
     * Gets the profile picture of the team
     *
     * @return team profile picture
     */
    public String getProfilePicName() { return profilePicName; }

    /**
     * Getter for the team's location
     * @return the team's location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Getter for the team's location (string representation)
     * @return the team's location as a string
     */
    public String getLocationString() {
        return locationString;
    }

    /**
     * Getter for the team's location as an edit string
     * @return the team's location (edit string)
     */
    public String getLocationEditString() {
        return location.toEditString();
    }

    /**
     * Gets the team's creation date
     *
     * @return date that the team was created
     */
    public LocalDateTime getCreationDate() { return dateCreated; }

    public void setToken(String token) {this.token = token;}

    /**
     * Returns the String representation of a team
     *
     * @return String representation
     */
    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", location='" + location.toString() + '\'' +
                ", sport='" + sport + '\'' +
                '}';

    }

    /**
     * Generates the token for the team that is displayed on the team profile
     * Specifically only seen by the managers
     * @return the token, as a string
     */
    public static String generateToken()
    {
        char[] text = new char[MAX_TOKEN_LENGTH];
        for (int i = 0; i < MAX_TOKEN_LENGTH; i++)
        {
            if (i < HALF_TOKEN_LENGTH)
            {
                text[i] = ALLOWED_LETTERS.charAt(random.nextInt(ALLOWED_LETTERS.length()));
            } else {
                text[i] = ALLOWED_DIGITS.charAt(random.nextInt(ALLOWED_DIGITS.length()));
            }
        }
        return new String(text);
    }

    /**
     * should only be used in testing.
     * @param name the new name of the team.
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * adds a formation into the list of formations that a team has.
     * @param formation the formation being added to the list.
     * */
    public void addFormation(Formation formation) {
        formations.add(formation);
    }

    /**
     * get the metric used for team suggestions
     * @param user the user that is being suggested to
     * @param teamService a teamService used to access information about the teams.
     * */
    public float getSuggestionMetric(User user, TeamService teamService) {
        float locationFactor = 0;
        if (user.getLocation().isSameCity(location)) {
            locationFactor = 100;
        } else if (user.getLocation().getCountry().equals(location.getCountry())) {
            locationFactor = 10;
        }
        float sportFactor = 0;
        if (user.getSports().stream().anyMatch(sport1 -> sport.equals(sport1.getSportName()))) {
            sportFactor = 50;
        }
        float userFactor = teamService.getPercentOfUsersWhoShareTeam(user,this) * 100;
        return userFactor + sportFactor + locationFactor;
    }
}
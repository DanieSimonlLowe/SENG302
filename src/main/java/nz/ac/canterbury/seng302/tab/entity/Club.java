package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Club {
    /** The Club's id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name="location_id", nullable = false)
    private Location location;

    @Column
    private String profilePicName;

    @Column
    private String name;

    @Column(name = "location_string")
    private String locationString;

    @JoinColumn(name="user_id", nullable = false)
    @ManyToOne
    private User user;

    @OneToMany
    private List<Team> teams;

    static final String DEFAULT_PROFILE_PICTURE_NAME = "images/default-img.png";

    protected Club() {}
    public Club(User user, String name, Location location, List<Team> teams) {
        this.user = user;
        this.name = name;
        this.location = location;
        this.teams = teams;
        this.locationString = location.toString();
    }


    public Location getLocation() {
        return location;
    }

    public String getProfilePicName() {
        if (profilePicName == null) {
            return DEFAULT_PROFILE_PICTURE_NAME;
        }
        return profilePicName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(Location location) {
        this.location = location;
        this.locationString = location.toString();
    }

    public void setTeams(List<Team> teams) { this.teams = teams; }


    public String getName() {
        return name;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public Long getId() {
        return id;
    }

    public Long getManager() {
        return user.getId();
    }

    public void setProfilePicName(String fileName) {
        profilePicName = fileName;
    }

    /**
     * gets the sport that the club focuses on.
     * @return the sport that the club focuses on
     * */
    public String getSport() {
        if (teams.isEmpty()) {
            return null;
        }
        return teams.get(0).getSport();
    }
}

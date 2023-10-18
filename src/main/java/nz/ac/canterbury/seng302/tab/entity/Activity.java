package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Used to store Activity's that are to be done or have been done with a team or single user.
 */
@Entity
@Table(name = "tab_activities")
public class Activity {
    /** The id of the activity */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The type of the activity */
    @Column(nullable = false)
    private ActivityType type;

    /** The associated team of the activity */
    @OneToOne(fetch = FetchType.EAGER)
    private Team team;

    /** The opposition team. Used in games and friendlies*/
    @OneToOne(fetch = FetchType.EAGER)
    private Team opposition;

    /** The creator of the activity */
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    /** The description of the activity */
    @Column(nullable = false)
    private String description;

    /** The start date of the activity */
    @Column(nullable = false)
    private LocalDateTime startDate;

    /** The end date of the activity */
    @Column(nullable = false)
    private LocalDateTime endDate;

    /** The activity's location */
    @OneToOne
    @JoinColumn(name="location_id")
    private Location location;

    /** The activity's location as a string */
    @Column
    private String locationString;

    public static final String NO_TEAM_COLOR = "6565FF";
    public static final String HAS_TEAM_COLOR = "FF2222";

    protected Activity() {

    }


    public Long getId() {
        return id;
    }

    /**
     * creates an activity.
     * @param type the type of activity.
     * @param team the team that the activity is for.
     * @param description the description of the activity.
     * @param startDate the startDate of the activity.
     * @param endDate the endDate of the activity.
     * @param user the user who created this activity
     * */
    public Activity(ActivityType type, Team team, Team opposition, String description, LocalDateTime startDate, LocalDateTime endDate, User user, Location location) {
        this.type = type;
        this.team = team;
        this.opposition = opposition;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.user = user;
        this.location = location;
        this.locationString = location.toString();
    }

    public LocalDateTime getStartTime() {
        return this.startDate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ActivityType getType() {
        return type;
    }

    public void setType(ActivityType type) {
        this.type = type;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Team getOpposition() {
        return opposition;
    }

    public void setOpposition(Team opposition) {
        this.opposition = opposition;
    }

    public User getUser() { return user; }

    public Long getUserId() { return user.getId(); }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Location getLocation() { return location; }

    public String toString() {
        return "Type: " + type + "\nStartTime: " + startDate.toString() + "\nEndTime: " + endDate.toString();
    }

    public boolean isInTimeRange(Integer minutes) {
        LocalDateTime date = startDate.plusMinutes(minutes);
        return(endDate.isAfter(date));
    }

    public String getJavaScriptEvent(int id) {
        StringBuilder javascript = new StringBuilder();
        javascript.append('{');
        javascript.append("id: \"");
        javascript.append(getDescription());
        javascript.append(id);
        javascript.append("\", start: \"");
        javascript.append(getStartDate().format(DateTimeFormatter.ISO_DATE_TIME));
        javascript.append("\", end: \"");
        javascript.append(getEndDate().format(DateTimeFormatter.ISO_DATE_TIME));
        javascript.append("\", title: \"");
        javascript.append(getDescription());
        javascript.append("\", color: \"");
        javascript.append("\"},");
        return javascript.toString();
    }
}

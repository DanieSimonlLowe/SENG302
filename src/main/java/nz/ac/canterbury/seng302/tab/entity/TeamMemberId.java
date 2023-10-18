package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.io.Serializable;
import java.util.Objects;

/**
 * TeamMemberId, used as a composite keu for TeamMembers
 */
@Embeddable
public class TeamMemberId implements Serializable {

    /**
     * The user the team member corresponds to
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * The team that the team member corresponds to
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    /**
     * JPA required no-args constructor
     */
    protected TeamMemberId() {}

    /**
     * Constructor for a TeamMemberId
     * @param user the user that the TeamMemberId corresponds to
     * @param team the team that the TeamMemberId belongs to
     */
    public TeamMemberId(User user, Team team) {
        this.user = user;
        this.team = team;
    }

    /**
     * Gets the user that belongs to the TeamMemberId
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * Gets the team that belongs to the TeamMemberId
     * @return the team
     */
    public Team getTeam() {
        return team;
    }

    /**
     * Checks whether the TeamMemberId matches the given TeamMemberId
     * @param o the given TeamMemberId
     * @return a true if the two TeamMemberIds are equal, false if not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamMemberId that = (TeamMemberId) o;
        return Objects.equals(user.getId(), that.user.getId()) && Objects.equals(team.getId(), that.team.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, team);
    }
}

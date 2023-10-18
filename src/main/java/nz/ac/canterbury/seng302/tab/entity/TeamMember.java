package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Team member entity
 */
@Entity
@Table(name = "team_member")
public class TeamMember {

    /** Composite key, composed of a user and a team */
    @EmbeddedId
    private TeamMemberId teamMemberId;

    /** The role of the team member within the team */
    @Column(nullable = false, name="role")
    private Role role;

    /**
     * JPA required no-args constructor
     */
    public TeamMember() {}

    /**
     * Constructor for a team member
     * @param user the user that is a team member
     * @param team the team that the team member belongs to
     * @param role the role of the user within the team
     */
    public TeamMember(User user, Team team, Role role) {
        this.teamMemberId = new TeamMemberId(user, team);
        this.role = role;
    }

    /**
     * Gets the team member's id, which is a composite key composed of a team and a user
     * @return the team member's id
     */
    public TeamMemberId getTeamMemberId() {
        return teamMemberId;
    }

    /**
     * Gets the role of the team member within the team
     * @return the role, one of the Role enum
     */
    public Role getRole() {
        return role;
    }

    /**
     * Sets the role of the user within the team
     * @param role the new role of the user, one of the Role enum
     */
    public void setRole(Role role) {
        this.role = role;
    }
}

package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tab_lineup_player")
public class LineupPlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false)
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(optional=false)
    private LineUp lineUp;

    @Column
    private int position;

    protected LineupPlayer() {}

    /**
     * Lineup constructor
     * @param user The player
     * @param lineUp The lineup to assign the player to
     * @param position Zero based index of the user
     */
    public LineupPlayer(User user, LineUp lineUp, int position) {
        this.user = user;
        this.lineUp = lineUp;
        this.position = position;
    }

    /**
     * Gets the lineup player's user
     * @return the user
     */
    public User getUser() {
        return user;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public LineUp getLineUp() {
        return lineUp;
    }

}

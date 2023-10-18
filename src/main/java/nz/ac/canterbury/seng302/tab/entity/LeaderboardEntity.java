package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@MappedSuperclass
public abstract class LeaderboardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club;

    @Column
    private Integer count;

    @Column
    private LocalDateTime lastUpdated;

    protected LeaderboardEntity() {}

    protected LeaderboardEntity(Club club, User user) {
        this.user = user;
        this.club = club;
        this.count = 1;
        this.lastUpdated = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Integer getCount() {
        return count;
    }

    public User getUser() {
        return user;
    }

    public Club getClub() {
        return club;
    }

    /**
     * Increments the count of the leaderboard entity and updates the last updated time
     */
    public void increment() {
        this.count++;
        this.lastUpdated = LocalDateTime.now();
    }
}

package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;

/**
 * The FeedPost class is used to store information about a post on the feed
 */
@Entity
public class FeedAlerts {


    /**
     * The FeedPost's id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    /**
     * The id of the owner of the post
     */
    @Column
    private Long userId;


    /**
     * The number of read alerts
     */
    @Column
    private Long readAlerts;


    /**
     * The default constructor for the FeedPost class
     */
    protected FeedAlerts() {}


    /**
     * The constructor for the FeedPost class
     * @param userId The id of the owner of the post
     */
    public FeedAlerts(Long userId) {
        this.userId = userId;
        this.readAlerts = 0L;
    }

    /**
     * gets the number of read alerts
     * @return the number of read alerts
     */
    public Long getReadAlerts() {
        return readAlerts;
    }


    /**
     * sets the number of read alerts
     * @param readAlerts the number of read alerts
     */
    public void setReadAlerts(Long readAlerts) {
        this.readAlerts = readAlerts;
    }

    /**
     * gets the id of the number of read alerts
     * @return the id of the number of read alerts
     */
    public long getUserId() {
        return userId;
    }


    /**
     * gets the id of the number of read alerts
     * @return the id of the number of read alerts
     */
    public Long getId() {
        return id;
    }
}

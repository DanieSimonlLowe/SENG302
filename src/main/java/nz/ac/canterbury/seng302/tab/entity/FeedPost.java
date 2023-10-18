package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class FeedPost {

    /**
     * The FeedPost's id
     */
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    /**
     * The id of the owner of the post
     */
    @Column
    private Long ownerId;

    /**
     * The type of the owner of the post
     */
    @Column
    private OwnerType ownerType;

    /**
     * The name of the owner of the post
     */
    @Column
    private String ownerName;

    /**
     * The title of the post
     */
    @Column
    private String title;

    /**
     * The message of the post
     */
    @Column
    private String message;

    /**
     * The author of the post
     */
    @JoinColumn(name = "author")
    @ManyToOne
    private User author;

    /**
     * The date and time of the post
     */
    @Column
    private LocalDateTime dateTime;

    /**
     * The string name of the post attachment (not required)
     */
    @Column
    private String attachmentName;

    @Column
    private String style;

    /**
     * A boolean value for whether the post has been flagged for review
     */
    @Column
    private boolean flagged;

    /**
     * The reason that the post has been flagged for review.
     */
    @Column
    private String flaggedReason;

    /**
     * Constructor for a FeedPost
     * @param ownerId The id of the owner of the post
     * @param ownerType The type of the owner of the post
     * @param ownerName The name of the owner of the post
     * @param message The message of the post
     * @param author The author of the post
     * @param attachmentName The name of the attachment
     */
    public FeedPost(Long ownerId, OwnerType ownerType, String ownerName, String title, String message, User author, String attachmentName) {
        this.ownerId = ownerId;
        this.ownerType = ownerType;
        this.ownerName = ownerName;
        this.title = title;
        this.message = message;
        this.author = author;
        this.dateTime = LocalDateTime.now();
        this.attachmentName = attachmentName;
        this.flagged = false;
        this.style = "";
    }

    /** Constructor used to make a copy of a FeedPost **/
    public FeedPost (FeedPost post) {
        this.id = post.id;
        this.ownerId = post.ownerId;
        this.ownerType = post.ownerType;
        this.ownerName = post.ownerName;
        this.title = post.title;
        this.message = post.message;
        this.author = post.author;
        this.dateTime = post.dateTime;
        this.attachmentName = post.attachmentName;
        this.style = post.style;
        this.flagged = post.flagged;
        this.flaggedReason = post.flaggedReason;
    }

    /**
     * Empty constructor for JPA
     */
    protected FeedPost() {
    }

    /**
     * Gets the feed post's id
     *
     * @return feed post id
     */
    public Long getId() { return id; }

    /**
     * Get the feed's title
     * @return feed title
     */
    public String getTitle() { return title; }

    /**
     * Gets the feed message
     * @return the feed message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the feed author
     * @return the feed author
     */
    public User getAuthor() {
        return author;
    }

    /**
     * Gets the feed date and time
     * @return the feed date and time
     */
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    /**
     * Gets the feed date and time as a string
     * @return the feed date and time as a string
     */
    public String getDateTimeString() {
        return dateTime.toString();
    }

    /**
     * Gets the owner id
      * @return the owner id
     */
    public Long getOwnerId() {
        return ownerId;
    }

    /**
     * Gets the owner type
     * @return the owner type
     */
    public OwnerType getOwnerType() {
        return ownerType;
    }

    /**
     * Gets the owner name
     * @return the owner name
     */
    public String getOwnerName() {
        return ownerName;
    }

    /**
     * Gets the attachment name
     * @return the attachment name
     */
    public String getAttachmentName() { return attachmentName; }

    /**
     * Gets the style
     * @return the style
     */
    public String getStyle() { return style; }

    /**
     * Sets the style
     * @param style the style
     */
    public void setStyle(String style) { this.style = style; }

    /**
     * Checks whether the attachment is a video or not
     * @return boolean of whether the attachment is a video or not
     */
    public boolean isVideo() {
        if (attachmentName == null) {
            return false;
        }
        List<String> validExtensions = new ArrayList<>();
        validExtensions.add("mp4");
        validExtensions.add("webm");
        validExtensions.add("ogg");
        String ext = attachmentName.substring(attachmentName.lastIndexOf('.') + 1);
        return validExtensions.contains(ext);
    }

    /**
     * Checks whether the attachment is an image or not
     * @return boolean of whether the attachment is an image or not
     */
    public boolean isImg() {
        if (attachmentName == null) {
            return false;
        }
        List<String> validExtensions = new ArrayList<>();
        validExtensions.add("jpeg");
        validExtensions.add("jpg");
        validExtensions.add("svg");
        validExtensions.add("png");
        String ext = attachmentName.substring(attachmentName.lastIndexOf('.') + 1);
        return validExtensions.contains(ext);
    }

    /**
     * Returns the file extension of the attachment
     * @return the file extension of the attachment
     */
    public String getAttachmentType() {
        if (attachmentName == null) {
            return "";
        } else {
            return attachmentName.substring(attachmentName.lastIndexOf('.') + 1);
        }
    }

    /**
     * Sets the feed post to be flagged or not flagged
     * @param flag boolean for whether the post is flagged or not
     */
    public void setFlagged(boolean flag) {
        this.flagged = flag;
    }

    /**
     * Sets the reason that the post has been flagged. Can only be set if the post has been flagged.
     * @param reason The reason that the post has been flagged.
     */
    public void setFlaggedReason(String reason) {
        if (this.flagged) {
            this.flaggedReason = reason.replaceAll(",\s*$", "");
        }
    }

    /**
     * Returns whether the post has been flagged for review.
     * @return The boolean for whether the post has been flagged.
     */
    public boolean getFlagged() {
        return this.flagged;
    }

    /**
     * Gets the reason the post has been flagged.
     * @return the reason that the post was flagged.
     */
    public String getFlaggedReason() {
        if (this.flagged) {
            return flaggedReason;
        } else {
            return "N/A";
        }
    }
}

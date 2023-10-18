package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Comment {

    /**
     * The Comment's id
     */
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    /**
     * The id of the post that this comment is on
     */
    @Column
    private Long postId;

    /**
     * The id of the parent comment. This will be null if this comment is not a reply.
     */
    @Column
    private Long parentCommentId;

    /**
     * The message of the post
     */
    @Column(columnDefinition="TEXT")
    private String message;

    /**
     * The author of the post
     */
    @JoinColumn(name = "user_id")
    @ManyToOne
    private User user;

    /**
     * The date and time of the post
     */
    @Column
    private LocalDateTime dateTime;

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


    protected Comment() {}

    public Comment(Long postId, Long parentCommentId, String message, User user, LocalDateTime dateTime) {
        this.postId = postId;
        this.parentCommentId = parentCommentId;
        this.message = message;
        this.user = user;
        this.dateTime = dateTime;
        this.style = "";
    }

    /** Constructor used to make a copy **/
    public Comment(Comment comment) {
        this.id = comment.getId();
        this.postId = comment.getPostId();
        this.parentCommentId = comment.getParentCommentId();
        this.message = comment.getMessage();
        this.user = comment.getAuthor();
        this.dateTime = comment.getDateTime();
        this.style = comment.getStyle();
        this.flagged = comment.getFlagged();
        this.flaggedReason = comment.getFlaggedReason();
    }

    public Long getPostId() { return postId; }

    public Long getParentCommentId() { return parentCommentId; }

    public String getMessage() {
        return message;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public User getAuthor() {
        return user;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public Long getId() {
        return id;
    }

    public boolean getFlagged() { return flagged; }

    public void setFlagged(boolean flagged) { this.flagged = flagged; }

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

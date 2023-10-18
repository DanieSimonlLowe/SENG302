package nz.ac.canterbury.seng302.tab.entity;

public class ModerationResult {
    /** A boolean value for whether the post has been flagged for review */
    private boolean flagged;
    /** The reason that the post has been flagged for review. */
    private String flaggedReason;

    /**
     * Constructor for the moderation result
     * @param flagged Whether the post has been flagged
     * @param flaggedReason The reason the post has been flagged
     */
    public ModerationResult(boolean flagged, String flaggedReason) {
        this.flagged = flagged;
        this.flaggedReason = flaggedReason;
    }

    /**
     * Gets whether the post has been flagged
     * @return Whether the post has been flagged
     */
    public boolean isFlagged() {
        return flagged;
    }

    /**
     * Sets whether the post has been flagged
     * @param flagged Whether the post has been flagged
     */
    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }

    /**
     * Gets the reason the post has been flagged
     * @return The reason the post has been flagged
     */
    public String getFlaggedReason() {
        return flaggedReason;
    }

    /**
     * Sets the reason the post has been flagged
     * @param flaggedReason The reason the post has been flagged
     */
    public void setFlaggedReason(String flaggedReason) {
        this.flaggedReason = flaggedReason;
    }
}
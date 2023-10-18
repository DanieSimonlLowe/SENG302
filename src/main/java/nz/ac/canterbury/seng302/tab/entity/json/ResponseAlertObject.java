package nz.ac.canterbury.seng302.tab.entity.json;

/**
 * A JSON response object for personal feed posts
 */
public record ResponseAlertObject(String ownerName, String message, String attachment, String attachmentType, String postDateTime) {
}

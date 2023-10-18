package nz.ac.canterbury.seng302.tab.entity.json;

/**
 * A JSON response object for players
 */
public record ResponsePlayerObject(Long userId,
                                   String firstName,
                                   String profilePicName) {
}

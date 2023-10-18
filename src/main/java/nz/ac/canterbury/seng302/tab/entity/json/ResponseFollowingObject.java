package nz.ac.canterbury.seng302.tab.entity.json;

public record ResponseFollowingObject(Long userId,
                                      String firstName,
                                      String lastName,
                                      String email,
                                      Boolean areFriends,
                                      String profilePicName) {
}

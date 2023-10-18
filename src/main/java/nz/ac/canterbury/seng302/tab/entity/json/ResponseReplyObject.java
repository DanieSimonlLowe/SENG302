package nz.ac.canterbury.seng302.tab.entity.json;

public record ResponseReplyObject(String userImage,
                                  String userName,
                                  String commentMessage,
                                  String customBorderStyle,
                                  String commentDate,
                                  long commentId) {
}

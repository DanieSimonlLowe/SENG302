package nz.ac.canterbury.seng302.tab.entity.json;

import java.util.ArrayList;

public record ResponseCommentObject(String userImage,
                                    String userName,
                                    String commentMessage,
                                    String commentDate,
                                    Long commentId,
                                    String customBorderStyle,
                                    ArrayList<ResponseReplyObject> replies
                                    ) {
}

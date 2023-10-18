package nz.ac.canterbury.seng302.tab.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.entity.json.ResponseCommentObject;
import nz.ac.canterbury.seng302.tab.entity.json.ResponseReplyObject;
import nz.ac.canterbury.seng302.tab.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.*;

@RestController
public class FeedRestController {

    /**
     * The logger
     */
    final Logger logger = LoggerFactory.getLogger(FeedRestController.class);

    /**
     * The ModerationAPIController for moderation API logic
     */
    private final ModerationAPIController moderationAPIController;

    /**
     * The FeedPostService for database logic
     */
    private final FeedPostService feedPostService;

    /**
     * The UserService for database logic
     */
    private final UserService userService;

    /**
     * The TeamService for database logic
     */
    private final TeamService teamService;

    /**
     * The TeamMemberService for database logic
     */
    private final TeamMemberService teamMemberService;

    /**
     * The ClubService for database logic
     */
    private final ClubService clubService;

    /** The CommentService for accessing the comments in the database */
    private final CommentService commentService;

    /** The NumCommentsService for accessing the leaderboard entities in the database */
    private final NumCommentsService numCommentsService;

    /** The NumPostsService for accessing the leaderboard entities in the database */
    private final NumPostsService numPostsService;

    /**
     * Post delete string constant
     */
    private static final String POST_DELETED = "Post deleted";

    /** Comment delete string constant */
    private static final String COMMENT_DELETED = "Comment deleted";

    /** Invalid request string constant */
    private static final String INVALID_REQUEST = "Invalid request.";

    /** User not manager of a club string constant */
    private static final String NOT_MANAGER_CLUB = "User is not a manager of the club";

    /** User not manager of a team string constant */
    private static final String NOT_MANAGER_TEAM = "User is not a manager of the team";

    /** Team not found string constant */
    private static final String TEAM_NOT_FOUND = "Team not found";

    /** Club not found string constant */
    private static final String CLUB_NOT_FOUND = "Club not found";

    /** Ownertype invalid string constant */
    private static final String OWNERTYPE_INVALID = "OwnerType invalid";

    /** Ownertype not found string constant */
    private static final String OWNERTYPE_NOT_FOUND = "OwnerType not found";






    /**
     * Constructor for the feed rest controller
     *
     * @param feedPostService   the feed post service
     * @param userService       the user service
     * @param teamService       the team service
     * @param teamMemberService the team member service
     * @param clubService       the club service
     * @param moderationAPIController the moderation API controller
     * @param commentService The comment service
     * @param numCommentsService The num comments service
     */
    @Autowired
    public FeedRestController(FeedPostService feedPostService,
                              UserService userService,
                              TeamService teamService,
                              TeamMemberService teamMemberService,
                              ClubService clubService,
                              ModerationAPIController moderationAPIController,
                              CommentService commentService,
                              NumCommentsService numCommentsService,
                              NumPostsService numPostsService) {
        this.feedPostService = feedPostService;
        this.userService = userService;
        this.teamService = teamService;
        this.teamMemberService = teamMemberService;
        this.clubService = clubService;
        this.commentService = commentService;
        this.moderationAPIController = moderationAPIController;
        this.numCommentsService = numCommentsService;
        this.numPostsService = numPostsService;
    }

    /**
     * Endpoint for deleting a feed post
     *
     * @param postId the id of the post to delete
     * @return a response entity with the result of the deletion
     */
    @DeleteMapping("/feed/delete/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId) {
        logger.info("Deleting post with id: {} from the database", postId);
        try {
            FeedPost feedPost = feedPostService.getFeedPostById(postId).orElseThrow();
            String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userService.getUserByEmail(email);
            return deleteFeedPost(feedPost, user);
        } catch (NoSuchElementException e) {
            logger.error("Post with id: {} not found in the database", postId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found");
        } catch (Exception e) {
            logger.error("Error deleting post with id: {} from the database", postId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting post");
        }
    }

    /**
     * deletes a comment, if the logged-in user can delete the comment.
     * @param commentId the id of the comment to be deleted.
     * @return a response entity that says if the comment was deleted or not.
     * */
    @DeleteMapping("/feed/comment/delete/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
        logger.info("deleting comment " + commentId);
        try {
            Comment comment = commentService.getCommentById(commentId).orElseThrow();
            if (canDeleteComment(comment)) {
                commentService.delete(comment);
                return ResponseEntity.status(HttpStatus.OK).body("comment deleted");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("you can't delete that comment");
            }
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting post " + e.getMessage());
        }

    }

    /**
     * checks if a comment can be deleted by the current logged-in user.
     * @param comment the comment that is to be checked
     * @return if the logged-in user can delete the comment.
     * */
    private boolean canDeleteComment(Comment comment) {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getUserByEmail(email);
        FeedPost feedPost = feedPostService.getFeedPostById(comment.getPostId()).orElseThrow();
        OwnerType ownerType = feedPost.getOwnerType();
        switch (ownerType) {
            case USER -> {
                return feedPost.getOwnerId().equals(user.getId());
            }
            case TEAM -> {
                try {
                    Team team = teamService.getTeam(feedPost.getOwnerId()).orElseThrow();
                    return teamMemberService.getAllManagersFromTeam(team)
                            .stream()
                            .map(manager -> manager.getTeamMemberId().getUser())
                            .toList()
                            .contains(user);
                } catch (NoSuchElementException e) {
                    return false;
                }
            }
            case CLUB -> {
                try {
                    Club club = clubService.getClub(feedPost.getOwnerId()).orElseThrow();
                    return club.getManager().equals(user.getId());
                } catch (NoSuchElementException e) {
                    logger.error("Club with id: {} not found in the database", feedPost.getOwnerId());
                    return false;
                }
            }
            default -> {
                logger.error("OwnerType: {} not found", ownerType);
                return false;
            }
        }
    }

    /**
     * Attempts to delete a given feed post
     *
     * @param feedPost the feed post to delete
     * @param user     the user attempting to delete the post
     * @return a response entity with the result of the deletion
     */
    public ResponseEntity<String> deleteFeedPost(FeedPost feedPost, User user) {
        OwnerType ownerType = feedPost.getOwnerType();
        switch (ownerType) {
            case USER -> {
                if (feedPost.getOwnerId().equals(user.getId())) {
                    feedPostService.deleteFeedPost(feedPost.getId());
                    return ResponseEntity.status(HttpStatus.OK).body(POST_DELETED);
                }
            }
            case TEAM -> {
                try {
                    Team team = teamService.getTeam(feedPost.getOwnerId()).orElseThrow();
                    if (teamMemberService.getAllManagersFromTeam(team)
                            .stream()
                            .map(manager -> manager.getTeamMemberId().getUser())
                            .toList()
                            .contains(user)) {
                        feedPostService.deleteFeedPost(feedPost.getId());
                        return ResponseEntity.status(HttpStatus.OK).body(POST_DELETED);
                    } else {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(NOT_MANAGER_TEAM);
                    }
                } catch (NoSuchElementException e) {
                    logger.error("Team with id: {} was not found in the database", feedPost.getOwnerId());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(TEAM_NOT_FOUND);
                }
            }
            case CLUB -> {
                try {
                    Club club = clubService.getClub(feedPost.getOwnerId()).orElseThrow();
                    if (club.getManager().equals(user.getId())) {
                        feedPostService.deleteFeedPost(feedPost.getId());
                        return ResponseEntity.status(HttpStatus.OK).body(POST_DELETED);
                    } else {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(NOT_MANAGER_CLUB);
                    }
                } catch (NoSuchElementException e) {
                    logger.error("Club with id: {} could not be found in the database", feedPost.getOwnerId());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CLUB_NOT_FOUND);
                }
            }
            default -> {
                logger.error(OWNERTYPE_NOT_FOUND);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(OWNERTYPE_NOT_FOUND);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User does not have permission to delete post");
    }

    /**
     * Returns the comments for a given post
     * @param postId The id of the post
     * @param clubId The id of the club, will be -1 on non-club pages
     * @return A JSON string containing the comment
     */
    @GetMapping("/feed/getComments/{postId}&{clubId}")
    public ResponseEntity<String> getComments(@PathVariable Long postId, @PathVariable Long clubId) {
        logger.info("Getting comments for post with id {}", postId);

        List<Comment> allComments = commentService.getAllCommentsByPostId(postId);

        List<NumComments> topThreeCommenters;
        List<NumPosts> topThreePosters;

        if (clubId != -1) {
            topThreeCommenters = numCommentsService.getTopThreeByClub(clubId);
            topThreePosters = numPostsService.getTopThreeByClub(clubId);

            List<Comment> copyOfComments = new ArrayList<>(allComments);

            for (Comment comment : copyOfComments) {
                for (NumComments commenter : topThreeCommenters) {
                    if (commenter.getUser().getId().equals(comment.getAuthor().getId())) {
                        comment.setStyle(getBorderStyle(topThreeCommenters.indexOf(commenter)));
                    }
                }

                for (NumPosts poster : topThreePosters) {
                    if (poster.getUser().getId().equals(comment.getAuthor().getId())) {
                        comment.setStyle(getBorderStyle(topThreePosters.indexOf(poster)));
                    }
                }
            }
            allComments = copyOfComments;
        } else {
            topThreeCommenters = new ArrayList<>();
            topThreePosters = new ArrayList<>();
        }

        List<ResponseCommentObject> responseCommentObjects = new ArrayList<>();

        for (Comment comment : allComments) {
            String dateString = comment.getDateTime().toString();
            List<ResponseReplyObject> responseReplyObjects = commentService
                    .findAllByParentCommentId(comment.getId())
                    .stream()
                    .map(c -> createResponseReplyObject(c, clubId, topThreePosters, topThreeCommenters))
                    .toList();

            ResponseCommentObject responseObject = new ResponseCommentObject(
                    comment.getAuthor().getProfilePicName(),
                    comment.getAuthor().getFirstName() + " " + comment.getAuthor().getLastName(),
                    comment.getMessage(),
                    dateString,
                    comment.getId(),
                    comment.getStyle(),
                    new ArrayList<>(responseReplyObjects)
            );
            responseCommentObjects.add(responseObject);
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(responseCommentObjects);
            return ResponseEntity.status(HttpStatus.OK).body(jsonString);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error converting to JSON");
        }
    }

    /**
     * Returns the border style for the comment
     * @param index The index of the comment
     * @return The border style
     */
    private String getBorderStyle(int index) {
        if (index == 0) {
            return "3px solid gold";
        } else {
            return index == 1 ? "3px solid silver" : "3px solid saddlebrown";
        }
    }

    /**
     * Creates a response reply object
     * @param c The comment to create the response reply object from
     * @param clubId The id of the club, will be -1 on non-club pages
     * @param topThreePosters The top three posters
     * @param topThreeCommenters The top three commenters
     * @return The response reply object
     */
    private ResponseReplyObject createResponseReplyObject(Comment c, Long clubId, List<NumPosts> topThreePosters, List<NumComments> topThreeCommenters) {
        Comment copy = new Comment(c);
        if (clubId != -1) {
            for (NumPosts poster : topThreePosters) {
                if (poster.getUser().getId().equals(c.getAuthor().getId())) {
                    copy.setStyle(getBorderStyle(topThreePosters.indexOf(poster)));
                }
            }

            for (NumComments commenter : topThreeCommenters) {
                if (commenter.getUser().getId().equals(c.getAuthor().getId())) {
                    copy.setStyle(getBorderStyle(topThreeCommenters.indexOf(commenter)));
                }
            }
        }
        return new ResponseReplyObject(
                copy.getAuthor().getProfilePicName(),
                copy.getAuthor().getFirstName() + " " + copy.getAuthor().getLastName(),
                copy.getMessage(),
                copy.getStyle(),
                copy.getDateTime().toString(),
                copy.getId()
        );
    }


    /**
     * Post mapping to add a comment
     * @param body The post body containing the comment information
     * @return The outcome of adding a comment to the database
     */
    @PostMapping("/feed/addComment")
    public ResponseEntity<String> addComment(@RequestBody String body) {
        logger.info("Adding comment to database");

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonResponse = mapper.readTree(body);
            String message = jsonResponse.get("message").asText();
            Long parentCommentId = Long.parseLong(jsonResponse.get("parentCommentId").asText());
            Long postId = Long.parseLong(jsonResponse.get("postId").asText());

            if (parentCommentId == -1L) {
                parentCommentId = null;
            }

            User user = userService.getUserByEmail((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

            FeedPost post = feedPostService.getFeedPostById(postId).orElseThrow();
            if(post.getOwnerType() == OwnerType.CLUB) {
                Long clubId = post.getOwnerId();
                Club club = clubService.getClub(clubId).orElseThrow();
                numCommentsService.getByClubAndUser(clubId, user.getId()).ifPresentOrElse(
                        numPosts -> {
                            numPosts.increment();
                            numCommentsService.save(numPosts);
                        },
                        () -> {
                            NumComments numPosts = new NumComments(club, user);
                            numCommentsService.save(numPosts);
                        });
            }

            Comment comment = new Comment(postId, parentCommentId, message.trim(), userService.getUserByEmail((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal()), LocalDateTime.now());
            commentService.save(moderationAPIController.moderateComment(comment));

            if (comment.getFlagged() && post.getOwnerType() == OwnerType.USER) {
                commentService.delete(comment);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Comment was flagged.");
            }

            if (comment.getFlagged()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Comment was flagged.");
            }
            return ResponseEntity.status(HttpStatus.CREATED).body("Comment added to database");
        } catch (Exception e) {
            logger.info(e.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(INVALID_REQUEST);
        }
    }


    /**
     * Endpoint for deleting a comment
     *
     * @param commentId the id of the comment to delete
     * @return a response entity with the result of the deletion
     */
    @DeleteMapping("/feed/deleteComment/{commentId}")
    public ResponseEntity<String> deleteCommentMapping(@PathVariable Long commentId) {
        logger.info("Deleting comment with id: {} from the database", commentId);
        try {
            Comment comment = commentService.getCommentById(commentId).orElseThrow();
            String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userService.getUserByEmail(email);
            return deleteComment(comment, user);
        } catch (NoSuchElementException e) {
            logger.error("Comment with id: {} not found in the database cal135", commentId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Comment not found");
        } catch (Exception e) {
            logger.error("Error deleting comment with id: {} from the database", commentId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting comment");
        }
    }



    /**
     * Attempts to delete a given comment
     *
     * @param comment the comment to delete
     * @param user    the user attempting to delete the post
     * @return a response entity with the result of the deletion
     */
    public ResponseEntity<String> deleteComment(Comment comment, User user) {
        Optional<FeedPost> parentPost = feedPostService.getFeedPostById(comment.getPostId());
        FeedPost parent = null;
        OwnerType ownerType = null;
        if (parentPost.isPresent()) {
            ownerType = parentPost.get().getOwnerType();
            parent = parentPost.get();
        }

        switch (Objects.requireNonNull(ownerType)) {
            case TEAM -> {
                try {
                    Team team = teamService.getTeam(parent.getOwnerId()).orElseThrow();
                    if (teamMemberService.getAllManagersFromTeam(team)
                            .stream()
                            .map(manager -> manager.getTeamMemberId().getUser())
                            .toList()
                            .contains(user)) {
                        commentService.delete(comment);
                        return ResponseEntity.status(HttpStatus.OK).body(COMMENT_DELETED);
                    } else {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(NOT_MANAGER_TEAM);
                    }
                } catch (NoSuchElementException e) {
                    logger.error("Team with id: {} could not be found in the database", parent.getId());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(TEAM_NOT_FOUND);
                }
            }
            case CLUB -> {
                try {
                    Club club = clubService.getClub(parent.getOwnerId()).orElseThrow();
                    if (club.getManager().equals(user.getId())) {
                        commentService.delete(comment);
                        return ResponseEntity.status(HttpStatus.OK).body(COMMENT_DELETED);
                    } else {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(NOT_MANAGER_CLUB);
                    }
                } catch (NoSuchElementException e) {
                    logger.error("Club with id: {} wasn't found in the database", parent.getId());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CLUB_NOT_FOUND);
                }
            }
            default -> {
                logger.error(OWNERTYPE_NOT_FOUND);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(OWNERTYPE_NOT_FOUND);
            }
        }
    }





    /**
     * Endpoint for unflagging a flagged feed post
     *
     * @param postId the id of the post to unflag
     * @return a response entity with the result of the deletion
     */
    @PostMapping("/feed/unflag/{postId}")
    public ResponseEntity<String> unflagPost(@PathVariable Long postId) {
        logger.info("Unflagging post with id: {} in the database", postId);
        try {
            FeedPost feedPost = feedPostService.getFeedPostById(postId).orElseThrow();
            String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userService.getUserByEmail(email);
            return unflagFeedPost(feedPost, user);
        } catch (NoSuchElementException e) {
            logger.error("Post with id: {} not found in the database", postId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found");
        } catch (Exception e) {
            logger.error("Error unflagging post with id: {} from the database", postId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error unflagging post");
        }
    }


    /**
     * Attempts to unflag a given feed post
     *
     * @param feedPost the feed post to unflag
     * @param user     the user attempting to unflag the post
     * @return a response entity with the result of unflagging the post
     */
    public ResponseEntity<String> unflagFeedPost(FeedPost feedPost, User user) {
        OwnerType ownerType = feedPost.getOwnerType();
        switch (ownerType) {
            case TEAM -> {
                try {
                    Team team = teamService.getTeam(feedPost.getOwnerId()).orElseThrow();
                    if (teamMemberService.getAllManagersFromTeam(team)
                            .stream()
                            .map(manager -> manager.getTeamMemberId().getUser())
                            .toList()
                            .contains(user)) {
                        feedPost.setFlagged(false);
                        feedPostService.save(feedPost);
                        return ResponseEntity.status(HttpStatus.OK).body("Post resolved.");
                    } else {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(NOT_MANAGER_TEAM);
                    }
                } catch (NoSuchElementException e) {
                    logger.error("Team with id: {} not found in the database", feedPost.getId());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(TEAM_NOT_FOUND);
                }
            }
            case CLUB -> {
                try {
                    Club club = clubService.getClub(feedPost.getOwnerId()).orElseThrow();
                    if (club.getManager().equals(user.getId())) {
                        feedPost.setFlagged(false);
                        feedPostService.save(feedPost);
                        return ResponseEntity.status(HttpStatus.OK).body("Post resolved.");
                    } else {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(NOT_MANAGER_CLUB);
                    }
                } catch (NoSuchElementException e) {
                    logger.error("Club with id: {} not found in the database", feedPost.getId());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CLUB_NOT_FOUND);
                }
            }
            default -> {
                logger.error(OWNERTYPE_INVALID);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(OWNERTYPE_INVALID);
            }
        }
    }


    /**
     * Endpoint for unflagging a flagged comment
     *
     * @param commentId the id of the comment to unflag
     * @return a response entity with the result of the deletion
     */
    @PostMapping("/feed/unflagComment/{commentId}")
    public ResponseEntity<String> unflagCommentMapping(@PathVariable Long commentId) {
        logger.info("Unflagging comment with id: {} in the database", commentId);
        try {
            Comment comment = commentService.getCommentById(commentId).orElseThrow();
            String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userService.getUserByEmail(email);
            return unflagComment(comment, user);
        } catch (NoSuchElementException e) {
            logger.error("Comment with id: {} not found in the database", commentId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Comment not found");
        } catch (Exception e) {
            logger.error("Error unflagging comment with id: {} from the database", commentId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error unflagging comment");
        }
    }


    /**
     * Attempts to unflag a given comment
     *
     * @param comment  the comment to unflag
     * @param user     the user attempting to unflag the post
     * @return a response entity with the result of unflagging the comment
     */
    public ResponseEntity<String> unflagComment(Comment comment, User user) {
        Optional<FeedPost> parentPost = feedPostService.getFeedPostById(comment.getPostId());
        FeedPost parent = null;
        OwnerType ownerType = null;
        if (parentPost.isPresent()) {
            ownerType = parentPost.get().getOwnerType();
            parent = parentPost.get();
        }
        switch (Objects.requireNonNull(ownerType)) {
            case TEAM -> {
                try {
                    Team team = teamService.getTeam(parent.getOwnerId()).orElseThrow();
                    if (teamMemberService.getAllManagersFromTeam(team)
                            .stream()
                            .map(manager -> manager.getTeamMemberId().getUser())
                            .toList()
                            .contains(user)) {
                        comment.setFlagged(false);
                        commentService.save(comment);
                        return ResponseEntity.status(HttpStatus.OK).body("Comment resolved.");
                    } else {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(NOT_MANAGER_TEAM);
                    }
                } catch (NoSuchElementException e) {
                    logger.error("Team with id: {} wasn't found in the database", parent.getId());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(TEAM_NOT_FOUND);
                }
            }
            case CLUB -> {
                try {
                    Club club = clubService.getClub(parent.getOwnerId()).orElseThrow();
                    if (club.getManager().equals(user.getId())) {
                        comment.setFlagged(false);
                        commentService.save(comment);
                        return ResponseEntity.status(HttpStatus.OK).body("Comment resolved.");
                    } else {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(NOT_MANAGER_CLUB);
                    }
                } catch (NoSuchElementException e) {
                    logger.error("Club with id: {} was not found in the database", parent.getId());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CLUB_NOT_FOUND);
                }
            }
            default -> {
                logger.error(OWNERTYPE_INVALID);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(OWNERTYPE_INVALID);
            }
        }
    }
}

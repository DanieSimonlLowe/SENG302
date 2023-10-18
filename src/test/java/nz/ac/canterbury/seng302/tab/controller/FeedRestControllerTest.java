package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.service.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithAnonymousUser
class FeedRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    /**
     * The ModerationAPIController for moderating comments.
     */
    @MockBean
    private ModerationAPIController moderationAPIController;

    /**
     * The FeedPostService for database logic
     */
    @MockBean
    private FeedPostService feedPostService;

    /**
     * The UserService for database logic
     */
    @MockBean
    private UserService userService;

    /**
     * The TeamService for database logic
     */
    @MockBean
    private TeamService teamService;

    /**
     * The TeamMemberService for database logic
     */
    @MockBean
    private TeamMemberService teamMemberService;

    /**
     * The ClubService for database logic
     */
    @MockBean
    private ClubService clubService;

    /**
     * The FeedRestController for handling requests
     */
    private FeedRestController feedRestController;

    /** The CommentService for accessing the comments in the database */
    @MockBean
    private CommentService commentService;

    /** The NumCommentsService for accessing the number of comments in the database */
    @MockBean
    private NumCommentsService numCommentsService;

    @MockBean
    private NumPostsService numPostsService;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        feedRestController = new FeedRestController(
                feedPostService,
                userService,
                teamService,
                teamMemberService,
                clubService,
                moderationAPIController,
                commentService,
                numCommentsService,
                numPostsService);
    }

    @Test
    void testDeleteFeedPost_NonExistent() {
        Mockito.when(feedPostService.getFeedPostById(-1L)).thenThrow(new NoSuchElementException());
        ResponseEntity<String> responseEntity = feedRestController.deletePost(-1L);
        Assertions.assertEquals("Post not found", responseEntity.getBody());
    }

    @Test
    void testDeleteFeedPost_ServerError() {
        Mockito.when(feedPostService.getFeedPostById(1L)).thenThrow(new RuntimeException());
        ResponseEntity<String> responseEntity = feedRestController.deletePost(1L);
        Assertions.assertEquals("Error deleting post", responseEntity.getBody());
    }

    @Test
    void testDeleteUserFeedPost_BlueSky() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn("email");

        FeedPost feedPost = Mockito.mock(FeedPost.class);
        User user = Mockito.mock(User.class);

        Mockito.when(feedPostService.getFeedPostById(1L)).thenReturn(Optional.ofNullable(feedPost));
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(Objects.requireNonNull(feedPost).getOwnerType()).thenReturn(OwnerType.USER);

        ResponseEntity<String> responseEntity = feedRestController.deletePost(1L);
        Assertions.assertEquals("Post deleted", responseEntity.getBody());
    }

    @Test
    void testDeleteUserFeedPost_NoPermission() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn("email");

        FeedPost feedPost = Mockito.mock(FeedPost.class);
        User user = Mockito.mock(User.class);

        Mockito.when(feedPostService.getFeedPostById(1L)).thenReturn(Optional.ofNullable(feedPost));
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(Objects.requireNonNull(feedPost).getOwnerType()).thenReturn(OwnerType.USER);
        Mockito.when(feedPost.getOwnerId()).thenReturn(2L);

        ResponseEntity<String> responseEntity = feedRestController.deletePost(1L);
        Assertions.assertEquals("User does not have permission to delete post", responseEntity.getBody());
    }

    @Test
    void testDeleteTeamFeedPost_NotFound() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn("email");

        FeedPost feedPost = Mockito.mock(FeedPost.class);
        User user = Mockito.mock(User.class);

        Mockito.when(feedPostService.getFeedPostById(1L)).thenReturn(Optional.ofNullable(feedPost));
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(Objects.requireNonNull(feedPost).getOwnerType()).thenReturn(OwnerType.TEAM);
        Mockito.when(teamService.getTeam(1L)).thenThrow(new NoSuchElementException());

        ResponseEntity<String> responseEntity = feedRestController.deletePost(1L);
        Assertions.assertEquals("Team not found", responseEntity.getBody());
    }

    @Test
    void testDeleteTeamFeedPost_NotAuthorized() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn("email");

        FeedPost feedPost = Mockito.mock(FeedPost.class);
        User user = Mockito.mock(User.class);
        TeamMemberId teamMemberId = Mockito.mock(TeamMemberId.class);
        Team team = Mockito.mock(Team.class);
        TeamMember manager = Mockito.mock(TeamMember.class);

        List<TeamMember> managers = new ArrayList<>();
        managers.add(manager);

        Mockito.when(feedPostService.getFeedPostById(1L)).thenReturn(Optional.ofNullable(feedPost));
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(Objects.requireNonNull(feedPost).getOwnerType()).thenReturn(OwnerType.TEAM);
        Mockito.when(teamService.getTeam(Mockito.anyLong())).thenReturn(Optional.ofNullable(team));
        Mockito.when(teamMemberService.getAllManagersFromTeam(team)).thenReturn(managers);

        Mockito.when(manager.getTeamMemberId()).thenReturn(teamMemberId);

        ResponseEntity<String> responseEntity = feedRestController.deletePost(1L);
        Assertions.assertEquals("User is not a manager of the team", responseEntity.getBody());
    }

    @Test
    void testGetComments_blueSkyScenario() {
        User user = Mockito.mock(User.class);
        Mockito.when(user.getProfilePicName()).thenReturn("test");
        Mockito.when(user.getFirstName()).thenReturn("test");
        Mockito.when(user.getLastName()).thenReturn("test");

        Comment comment = Mockito.mock(Comment.class);
        Mockito.when(comment.getAuthor()).thenReturn(user);
        Mockito.when(comment.getDateTime()).thenReturn(LocalDateTime.now());
        Mockito.when(comment.getMessage()).thenReturn("test");
        Mockito.when(comment.getId()).thenReturn(1L);

        Comment reply = Mockito.mock(Comment.class);
        Mockito.when(reply.getAuthor()).thenReturn(user);
        Mockito.when(reply.getDateTime()).thenReturn(LocalDateTime.now());
        Mockito.when(reply.getMessage()).thenReturn("test");

        Mockito.when(commentService.getAllCommentsByPostId(2L)).thenReturn(List.of(comment));
        Mockito.when(commentService.findAllByParentCommentId(1L)).thenReturn(List.of(reply));


        ResponseEntity<String> responseEntity = feedRestController.getComments(2L, 3L);
        Mockito.verify(commentService, Mockito.times(1)).getAllCommentsByPostId(2L);
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void testGetComments_noComments() {
        Mockito.when(commentService.getAllCommentsByPostId(1L)).thenReturn(List.of());
        ResponseEntity<String> responseEntity = feedRestController.getComments(2L, 3L);
        Mockito.verify(commentService, Mockito.times(1)).getAllCommentsByPostId(2L);
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void testAddCommentWithTeam_Success() {
        User user = Mockito.mock(User.class);
        FeedPost feedPost = Mockito.mock(FeedPost.class);
        Comment comment = Mockito.mock(Comment.class);

        String requestBody = "{\"message\":\"Test message\",\"parentCommentId\":\"-1\",\"postId\":\"123\"}";
        Mockito.when(userService.getUserByEmail(Mockito.anyString())).thenReturn(user);
        Mockito.when(commentService.save(Mockito.any(Comment.class))).thenReturn(comment);
        Mockito.when(feedPostService.getFeedPostById(Mockito.anyLong())).thenReturn(Optional.ofNullable(feedPost));
        Mockito.when(feedPost.getOwnerType()).thenReturn(OwnerType.TEAM);
        Mockito.when(moderationAPIController.moderateComment(Mockito.any(Comment.class))).thenReturn(comment);

        ResponseEntity<String> response = feedRestController.addComment(requestBody);

        Mockito.verify(commentService, Mockito.times(1)).save(Mockito.any(Comment.class));

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals("Comment added to database", response.getBody());
    }

    @Test
    void testAddCommentWithClubAndNoNumComments_Success() {
        User user = Mockito.mock(User.class);
        FeedPost feedPost = Mockito.mock(FeedPost.class);
        Comment comment = Mockito.mock(Comment.class);
        Club club = Mockito.mock(Club.class);

        String requestBody = "{\"message\":\"Test message\",\"parentCommentId\":\"-1\",\"postId\":\"123\"}";
        Mockito.when(userService.getUserByEmail(Mockito.anyString())).thenReturn(user);
        Mockito.when(commentService.save(Mockito.any(Comment.class))).thenReturn(comment);
        Mockito.when(feedPostService.getFeedPostById(Mockito.anyLong())).thenReturn(Optional.of(feedPost));
        Mockito.when(feedPost.getOwnerType()).thenReturn(OwnerType.CLUB);
        Mockito.when(feedPost.getOwnerId()).thenReturn(1L);
        Mockito.when(clubService.getClub(Mockito.anyLong())).thenReturn(Optional.of(club));
        Mockito.when(moderationAPIController.moderateComment(Mockito.any(Comment.class))).thenReturn(comment);

        ResponseEntity<String> response = feedRestController.addComment(requestBody);

        Mockito.verify(commentService, Mockito.times(1)).save(Mockito.any(Comment.class));

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals("Comment added to database", response.getBody());
    }

    @Test
    void testAddCommentWithClubAndNumComments_Success() {
        User user = Mockito.mock(User.class);
        FeedPost feedPost = Mockito.mock(FeedPost.class);
        Comment comment = Mockito.mock(Comment.class);
        Club club = Mockito.mock(Club.class);
        NumComments numComments = Mockito.mock(NumComments.class);

        String requestBody = "{\"message\":\"Test message\",\"parentCommentId\":\"-1\",\"postId\":\"123\"}";
        Mockito.when(userService.getUserByEmail(Mockito.anyString())).thenReturn(user);
        Mockito.when(commentService.save(Mockito.any(Comment.class))).thenReturn(comment);
        Mockito.when(moderationAPIController.moderateComment(Mockito.any(Comment.class))).thenReturn(comment);
        Mockito.when(feedPostService.getFeedPostById(Mockito.anyLong())).thenReturn(Optional.of(feedPost));
        Mockito.when(feedPost.getOwnerType()).thenReturn(OwnerType.CLUB);
        Mockito.when(feedPost.getOwnerId()).thenReturn(1L);
        Mockito.when(clubService.getClub(Mockito.anyLong())).thenReturn(Optional.of(club));

        Mockito.when(numCommentsService.getByClubAndUser(Mockito.anyLong(), Mockito.anyLong())).thenReturn(Optional.of(numComments));

        ResponseEntity<String> response = feedRestController.addComment(requestBody);

        Mockito.verify(commentService, Mockito.times(1)).save(Mockito.any(Comment.class));

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals("Comment added to database", response.getBody());
    }

    @Test
    void testAddComment_InvalidRequest() {
        // Arrange
        String requestBody = "{\"message\":\"Test message\",\"parentCommentId\":\"abc\",\"postId\":\"123\"}";

        // Act
        ResponseEntity<String> response = feedRestController.addComment(requestBody);

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Invalid request.", response.getBody());
    }

    @Test
    void testDeleteTeamFeedPost_BlueSky() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn("email");

        FeedPost feedPost = Mockito.mock(FeedPost.class);
        User user = Mockito.mock(User.class);
        TeamMemberId teamMemberId = Mockito.mock(TeamMemberId.class);
        Team team = Mockito.mock(Team.class);
        TeamMember manager = Mockito.mock(TeamMember.class);

        List<TeamMember> managers = new ArrayList<>();
        managers.add(manager);

        Mockito.when(feedPostService.getFeedPostById(1L)).thenReturn(Optional.ofNullable(feedPost));
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(Objects.requireNonNull(feedPost).getOwnerType()).thenReturn(OwnerType.TEAM);
        Mockito.when(teamService.getTeam(Mockito.anyLong())).thenReturn(Optional.ofNullable(team));
        Mockito.when(teamMemberService.getAllManagersFromTeam(team)).thenReturn(managers);
        Mockito.when(manager.getTeamMemberId()).thenReturn(teamMemberId);
        Mockito.when(teamMemberId.getUser()).thenReturn(user);

        Mockito.when(manager.getTeamMemberId()).thenReturn(teamMemberId);

        ResponseEntity<String> responseEntity = feedRestController.deletePost(1L);
        Assertions.assertEquals("Post deleted", responseEntity.getBody());
    }

    @Test
    void testDeleteClubFeedPost_NotFound() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn("email");

        FeedPost feedPost = Mockito.mock(FeedPost.class);
        User user = Mockito.mock(User.class);

        Mockito.when(feedPostService.getFeedPostById(1L)).thenReturn(Optional.ofNullable(feedPost));
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(Objects.requireNonNull(feedPost).getOwnerType()).thenReturn(OwnerType.CLUB);
        Mockito.when(clubService.getClub(1L)).thenThrow(new NoSuchElementException());

        ResponseEntity<String> responseEntity = feedRestController.deletePost(1L);
        Assertions.assertEquals("Club not found", responseEntity.getBody());
    }

    @Test
    void testDeleteClubFeedPost_NotAuthorized() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn("email");

        FeedPost feedPost = Mockito.mock(FeedPost.class);
        User user = Mockito.mock(User.class);
        Club club = Mockito.mock(Club.class);

        Mockito.when(user.getId()).thenReturn(2L);
        Mockito.when(feedPostService.getFeedPostById(1L)).thenReturn(Optional.ofNullable(feedPost));
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(Objects.requireNonNull(feedPost).getOwnerType()).thenReturn(OwnerType.CLUB);
        Mockito.when(clubService.getClub(Mockito.anyLong())).thenReturn(Optional.ofNullable(club));
        Mockito.when(Objects.requireNonNull(club).getManager()).thenReturn(1L);

        ResponseEntity<String> responseEntity = feedRestController.deletePost(1L);
        Assertions.assertEquals("User is not a manager of the club", responseEntity.getBody());
    }

    @Test
    void testDeleteClubFeedPost_BlueSky() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn("email");

        FeedPost feedPost = Mockito.mock(FeedPost.class);
        User user = Mockito.mock(User.class);
        Club club = Mockito.mock(Club.class);

        Mockito.when(user.getId()).thenReturn(1L);
        Mockito.when(feedPostService.getFeedPostById(1L)).thenReturn(Optional.ofNullable(feedPost));
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(Objects.requireNonNull(feedPost).getOwnerType()).thenReturn(OwnerType.CLUB);
        Mockito.when(clubService.getClub(Mockito.anyLong())).thenReturn(Optional.ofNullable(club));
        Mockito.when(Objects.requireNonNull(club).getManager()).thenReturn(1L);

        ResponseEntity<String> responseEntity = feedRestController.deletePost(1L);
        Assertions.assertEquals("Post deleted", responseEntity.getBody());
    }

    @Test
    void deleteComment_user_ok_Test() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn("email");

        Comment comment = Mockito.mock(Comment.class);
        Mockito.when(comment.getPostId()).thenReturn(1L);

        Mockito.when(commentService.getCommentById(2L)).thenReturn(Optional.of(comment));

        FeedPost feedPost = Mockito.mock(FeedPost.class);
        User user = Mockito.mock(User.class);

        Mockito.when(user.getId()).thenReturn(5L);

        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(feedPostService.getFeedPostById(1L)).thenReturn(Optional.ofNullable(feedPost));

        Mockito.when(feedPost.getOwnerType()).thenReturn(OwnerType.USER);

        Mockito.when(feedPost.getOwnerId()).thenReturn(5L);

        ResponseEntity<String> responseEntity = feedRestController.deleteComment(2L);

        Assertions.assertEquals(HttpStatusCode.valueOf(200),responseEntity.getStatusCode());
    }

    @Test
    void deleteComment_user_bad_Test() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn("email");

        Comment comment = Mockito.mock(Comment.class);
        Mockito.when(comment.getPostId()).thenReturn(1L);

        Mockito.when(commentService.getCommentById(2L)).thenReturn(Optional.of(comment));

        FeedPost feedPost = Mockito.mock(FeedPost.class);
        User user = Mockito.mock(User.class);

        Mockito.when(user.getId()).thenReturn(5L);

        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(feedPostService.getFeedPostById(1L)).thenReturn(Optional.ofNullable(feedPost));

        Mockito.when(feedPost.getOwnerType()).thenReturn(OwnerType.USER);

        Mockito.when(feedPost.getOwnerId()).thenReturn(6L);

        ResponseEntity<String> responseEntity = feedRestController.deleteComment(2L);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED,responseEntity.getStatusCode());
    }


    @Test
    void deleteComment_team_ok_Test() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn("email");

        Comment comment = Mockito.mock(Comment.class);
        Mockito.when(comment.getPostId()).thenReturn(1L);

        Mockito.when(commentService.getCommentById(2L)).thenReturn(Optional.of(comment));

        FeedPost feedPost = Mockito.mock(FeedPost.class);
        User user = Mockito.mock(User.class);

        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(feedPostService.getFeedPostById(1L)).thenReturn(Optional.ofNullable(feedPost));

        Mockito.when(feedPost.getOwnerType()).thenReturn(OwnerType.TEAM);
        Mockito.when(feedPost.getOwnerId()).thenReturn(4L);

        Team team = Mockito.mock(Team.class);

        Mockito.when(teamService.getTeam(4L)).thenReturn(Optional.ofNullable(team));

        TeamMember teamMember = Mockito.mock(TeamMember.class);
        TeamMemberId teamMemberId = Mockito.mock(TeamMemberId.class);
        ArrayList arrayList = new ArrayList<>();
        arrayList.add(teamMember);
        Mockito.when(teamMember.getTeamMemberId()).thenReturn(teamMemberId);
        Mockito.when(teamMemberId.getUser()).thenReturn(user);

        Mockito.when(teamMemberService.getAllManagersFromTeam(team)).thenReturn(arrayList);

        ResponseEntity<String> responseEntity = feedRestController.deleteComment(2L);

        Assertions.assertEquals(HttpStatusCode.valueOf(200),responseEntity.getStatusCode(), responseEntity.getBody());
    }

    @Test
    void deleteComment_team_bad_Test() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn("email");

        Comment comment = Mockito.mock(Comment.class);
        Mockito.when(comment.getPostId()).thenReturn(1L);

        Mockito.when(commentService.getCommentById(2L)).thenReturn(Optional.of(comment));

        FeedPost feedPost = Mockito.mock(FeedPost.class);
        User user = Mockito.mock(User.class);

        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(feedPostService.getFeedPostById(1L)).thenReturn(Optional.ofNullable(feedPost));

        Mockito.when(feedPost.getOwnerType()).thenReturn(OwnerType.TEAM);
        Mockito.when(feedPost.getOwnerId()).thenReturn(4L);

        Team team = Mockito.mock(Team.class);

        Mockito.when(teamService.getTeam(4L)).thenReturn(Optional.ofNullable(team));

        TeamMember teamMember = Mockito.mock(TeamMember.class);
        TeamMemberId teamMemberId = Mockito.mock(TeamMemberId.class);
        ArrayList arrayList = new ArrayList<>();
        arrayList.add(teamMember);
        Mockito.when(teamMember.getTeamMemberId()).thenReturn(teamMemberId);
        User user1 = Mockito.mock(User.class);
        Mockito.when(user1.getId()).thenReturn(10L);
        Mockito.when(teamMemberId.getUser()).thenReturn(user1);

        Mockito.when(teamMemberService.getAllManagersFromTeam(team)).thenReturn(arrayList);

        ResponseEntity<String> responseEntity = feedRestController.deleteComment(2L);

        Assertions.assertEquals(HttpStatusCode.valueOf(401),responseEntity.getStatusCode(), responseEntity.getBody());
    }


    @Test
    void deleteComment_club_ok_Test() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn("email");

        Comment comment = Mockito.mock(Comment.class);
        Mockito.when(comment.getPostId()).thenReturn(1L);

        Mockito.when(commentService.getCommentById(2L)).thenReturn(Optional.of(comment));

        FeedPost feedPost = Mockito.mock(FeedPost.class);
        User user = Mockito.mock(User.class);
        Mockito.when(user.getId()).thenReturn(10L);

        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(feedPostService.getFeedPostById(1L)).thenReturn(Optional.ofNullable(feedPost));

        Mockito.when(feedPost.getOwnerType()).thenReturn(OwnerType.CLUB);
        Mockito.when(feedPost.getOwnerId()).thenReturn(4L);

        Club club = Mockito.mock(Club.class);
        Mockito.when(clubService.getClub(4L)).thenReturn(Optional.ofNullable(club));

        Mockito.when(club.getManager()).thenReturn(10L);

        ResponseEntity<String> responseEntity = feedRestController.deleteComment(2L);

        Assertions.assertEquals(HttpStatusCode.valueOf(200),responseEntity.getStatusCode(), responseEntity.getBody());
    }

    @Test
    void deleteComment_club_bad_Test() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn("email");

        Comment comment = Mockito.mock(Comment.class);
        Mockito.when(comment.getPostId()).thenReturn(1L);

        Mockito.when(commentService.getCommentById(2L)).thenReturn(Optional.of(comment));

        FeedPost feedPost = Mockito.mock(FeedPost.class);
        User user = Mockito.mock(User.class);
        Mockito.when(user.getId()).thenReturn(10L);

        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(feedPostService.getFeedPostById(1L)).thenReturn(Optional.ofNullable(feedPost));

        Mockito.when(feedPost.getOwnerType()).thenReturn(OwnerType.CLUB);
        Mockito.when(feedPost.getOwnerId()).thenReturn(4L);

        Club club = Mockito.mock(Club.class);
        Mockito.when(clubService.getClub(4L)).thenReturn(Optional.ofNullable(club));

        Mockito.when(club.getManager()).thenReturn(11L);

        ResponseEntity<String> responseEntity = feedRestController.deleteComment(2L);

        Assertions.assertEquals(HttpStatusCode.valueOf(401),responseEntity.getStatusCode(), responseEntity.getBody());
    }

    @Test
    void deleteComment_club_noClub_Test() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn("email");

        Comment comment = Mockito.mock(Comment.class);
        Mockito.when(comment.getPostId()).thenReturn(1L);

        Mockito.when(commentService.getCommentById(2L)).thenReturn(Optional.of(comment));

        FeedPost feedPost = Mockito.mock(FeedPost.class);
        User user = Mockito.mock(User.class);
        Mockito.when(user.getId()).thenReturn(10L);

        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(feedPostService.getFeedPostById(1L)).thenReturn(Optional.ofNullable(feedPost));

        Mockito.when(feedPost.getOwnerType()).thenReturn(OwnerType.CLUB);
        Mockito.when(feedPost.getOwnerId()).thenReturn(4L);

        Mockito.when(clubService.getClub(4L)).thenReturn(Optional.empty());


        ResponseEntity<String> responseEntity = feedRestController.deleteComment(2L);

        Assertions.assertEquals(HttpStatusCode.valueOf(401),responseEntity.getStatusCode(), responseEntity.getBody());
    }

    @Test
    void deleteComment_noComment_Test() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn("email");

        Mockito.when(commentService.getCommentById(2L)).thenReturn(Optional.empty());

        ResponseEntity<String> responseEntity = feedRestController.deleteComment(2L);

        Assertions.assertEquals(HttpStatusCode.valueOf(404),responseEntity.getStatusCode(), responseEntity.getBody());
    }

    @Test
    void deleteComment_error_Test() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn("email");

        Mockito.when(commentService.getCommentById(2L)).thenThrow(new RuntimeException(""));

        ResponseEntity<String> responseEntity = feedRestController.deleteComment(2L);

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,responseEntity.getStatusCode(), responseEntity.getBody());
    }

    @Test
    void deleteComment_team_noTeamMember_Test() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn("email");

        Comment comment = Mockito.mock(Comment.class);
        Mockito.when(comment.getPostId()).thenReturn(1L);

        Mockito.when(commentService.getCommentById(2L)).thenReturn(Optional.of(comment));

        FeedPost feedPost = Mockito.mock(FeedPost.class);
        User user = Mockito.mock(User.class);

        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(feedPostService.getFeedPostById(1L)).thenReturn(Optional.ofNullable(feedPost));

        Mockito.when(feedPost.getOwnerType()).thenReturn(OwnerType.TEAM);
        Mockito.when(feedPost.getOwnerId()).thenReturn(4L);

        Team team = Mockito.mock(Team.class);

        Mockito.when(teamService.getTeam(4L)).thenReturn(Optional.ofNullable(team));


        ArrayList arrayList = new ArrayList<>();

        Mockito.when(teamMemberService.getAllManagersFromTeam(team)).thenReturn(arrayList);

        ResponseEntity<String> responseEntity = feedRestController.deleteComment(2L);

        Assertions.assertEquals(HttpStatusCode.valueOf(401),responseEntity.getStatusCode(), responseEntity.getBody());
    }

    @Test
    void deleteComment_team_noTeam_Test() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn("email");

        Comment comment = Mockito.mock(Comment.class);
        Mockito.when(comment.getPostId()).thenReturn(1L);

        Mockito.when(commentService.getCommentById(2L)).thenReturn(Optional.of(comment));

        FeedPost feedPost = Mockito.mock(FeedPost.class);
        User user = Mockito.mock(User.class);

        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(feedPostService.getFeedPostById(1L)).thenReturn(Optional.ofNullable(feedPost));

        Mockito.when(feedPost.getOwnerType()).thenReturn(OwnerType.TEAM);
        Mockito.when(feedPost.getOwnerId()).thenReturn(4L);


        Mockito.when(teamService.getTeam(4L)).thenThrow(new NoSuchElementException());


        ResponseEntity<String> responseEntity = feedRestController.deleteComment(2L);

        Assertions.assertEquals(HttpStatusCode.valueOf(401),responseEntity.getStatusCode(), responseEntity.getBody());
    }



//    @Test
//    void testUnflagTeamFeedPost_BlueSky() {
//        Authentication authentication = Mockito.mock(Authentication.class);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        Mockito.when(authentication.getPrincipal()).thenReturn("email");
//
//        FeedPost feedPost = Mockito.mock(FeedPost.class);
//        User user = Mockito.mock(User.class);
//        Team team = Mockito.mock(Team.class);
//        TeamMember teamMember = Mockito.mock(TeamMember.class);
//        TeamMemberId teamMemberId = Mockito.mock(TeamMemberId.class);
//        List<TeamMember> teamMembers = Collections.singletonList(teamMember);
//
//        Mockito.when(feedPostService.getFeedPostById(1L)).thenReturn(Optional.ofNullable(feedPost));
//        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
//        Mockito.when(Objects.requireNonNull(feedPost).getOwnerType()).thenReturn(OwnerType.TEAM);
//        Mockito.when(feedPost.getId()).thenReturn(1L);
//        Mockito.when(teamService.getTeam(1L)).thenReturn(Optional.ofNullable(team));
//        Mockito.when(teamMemberService.getAllManagersFromTeam(team)).thenReturn(teamMembers);
//        Mockito.when(teamMember.getTeamMemberId()).thenReturn(teamMemberId);
//        Mockito.when(teamMemberId.getUser()).thenReturn(user);
//
//        ResponseEntity<String> responseEntity = feedRestController.unflagPost(1L);
//        Assertions.assertEquals("Post resolved.", responseEntity.getBody());
//    }


    @Test
    void testUnflagTeamFeedPost_TeamNotFound() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn("email");

        FeedPost feedPost = Mockito.mock(FeedPost.class);
        User user = Mockito.mock(User.class);
        Team team = Mockito.mock(Team.class);

        Mockito.when(feedPostService.getFeedPostById(1L)).thenReturn(Optional.ofNullable(feedPost));
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(Objects.requireNonNull(feedPost).getOwnerType()).thenReturn(OwnerType.TEAM);
        Mockito.when(feedPost.getId()).thenReturn(1L);
        Mockito.when(teamService.getTeam(-1L)).thenReturn(Optional.ofNullable(team));

        ResponseEntity<String> responseEntity = feedRestController.unflagPost(1L);
        Assertions.assertEquals("Team not found", responseEntity.getBody());
    }

//    @Test
//    void testUnflagClubFeedPost_BlueSky() {
//        Authentication authentication = Mockito.mock(Authentication.class);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        Mockito.when(authentication.getPrincipal()).thenReturn("email");
//
//        FeedPost feedPost = Mockito.mock(FeedPost.class);
//        User user = Mockito.mock(User.class);
//        Club club = Mockito.mock(Club.class);
//
//        Mockito.when(feedPostService.getFeedPostById(1L)).thenReturn(Optional.ofNullable(feedPost));
//        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
//        Mockito.when(Objects.requireNonNull(feedPost).getOwnerType()).thenReturn(OwnerType.CLUB);
//        Mockito.when(feedPost.getId()).thenReturn(1L);
//        Mockito.when(clubService.getClub(1L)).thenReturn(Optional.ofNullable(club));
//        Mockito.when(club.getManager()).thenReturn(1L);
//        Mockito.when(user.getId()).thenReturn(1L);
//
//        ResponseEntity<String> responseEntity = feedRestController.unflagPost(1L);
//        Assertions.assertEquals("Post resolved.", responseEntity.getBody());
//    }


    @Test
    void testUnflagClubFeedPost_ClubNotFound() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn("email");

        FeedPost feedPost = Mockito.mock(FeedPost.class);
        User user = Mockito.mock(User.class);
        Club club = Mockito.mock(Club.class);

        Mockito.when(feedPostService.getFeedPostById(1L)).thenReturn(Optional.ofNullable(feedPost));
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(Objects.requireNonNull(feedPost).getOwnerType()).thenReturn(OwnerType.CLUB);
        Mockito.when(feedPost.getId()).thenReturn(1L);
        Mockito.when(clubService.getClub(-1L)).thenReturn(Optional.ofNullable(club));


        ResponseEntity<String> responseEntity = feedRestController.unflagPost(1L);
        Assertions.assertEquals("Club not found", responseEntity.getBody());
    }

    @Test
    void testUnflagFeedPost_OwnerInvalid() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn("email");

        FeedPost feedPost = Mockito.mock(FeedPost.class);
        User user = Mockito.mock(User.class);

        Mockito.when(feedPostService.getFeedPostById(1L)).thenReturn(Optional.ofNullable(feedPost));
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(Objects.requireNonNull(feedPost).getOwnerType()).thenReturn(OwnerType.USER);

        ResponseEntity<String> responseEntity = feedRestController.unflagPost(1L);
        Assertions.assertEquals("OwnerType invalid", responseEntity.getBody());
    }

    @Test
    void testUnflagFeedPost_NoSuchElement() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn("email");

        FeedPost feedPost = Mockito.mock(FeedPost.class);
        User user = Mockito.mock(User.class);

        Mockito.when(feedPostService.getFeedPostById(-1L)).thenThrow(new NoSuchElementException());
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(Objects.requireNonNull(feedPost).getOwnerType()).thenReturn(OwnerType.USER);

        ResponseEntity<String> responseEntity = feedRestController.unflagPost(-1L);
        Assertions.assertEquals("Post not found", responseEntity.getBody());
    }

    @Test
    void testUnflagFeedPost_Exception() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn("email");

        FeedPost feedPost = Mockito.mock(FeedPost.class);
        User user = Mockito.mock(User.class);

        Mockito.when(feedPostService.getFeedPostById(-1L)).thenThrow(new RuntimeException());
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(Objects.requireNonNull(feedPost).getOwnerType()).thenReturn(OwnerType.USER);

        ResponseEntity<String> responseEntity = feedRestController.unflagPost(-1L);
        Assertions.assertEquals("Error unflagging post", responseEntity.getBody());
    }


//    @Test
//    void testUnflagTeamComment_BlueSky() {
//        Authentication authentication = Mockito.mock(Authentication.class);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        Mockito.when(authentication.getPrincipal()).thenReturn("email");
//
//        Comment comment = Mockito.mock(Comment.class);
//        FeedPost feedPost = Mockito.mock(FeedPost.class);
//        User user = Mockito.mock(User.class);
//
//        Team team = Mockito.mock(Team.class);
//        TeamMember teamMember = Mockito.mock(TeamMember.class);
//        TeamMemberId teamMemberId = Mockito.mock(TeamMemberId.class);
//        List<TeamMember> teamMembers = Collections.singletonList(teamMember);
//
//        Mockito.when(commentService.getCommentById(1L)).thenReturn(Optional.ofNullable(comment));
//        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
//        Mockito.when(comment.getPostId()).thenReturn(1L);
//        Mockito.when(feedPostService.getFeedPostById(1L)).thenReturn(Optional.ofNullable(feedPost));
//        Mockito.when(Objects.requireNonNull(feedPost).getOwnerType()).thenReturn(OwnerType.TEAM);
//        Mockito.when(feedPost.getId()).thenReturn(1L);
//        Mockito.when(teamService.getTeam(1L)).thenReturn(Optional.ofNullable(team));
//        Mockito.when(teamMemberService.getAllManagersFromTeam(team)).thenReturn(teamMembers);
//        Mockito.when(teamMember.getTeamMemberId()).thenReturn(teamMemberId);
//        Mockito.when(teamMemberId.getUser()).thenReturn(user);
//
//        ResponseEntity<String> responseEntity = feedRestController.unflagCommentMapping(1L);
//        Assertions.assertEquals("Comment resolved.", responseEntity.getBody());
//    }


//    @Test
//    void testUnflagClubComment_BlueSky() {
//        Authentication authentication = Mockito.mock(Authentication.class);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        Mockito.when(authentication.getPrincipal()).thenReturn("email");
//
//        Comment comment = Mockito.mock(Comment.class);
//        FeedPost feedPost = Mockito.mock(FeedPost.class);
//        User user = Mockito.mock(User.class);
//        Club club = Mockito.mock(Club.class);
//
//        Mockito.when(commentService.getCommentById(1L)).thenReturn(Optional.ofNullable(comment));
//        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
//        Mockito.when(comment.getPostId()).thenReturn(1L);
//        Mockito.when(feedPostService.getFeedPostById(1L)).thenReturn(Optional.ofNullable(feedPost));
//        Mockito.when(Objects.requireNonNull(feedPost).getOwnerType()).thenReturn(OwnerType.CLUB);
//
//        Mockito.when(feedPost.getId()).thenReturn(1L);
//        Mockito.when(clubService.getClub(1L)).thenReturn(Optional.ofNullable(club));
//        Mockito.when(club.getManager()).thenReturn(1L);
//        Mockito.when(user.getId()).thenReturn(1L);
//
//        ResponseEntity<String> responseEntity = feedRestController.unflagCommentMapping(1L);
//        Assertions.assertEquals("Comment resolved.", responseEntity.getBody());
//    }

    @Test
    void testUnflagTeamComment_TeamNotFound() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn("email");

        Comment comment = Mockito.mock(Comment.class);
        FeedPost feedPost = Mockito.mock(FeedPost.class);
        User user = Mockito.mock(User.class);

        Mockito.when(commentService.getCommentById(1L)).thenReturn(Optional.ofNullable(comment));
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(comment.getPostId()).thenReturn(1L);
        Mockito.when(feedPostService.getFeedPostById(1L)).thenReturn(Optional.ofNullable(feedPost));
        Mockito.when(Objects.requireNonNull(feedPost).getOwnerType()).thenReturn(OwnerType.TEAM);

        ResponseEntity<String> responseEntity = feedRestController.unflagCommentMapping(1L);
        Assertions.assertEquals("Team not found", responseEntity.getBody());
    }


    @Test
    void testUnflagClubComment_ClubNotFound() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn("email");

        Comment comment = Mockito.mock(Comment.class);
        FeedPost feedPost = Mockito.mock(FeedPost.class);
        User user = Mockito.mock(User.class);

        Mockito.when(commentService.getCommentById(1L)).thenReturn(Optional.ofNullable(comment));
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(comment.getPostId()).thenReturn(1L);
        Mockito.when(feedPostService.getFeedPostById(1L)).thenReturn(Optional.ofNullable(feedPost));
        Mockito.when(Objects.requireNonNull(feedPost).getOwnerType()).thenReturn(OwnerType.CLUB);

        ResponseEntity<String> responseEntity = feedRestController.unflagCommentMapping(1L);
        Assertions.assertEquals("Club not found", responseEntity.getBody());
    }


    @Test
    void testUnflagComment_NotFound() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn("email");
        Mockito.when(commentService.getCommentById(-1L)).thenThrow(new RuntimeException());
        ResponseEntity<String> responseEntity = feedRestController.unflagCommentMapping(1L);
        Assertions.assertEquals("Comment not found", responseEntity.getBody());
    }


    @Test
    void testUnflagComment_Exception() {

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn("email");

        Comment comment = Mockito.mock(Comment.class);
        User user = Mockito.mock(User.class);

        Mockito.when(commentService.getCommentById(1L)).thenReturn(Optional.ofNullable(comment));
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(comment.getPostId()).thenThrow(new RuntimeException());

        ResponseEntity<String> responseEntity = feedRestController.unflagCommentMapping(1L);
        Assertions.assertEquals("Error unflagging comment", responseEntity.getBody());
    }

    @Test
    void testDeleteComment_NonExistent() {
        Mockito.when(commentService.getCommentById(-1L)).thenThrow(new NoSuchElementException());
        ResponseEntity<String> responseEntity = feedRestController.deleteCommentMapping(-1L);
        Assertions.assertEquals("Comment not found", responseEntity.getBody());
    }

    @Test
    void testDeleteComment_ServerError() {
        Mockito.when(commentService.getCommentById(1L)).thenThrow(new RuntimeException());
        ResponseEntity<String> responseEntity = feedRestController.deleteCommentMapping(1L);
        Assertions.assertEquals("Error deleting comment", responseEntity.getBody());
    }

    @Test
    void testDeleteClubComment_BlueSky() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn("email");

        FeedPost feedPost = Mockito.mock(FeedPost.class);
        Comment comment = Mockito.mock(Comment.class);
        User user = Mockito.mock(User.class);
        Club club = Mockito.mock(Club.class);

        Mockito.when(commentService.getCommentById(1L)).thenReturn(Optional.ofNullable(comment));
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(comment.getPostId()).thenReturn(1L);
        Mockito.when(feedPostService.getFeedPostById(1L)).thenReturn(Optional.of(feedPost));
        Mockito.when(Objects.requireNonNull(feedPost).getOwnerType()).thenReturn(OwnerType.CLUB);
        Mockito.when(feedPost.getOwnerId()).thenReturn(1L);
        Mockito.when(clubService.getClub(1L)).thenReturn(Optional.ofNullable(club));
        Mockito.when(club.getManager()).thenReturn(1L);
        Mockito.when(user.getId()).thenReturn(1L);
        ResponseEntity<String> responseEntity = feedRestController.deleteCommentMapping(1L);
        Assertions.assertEquals("Comment deleted", responseEntity.getBody());
    }


    @Test
    void testDeleteTeamComment_BlueSky() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn("email");

        FeedPost feedPost = Mockito.mock(FeedPost.class);
        Comment comment = Mockito.mock(Comment.class);
        User user = Mockito.mock(User.class);
        Team team = Mockito.mock(Team.class);
        TeamMember teamMember = Mockito.mock(TeamMember.class);
        TeamMemberId teamMemberId = Mockito.mock(TeamMemberId.class);
        List<TeamMember> teamMembers = Collections.singletonList(teamMember);

        Mockito.when(commentService.getCommentById(1L)).thenReturn(Optional.ofNullable(comment));
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(comment.getPostId()).thenReturn(1L);
        Mockito.when(feedPostService.getFeedPostById(1L)).thenReturn(Optional.of(feedPost));
        Mockito.when(Objects.requireNonNull(feedPost).getOwnerType()).thenReturn(OwnerType.TEAM);
        Mockito.when(feedPost.getOwnerId()).thenReturn(1L);
        Mockito.when(teamService.getTeam(1L)).thenReturn(Optional.ofNullable(team));

        Mockito.when(teamMemberService.getAllManagersFromTeam(team)).thenReturn(teamMembers);
        Mockito.when(teamMember.getTeamMemberId()).thenReturn(teamMemberId);
        Mockito.when(teamMemberId.getUser()).thenReturn(user);

        ResponseEntity<String> responseEntity = feedRestController.deleteCommentMapping(1L);
        Assertions.assertEquals("Comment deleted", responseEntity.getBody());
    }


    @Test
    void testDeleteClubComment_NoClubFound() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn("email");

        FeedPost feedPost = Mockito.mock(FeedPost.class);
        Comment comment = Mockito.mock(Comment.class);
        User user = Mockito.mock(User.class);

        Mockito.when(commentService.getCommentById(1L)).thenReturn(Optional.ofNullable(comment));
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(comment.getPostId()).thenReturn(1L);
        Mockito.when(feedPostService.getFeedPostById(1L)).thenReturn(Optional.of(feedPost));
        Mockito.when(Objects.requireNonNull(feedPost).getOwnerType()).thenReturn(OwnerType.CLUB);

        ResponseEntity<String> responseEntity = feedRestController.deleteCommentMapping(1L);
        Assertions.assertEquals("Club not found", responseEntity.getBody());
    }


    @Test
    void testDeleteTeamComment_TeamNotFound() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn("email");

        FeedPost feedPost = Mockito.mock(FeedPost.class);
        Comment comment = Mockito.mock(Comment.class);
        User user = Mockito.mock(User.class);

        Mockito.when(commentService.getCommentById(1L)).thenReturn(Optional.ofNullable(comment));
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(comment.getPostId()).thenReturn(1L);
        Mockito.when(feedPostService.getFeedPostById(1L)).thenReturn(Optional.of(feedPost));
        Mockito.when(Objects.requireNonNull(feedPost).getOwnerType()).thenReturn(OwnerType.TEAM);
        Mockito.when(teamService.getTeam(1L)).thenThrow(new NoSuchElementException());

        ResponseEntity<String> responseEntity = feedRestController.deleteCommentMapping(1L);
        Assertions.assertEquals("Team not found", responseEntity.getBody());
    }

}


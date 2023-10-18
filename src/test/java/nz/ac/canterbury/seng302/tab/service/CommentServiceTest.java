package nz.ac.canterbury.seng302.tab.service;

import jakarta.annotation.Resource;
import nz.ac.canterbury.seng302.tab.entity.Comment;
import nz.ac.canterbury.seng302.tab.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class CommentServiceTest {

    @Resource
    private CommentService commentService;

    @MockBean
    private CommentRepository commentRepository;

    @Test
    void getCommentById_test() {
        commentService.getCommentById(1L);
        Mockito.verify(commentRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    void getAllCommentsByPostId_test() {
        commentService.getAllCommentsByPostId(1L);
        Mockito.verify(commentRepository, Mockito.times(1)).findAllByPostIdAndParentCommentIdIsNull(1L);
    }

    @Test
    void findAllByParentCommentId_test() {
        commentService.findAllByParentCommentId(1L);
        Mockito.verify(commentRepository, Mockito.times(1)).findAllByParentCommentId(1L);
    }

    @Test
    void save_test() {
        Comment comment = Mockito.mock(Comment.class);
        commentService.save(comment);
        Mockito.verify(commentRepository, Mockito.times(1)).save(comment);
    }

    @Test
    void getFlaggedClubComments_test() {
        commentService.getFlaggedClubComments(1L);
        Mockito.verify(commentRepository, Mockito.times(1)).findFlaggedClubComments(1L);
    }

    @Test
    void getFlaggedTeamComments_test() {
        commentService.getFlaggedTeamComments(1L);
        Mockito.verify(commentRepository, Mockito.times(1)).findFlaggedTeamComments(1L);
    }


    @Test
    void delete_test() {
        Comment comment = Mockito.mock(Comment.class);
        commentService.delete(comment);
        Mockito.verify(commentRepository, Mockito.times(1)).delete(comment);
    }
}

package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Comment;
import nz.ac.canterbury.seng302.tab.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {


    /** The team repository */
    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }


    /**
     * Saves a comment to the database
     * @param comment the comment to save
     * @return the saved comment
     */
    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    /**
     * Gets optional comment object by id
     * @param id The id of the comment
     * @return A optional comment by the given ID
     */
    public Optional<Comment> getCommentById(long id) {
        return commentRepository.findById(id);
    }

    /**
     * Finds all the comments from persistence given a post id
     * @param id the id of the post
     * @return a list of all comments on the post
     */
    public List<Comment> getAllCommentsByPostId(long id) {
        return commentRepository.findAllByPostIdAndParentCommentIdIsNull(id);
    }

    /**
     * Finds all replies to a comment given the parent comment id
     * @param id the id of the parent comment
     * @return a list of all replies to a comment
     */
    public List<Comment> findAllByParentCommentId(long id) {
        return commentRepository.findAllByParentCommentId(id);
    }

    /**
     * Finds all the flagged comments for one specific club's feed
     * @param clubId the id of the club
     * @return a list of flagged comments
     */
    public List<Comment> getFlaggedClubComments(Long clubId) {
        return commentRepository.findFlaggedClubComments(clubId);
    }


    /**
     * Finds all the flagged comments for one specific team's feed
     * @param teamId the id of the team
     * @return a list of flagged comments
     */
    public List<Comment> getFlaggedTeamComments(Long teamId) {
        return commentRepository.findFlaggedTeamComments(teamId);
    }


    /**
     * Deletes a comment from persistence
     * @param comment the comment to delete
     */
    public void delete(Comment comment) {
        commentRepository.delete(comment);
    }
}

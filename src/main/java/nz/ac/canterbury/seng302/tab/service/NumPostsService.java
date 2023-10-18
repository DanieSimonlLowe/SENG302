package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.NumPosts;
import nz.ac.canterbury.seng302.tab.repository.NumPostsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NumPostsService {

    /**
     * The repository for the NumPosts entity
     */
    private final NumPostsRepository numCommentsRepository;

    @Autowired
    public NumPostsService(NumPostsRepository numCommentsRepository) {
        this.numCommentsRepository = numCommentsRepository;
    }

    public void save(NumPosts numComments) {
        numCommentsRepository.save(numComments);
    }

    /**
     * Get all the NumPosts for a club
     * @param id the club id
     * @return a list of NumPosts ordered in decreasing order of number of posts and then by last updated
     */
    public List<NumPosts> getAllByClub(Long id) {
        return numCommentsRepository.findAllByClubIdOrderByCountDescLastUpdatedAsc(id);
    }

    /**
     * Get the top three NumPosts for a club. This is ordered from most posts to least posts.
     * @param id the club id
     * @return a list of NumPosts
     */
    public List<NumPosts> getTopThreeByClub(Long id) {
        List<NumPosts> allPosts = getAllByClub(id);

        if (allPosts.size() < 3) {
            return allPosts; // Return the entire list if it has less than 3 elements
        } else {
            return new ArrayList<>(allPosts.subList(0, 3)); // Otherwise, return the top 3 elements
        }
    }

    /**
     * Get the NumPosts for a user in a club
     * @param clubId the club id
     * @param userId the user id
     * @return an optional NumPosts object containing that users number of posts in that club
     */
    public Optional<NumPosts> getByClubAndUser(Long clubId, Long userId) {
        return numCommentsRepository.findByClubIdAndUserId(clubId, userId);
    }

}

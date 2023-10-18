package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.NumComments;
import nz.ac.canterbury.seng302.tab.repository.NumCommentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class NumCommentsService {

    private NumCommentsRepository numCommentsRepository;

    @Autowired
    public NumCommentsService(NumCommentsRepository numCommentsRepository) {
        this.numCommentsRepository = numCommentsRepository;
    }

    public void save(NumComments numComments) {
        numCommentsRepository.save(numComments);
    }

    /**
     * Get all leaderboard entries for a given club
     * @param id The id of the club
     * @return A list of all the leaderboard entities for a given club
     */
    public List<NumComments> getAllByClub(Long id) {
        return numCommentsRepository.findAllByClubIdOrderByCountDescLastUpdatedAsc(id);
    }

    /**
     * Return the users leaderboard entity for a specific club
     * @param clubId The id of the club
     * @param userId The id of the user
     * @return The NumComments entity if it exists of the user for the club
     */
    public Optional<NumComments> getByClubAndUser(Long clubId, Long userId) {
        return numCommentsRepository.findByClubIdAndUserId(clubId, userId);
    }

    /**
     * Return the top three objects for a given club. If there are less than three it returns them all
     * @param id The id of the club.
     * @return The top three objects for a given club.
     */
    public List<NumComments> getTopThreeByClub(Long id) {

        List<NumComments> allComments = getAllByClub(id);
        if(allComments.size() < 3) {
            return allComments;
        }
        return new ArrayList<>(allComments.subList(0, 3));
    }
}

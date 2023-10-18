package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.NumPosts;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NumPostsRepository extends CrudRepository<NumPosts, Long> {

    /**
     * Find the number of posts for all members in a club. Ordered by the number of posts descending.
     * @param id the club id
     * @return a list of NumPosts
     */
    List<NumPosts> findAllByClubIdOrderByCountDescLastUpdatedAsc(Long id);

    /**
     * Find the number of posts for a user in a club
     * @param clubId the club id
     * @param userId the user id
     * @return an optional NumPosts object containing that users number of posts in that club
     */
    Optional<NumPosts> findByClubIdAndUserId(Long clubId, Long userId);

}

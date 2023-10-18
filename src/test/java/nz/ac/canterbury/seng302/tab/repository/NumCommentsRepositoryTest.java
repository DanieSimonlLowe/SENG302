package nz.ac.canterbury.seng302.tab.repository;

import jakarta.annotation.Resource;
import nz.ac.canterbury.seng302.tab.entity.NumComments;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class NumCommentsRepositoryTest {

    @Resource
    NumCommentsRepository numCommentsRepository;

    @Test
    void findAllByClubIdOrderByNumPostsDesc() {
        List<NumComments> numComments = numCommentsRepository.findAllByClubIdOrderByCountDescLastUpdatedAsc(1L);
        Assertions.assertArrayEquals(new Integer[]{5, 2, 1}, numComments.stream().map(NumComments::getCount).toArray());
    }


}

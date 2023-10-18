package nz.ac.canterbury.seng302.tab.repository;

import jakarta.annotation.Resource;
import nz.ac.canterbury.seng302.tab.entity.NumPosts;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class NumPostsRepositoryTest {

    @Resource NumPostsRepository numPostsRepository;

    @Test
    void findAllByClubIdOrderByNumPostsDesc() {
        List<NumPosts> numPosts = numPostsRepository.findAllByClubIdOrderByCountDescLastUpdatedAsc(1L);
        Assertions.assertArrayEquals(new Integer[]{7, 4, 2}, numPosts.stream().map(NumPosts::getCount).toArray());
    }
}

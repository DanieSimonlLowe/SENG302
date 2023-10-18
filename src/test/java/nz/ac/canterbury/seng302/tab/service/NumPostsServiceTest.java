package nz.ac.canterbury.seng302.tab.service;

import jakarta.annotation.Resource;
import nz.ac.canterbury.seng302.tab.entity.NumPosts;
import nz.ac.canterbury.seng302.tab.repository.NumPostsRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

@SpringBootTest
class NumPostsServiceTest {

    @Resource NumPostsService numPostsService;

    @MockBean
    NumPostsRepository numPostsRepository;

    @Test
    void getAllByClub_test() {
        numPostsService.getAllByClub(1L);
        Mockito.verify(numPostsRepository).findAllByClubIdOrderByCountDescLastUpdatedAsc(1L);
    }

    @Test
    void getTopThreeByClub_test() {
        NumPosts numPosts1 = Mockito.mock(NumPosts.class);
        NumPosts numPosts2 = Mockito.mock(NumPosts.class);
        NumPosts numPosts3 = Mockito.mock(NumPosts.class);
        NumPosts numPosts4 = Mockito.mock(NumPosts.class);

        Mockito.when(numPosts1.getCount()).thenReturn(1);
        Mockito.when(numPosts2.getCount()).thenReturn(2);
        Mockito.when(numPosts3.getCount()).thenReturn(3);
        Mockito.when(numPosts4.getCount()).thenReturn(4);

        Mockito.when(numPostsRepository.findAllByClubIdOrderByCountDescLastUpdatedAsc(1L)).thenReturn(List.of(numPosts4, numPosts3, numPosts2, numPosts1));
        List<NumPosts> topThree = numPostsService.getTopThreeByClub(1L);
        Mockito.verify(numPostsRepository).findAllByClubIdOrderByCountDescLastUpdatedAsc(1L);
        Assertions.assertArrayEquals(new Integer[]{4, 3, 2}, topThree.stream().map(NumPosts::getCount).toArray());
    }

    @Test
    void getTopThreeByClub_lessThanThree() {
        NumPosts numPosts1 = Mockito.mock(NumPosts.class);
        NumPosts numPosts2 = Mockito.mock(NumPosts.class);

        Mockito.when(numPosts1.getCount()).thenReturn(1);
        Mockito.when(numPosts2.getCount()).thenReturn(2);

        Mockito.when(numPostsRepository.findAllByClubIdOrderByCountDescLastUpdatedAsc(1L)).thenReturn(List.of(numPosts2, numPosts1));
        List<NumPosts> topThree = numPostsService.getTopThreeByClub(1L);
        Mockito.verify(numPostsRepository).findAllByClubIdOrderByCountDescLastUpdatedAsc(1L);
        Assertions.assertArrayEquals(new Integer[]{2, 1}, topThree.stream().map(NumPosts::getCount).toArray());
    }

    @Test
    void getByClubAndUser_test() {
        numPostsService.getByClubAndUser(1L, 1L);
        Mockito.verify(numPostsRepository).findByClubIdAndUserId(1L, 1L);
    }
}

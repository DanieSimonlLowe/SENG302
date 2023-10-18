package nz.ac.canterbury.seng302.tab.service;

import jakarta.annotation.Resource;
import nz.ac.canterbury.seng302.tab.entity.NumComments;
import nz.ac.canterbury.seng302.tab.repository.NumCommentsRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

@SpringBootTest
class NumCommentsServiceTest {

    @Resource
    NumCommentsService numCommentsService;

    @MockBean
    NumCommentsRepository numCommentsRepository;

    @Test
    void getAllByClub_test() {
        numCommentsService.getAllByClub(1L);
        Mockito.verify(numCommentsRepository).findAllByClubIdOrderByCountDescLastUpdatedAsc(1L);
    }

    @Test
    void getTopThreeByClub_test() {
        NumComments numPosts1 = Mockito.mock(NumComments.class);
        NumComments numPosts2 = Mockito.mock(NumComments.class);
        NumComments numPosts3 = Mockito.mock(NumComments.class);
        NumComments numPosts4 = Mockito.mock(NumComments.class);

        Mockito.when(numPosts1.getCount()).thenReturn(1);
        Mockito.when(numPosts2.getCount()).thenReturn(2);
        Mockito.when(numPosts3.getCount()).thenReturn(3);
        Mockito.when(numPosts4.getCount()).thenReturn(4);

        Mockito.when(numCommentsRepository.findAllByClubIdOrderByCountDescLastUpdatedAsc(1L)).thenReturn(List.of(numPosts4, numPosts3, numPosts2, numPosts1));
        List<NumComments> topThree = numCommentsService.getTopThreeByClub(1L);
        Mockito.verify(numCommentsRepository).findAllByClubIdOrderByCountDescLastUpdatedAsc(1L);
        Assertions.assertArrayEquals(new Integer[]{4, 3, 2}, topThree.stream().map(NumComments::getCount).toArray());
    }

    @Test
    void getTopThreeByClub_lessThanThree() {
        NumComments numPosts1 = Mockito.mock(NumComments.class);
        NumComments numPosts2 = Mockito.mock(NumComments.class);

        Mockito.when(numPosts1.getCount()).thenReturn(1);
        Mockito.when(numPosts2.getCount()).thenReturn(2);

        Mockito.when(numCommentsRepository.findAllByClubIdOrderByCountDescLastUpdatedAsc(1L)).thenReturn(List.of(numPosts2, numPosts1));
        List<NumComments> topThree = numCommentsService.getTopThreeByClub(1L);
        Mockito.verify(numCommentsRepository).findAllByClubIdOrderByCountDescLastUpdatedAsc(1L);
        Assertions.assertArrayEquals(new Integer[]{2, 1}, topThree.stream().map(NumComments::getCount).toArray());
    }

    @Test
    void getByClubAndUser_test() {
        numCommentsService.getByClubAndUser(1L, 1L);
        Mockito.verify(numCommentsRepository).findByClubIdAndUserId(1L, 1L);
    }


}

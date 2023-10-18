package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.LineUp;
import nz.ac.canterbury.seng302.tab.repository.LineupPlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class LineupPlayerServiceTest {
    LineupPlayerService lineupPlayerService;
    LineUp lineup;
    @Mock
    LineupPlayerRepository lineupPlayerRepository;

    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.openMocks(this);

        lineup = Mockito.mock(LineUp.class);
        lineupPlayerRepository = Mockito.mock(LineupPlayerRepository.class);
        lineupPlayerService = new LineupPlayerService(lineupPlayerRepository);
    }
    @Test
    void findingLineup_byActivityId() {
        lineupPlayerService.getAllUserIdsByLineUpId(lineup.getId());
        Mockito.verify(lineupPlayerRepository).getUserIdsByLineUpId(lineup.getId());
    }
}

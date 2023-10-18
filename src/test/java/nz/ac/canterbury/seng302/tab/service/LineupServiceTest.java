package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.repository.LineupRepository;
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
public class LineupServiceTest {
    LineupService lineupService;
    Activity activity;
    @Mock
    LineupRepository lineupRepository;

    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.openMocks(this);

        activity = Mockito.mock(Activity.class);
        lineupRepository = Mockito.mock(LineupRepository.class);
        lineupService = new LineupService(lineupRepository);
    }

    @Test
    void findingLineup_byActivityId() {
        lineupService.getLineupByActivityId(activity.getId());
        Mockito.verify(lineupRepository).findByActivityId(activity.getId());
    }
}

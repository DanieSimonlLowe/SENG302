package nz.ac.canterbury.seng302.tab.repository;

import jakarta.annotation.Resource;
import nz.ac.canterbury.seng302.tab.entity.FeedAlerts;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class UserAlertsRepositoryTest {
    @Resource
    private UserAlertsRepository userAlertsRepository;

    /**
     * Tests that the feed alerts are found by userId
     */
    @Test
    void findByUserId() {
        FeedAlerts foundFeedAlerts = userAlertsRepository.findByUserId(1L);
        assertNotNull(foundFeedAlerts);
    }

}

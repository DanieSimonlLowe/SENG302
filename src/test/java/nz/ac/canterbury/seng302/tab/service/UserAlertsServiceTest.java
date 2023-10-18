package nz.ac.canterbury.seng302.tab.service;

import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.repository.UserAlertsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;


@SpringBootTest
@Transactional
@WithAnonymousUser
class UserAlertsServiceTest {

    UserAlertsService userAlertsService;

    UserAlertsRepository userAlertsRepository;

    FeedAlerts feedAlerts;

    /**
     * Sets up the mock repository and service
     */
    @BeforeEach
    void beforeEach() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("morgan.english@hotmail.com",null));
        userAlertsRepository = Mockito.mock(UserAlertsRepository.class);
        userAlertsService = new UserAlertsService(userAlertsRepository);
        feedAlerts = Mockito.mock(FeedAlerts.class);
    }

    /**
     * Tests that the feed alerts are found by userId
     */
    @Test
    void test_getUsersAlertNumber() {
        userAlertsService.getUserAlertsById(1L);
        Mockito.verify(userAlertsRepository).findByUserId(1L);
    }

    /**
     * Tests that the feed alerts can be saved
     */
    @Test
    void test_saveUserAlerts() {
        userAlertsService.save(feedAlerts);
        Mockito.verify(userAlertsRepository).save(feedAlerts);
    }
}

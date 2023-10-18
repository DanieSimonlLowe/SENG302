package nz.ac.canterbury.seng302.tab.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FeedAlertsTest {


    /**
     * Tests the FeedAlerts class
     */
    @Test
    void createFeedAlertsTest() {
        FeedAlerts feedAlerts = new FeedAlerts(1L);
        Assertions.assertEquals(1L, feedAlerts.getUserId());
        Assertions.assertEquals(0L, feedAlerts.getReadAlerts());
    }


    /**
     * Tests setting the read alerts
     */
    @Test
    void setReadAlertsTest() {
        FeedAlerts feedAlerts = new FeedAlerts(1L);
        feedAlerts.setReadAlerts(1L);
        Assertions.assertEquals(1L, feedAlerts.getReadAlerts());
    }

    /**
     * Tests getting the userId from feed alerts
     */
    @Test
    void getUserIdTest() {
        FeedAlerts feedAlerts = new FeedAlerts(1L);
        Assertions.assertEquals(1L, feedAlerts.getUserId());
    }

    /**
     * Tests getting the feedAlerts id
     */
    @Test
    void getIdTest() {
        FeedAlerts feedAlerts = new FeedAlerts(1L);
        Assertions.assertNull(feedAlerts.getId());
    }
}

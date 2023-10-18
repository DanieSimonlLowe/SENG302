package nz.ac.canterbury.seng302.tab.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class APIRateLimiterTest {

    @Mock
    private Map<Long, AtomicLong> clientRequestCounts;

    @Mock
    private Map<Long, Long> clientLastRequestTimes;

    @InjectMocks
    private APIRateLimiter apiRateLimiter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void allowRequest_FirstRequestWithinTimeWindow_ShouldAllow() {
        long clientId = 1L;
        long currentTimeMillis = 1000L;
        AtomicLong requestCount = new AtomicLong(1);

        when(clientLastRequestTimes.getOrDefault(clientId, 0L)).thenReturn(0L);
        when(clientRequestCounts.computeIfAbsent(clientId, k -> new AtomicLong(0))).thenReturn(requestCount);
        when(clientLastRequestTimes.put(clientId, currentTimeMillis)).thenReturn(0L);

        assertTrue(apiRateLimiter.allowRequest(clientId));
    }

    @Test
    void allowRequest_FirstRequestOutsideTimeWindow_ShouldAllow() {
        long clientId = 1L;
        long currentTimeMillis = 1000L;
        AtomicLong requestCount = new AtomicLong(1);

        when(clientLastRequestTimes.getOrDefault(clientId, 0L)).thenReturn(0L);
        when(clientRequestCounts.computeIfAbsent(clientId, k -> new AtomicLong(0))).thenReturn(requestCount);
        when(clientLastRequestTimes.put(clientId, currentTimeMillis)).thenReturn(0L);

        assertTrue(apiRateLimiter.allowRequest(clientId));
    }
}

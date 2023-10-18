package nz.ac.canterbury.seng302.tab.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class APIRateLimiter{
    /** The logger */
    final Logger logger = LoggerFactory.getLogger(APIRateLimiter.class);
    /** A map of client IDs to request counts */
    private final Map<Long, AtomicLong> clientRequestCounts = new ConcurrentHashMap<>();
    /** A map of client IDs to last request times */
    private final Map<Long, Long> clientLastRequestTimes = new ConcurrentHashMap<>();
    /** The time window before resetting the request count, in milliseconds */
    static final long TIME_WINDOW_MILLIS = 60000;

    @Value("${api.request.limit}")
    private int requestLimit;

    /**
     * Checks if a request is allowed for a given client ID
     * @param clientId The client ID to check
     * @return True if the request is allowed, false otherwise
     */
    public boolean allowRequest(Long clientId) {
        long currentTimeMillis = System.currentTimeMillis();

        // Get the request count and last request time for the client
        AtomicLong requestCount = clientRequestCounts.computeIfAbsent(clientId, k -> new AtomicLong(0));
        long lastRequestTime = clientLastRequestTimes.getOrDefault(clientId, 0L);

        logger.info("Request count for client {}:{}", clientId, requestCount.get());
        logger.info("Last request time for client {}:{}", clientId, lastRequestTime);

        if (currentTimeMillis - lastRequestTime > TIME_WINDOW_MILLIS) {
            // Reset request count and last request time for the client if the time window has passed
            logger.info("Resetting request count for client {}", clientId);
            requestCount.set(1);
            clientLastRequestTimes.put(clientId, currentTimeMillis);
        } else {
            // Increment the request count for the client
            logger.info("Incrementing request count for client {}", clientId);
            long updatedCount = requestCount.incrementAndGet();
            logger.info("Request count for client {}:{}", clientId, requestCount.get());

            if (updatedCount > requestLimit) {
                // If the request count exceeds the limit, reject the request
                logger.info("Request limit exceeded for client {}", clientId);
                return false;
            }
        }
        return true;
    }
}


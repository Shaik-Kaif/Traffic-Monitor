package com.backend.Secure.Gateway.API.service;

import com.backend.Secure.Gateway.API.modal.TrafficInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TrafficService {

    private final Set<String> blockedIps = new HashSet<>();
    private final Map<String, RequestCounter> requestCounters = new ConcurrentHashMap<>();
    private final Map<String, RateLimitPolicy> ipPolicies = new ConcurrentHashMap<>();
    private final Map<String, Integer> ipFailedAttempts = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(TrafficService.class);

    public TrafficService() {
        // Initialize blocked IPs
        blockedIps.add("66.249.66.19");
        blockedIps.add("0:0:0:0:0:0:0:61");
        // Initialize custom rate-limit policies
        ipPolicies.put("127.0.0.1", new RateLimitPolicy(100, 60_000));
        ipPolicies.put("66.249.66.196", new RateLimitPolicy(20, 60_000));
        ipPolicies.put("0:0:0:0:0:0:0:1", new RateLimitPolicy(100, 60_000));
    }

    /**
     * Evaluates access and returns result with allowance and reason.
     */
    public AccessResult evaluateAccess(String ipAddress) {
        if (ipAddress == null || ipAddress.equalsIgnoreCase("unknown")) {
            logger.warn("[Blocked] Request from unknown user: {}", ipAddress);
            return new AccessResult(false, "Blocked: Unknown user.");
        }
        if (blockedIps.contains(ipAddress)) {
            logger.warn("[Blocked] IP in blocklist: {}", ipAddress);
            return new AccessResult(false, "Blocked: IP address is blacklisted.");
        }
        if (!checkRateLimit(ipAddress)) {
            logger.warn("[Blocked] Rate limit exceeded for IP: {}", ipAddress);
            return new AccessResult(false, "Blocked: Rate limit exceeded.");
        }
        return new AccessResult(true, "Success can be allowed");
    }

    /**
     * Returns live traffic data for all tracked IPs.
     */
    public List<TrafficInfo> getLiveTrafficData() {
        List<TrafficInfo> data = new ArrayList<>();
        for (var entry : requestCounters.entrySet()) {
            String ip = entry.getKey();
            int count = entry.getValue().count;
            int limit = ipPolicies.getOrDefault(ip, new RateLimitPolicy(5, 60_000)).limit;
            data.add(new TrafficInfo(ip, count, limit));
        }
        return data;
    }

    /**
     * Checks and updates rate limit counters.
     */
    private boolean checkRateLimit(String ipAddress) {
        RateLimitPolicy policy = ipPolicies.getOrDefault(ipAddress, new RateLimitPolicy(5, 60_000));
        long now = System.currentTimeMillis();
        RequestCounter counter = requestCounters.get(ipAddress);

        if (counter == null) {
            requestCounters.put(ipAddress, new RequestCounter(1, now));
            return true;
        }
        if (now - counter.startTime < policy.windowInMillis) {
            if (counter.count >= policy.limit) {
                ipFailedAttempts.merge(ipAddress, 1, Integer::sum);
                return false;
            }
            counter.count++;
            return true;
        }
        // window expired → reset
        counter.startTime = now;
        counter.count = 1;
        return true;
    }

    // Helper classes
    private static class RequestCounter {
        int count;
        long startTime;
        RequestCounter(int count, long startTime) { this.count = count; this.startTime = startTime; }
    }
    private static class RateLimitPolicy {
        int limit;
        long windowInMillis;
        RateLimitPolicy(int limit, long windowInMillis) { this.limit = limit; this.windowInMillis = windowInMillis; }
    }
    public static class AccessResult {
        public final boolean allowed;
        public final String reason;
        public AccessResult(boolean allowed, String reason) { this.allowed = allowed; this.reason = reason; }
    }
}
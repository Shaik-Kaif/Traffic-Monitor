package com.backend.Secure.Gateway.API.filter;

import com.backend.Secure.Gateway.API.service.TrafficService;
import com.backend.Secure.Gateway.API.service.TrafficService.AccessResult;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TrafficFilter implements Filter {
    @Autowired
    private TrafficService trafficService;

    private static final Logger logger = LoggerFactory.getLogger(TrafficFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        String ipAddress = req.getRemoteAddr();

        // Normalize IPv6 localhost
        if ("0:0:0:0:0:0:0:1".equals(ipAddress)) {
            ipAddress = "127.0.0.1";
        }

        // Use evaluateAccess to get allow flag and reason
        AccessResult result = trafficService.evaluateAccess(ipAddress);

        if (!result.allowed) {
            logger.warn("[Blocked] Request from IP {} blocked: {}", ipAddress, result.reason);
            res.setStatus(429); // Too Many Requests
            res.getWriter().write(result.reason);
            return;
        }

        logger.info("[Allowed] Request from IP {}", ipAddress);
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
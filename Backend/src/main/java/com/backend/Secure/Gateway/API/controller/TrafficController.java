package com.backend.Secure.Gateway.API.controller;

import com.backend.Secure.Gateway.API.modal.TrafficInfo;
import com.backend.Secure.Gateway.API.service.TrafficService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:1234")
public class TrafficController {

    @Autowired
    private TrafficService trafficService;

    @GetMapping("/traffic")
    public ResponseEntity<?> getTrafficData(HttpServletRequest request) {
        String clientIp = extractClientIp(request);
        TrafficService.AccessResult result1 = trafficService.evaluateAccess(clientIp);

        if (result1.allowed) {
            List<TrafficInfo> trafficData = trafficService.getLiveTrafficData();
            return ResponseEntity.ok(trafficData);
        } else {
            HttpStatus status = switch (result1.reason) {
                case "Blocked: Unknown user." -> HttpStatus.BAD_REQUEST;
                case "Blocked: IP address is blacklisted." -> HttpStatus.FORBIDDEN;
                case "Blocked: Rate limit exceeded." -> HttpStatus.TOO_MANY_REQUESTS;
                default -> HttpStatus.INTERNAL_SERVER_ERROR;
            };
            return ResponseEntity.status(status).body(result1.reason);
        }
    }


    @GetMapping("/hello")
    public ResponseEntity<String> hello(HttpServletRequest request) {
        String clientIp = extractClientIp(request);
        TrafficService.AccessResult result = trafficService.evaluateAccess(clientIp);

        if (result.allowed) {
            return ResponseEntity.ok("Allowed ✅");
        } else {
            HttpStatus status = switch (result.reason) {
                case "Blocked: Unknown user." -> HttpStatus.BAD_REQUEST; // 400 Bad Request
                case "Blocked: IP address is blacklisted." -> HttpStatus.FORBIDDEN; // 403 Forbidden
                case "Blocked: Rate limit exceeded." -> HttpStatus.TOO_MANY_REQUESTS; // 429 Too Many Requests
                default -> HttpStatus.INTERNAL_SERVER_ERROR; // fallback for unexpected reasons
            };

            return ResponseEntity.status(status).body(result.reason);
        }
    }


    private String extractClientIp(HttpServletRequest request) {
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            return request.getRemoteAddr();
        }
        return clientIp.split(",")[0].trim();
    }
}
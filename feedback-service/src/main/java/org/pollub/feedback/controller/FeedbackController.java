package org.pollub.feedback.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pollub.feedback.model.Feedback;
import org.pollub.feedback.model.FeedbackStatus;
import org.pollub.feedback.model.dto.FeedbackAdminDto;
import org.pollub.feedback.model.dto.FeedbackRequestDto;
import org.pollub.feedback.model.dto.FeedbackResponseDto;
import org.pollub.feedback.service.IFeedbackService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final IFeedbackService feedbackService;

    /**
     * Trusted proxy IP addresses. Only trust X-Forwarded-For from these IPs.
     */
    @Value("${app.trusted-proxies:}")
    private Set<String> trustedProxies;

    /**
     * Submit feedback - accessible to everyone (anonymous or authenticated).
     * Rate limited by IP address.
     */
    @PostMapping
    public ResponseEntity<FeedbackResponseDto> submitFeedback(
            @Valid @RequestBody FeedbackRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest request
    ) {

        // Get client IP (with trusted proxy validation)
        String ipAddress = getClientIp(request);

        Feedback saved = feedbackService.submitFeedback(dto, ipAddress);

        // Add rate limit headers
        int[] rateLimitInfo = feedbackService.getRateLimitInfo(ipAddress);
        int remaining = Math.max(0, rateLimitInfo[1] - rateLimitInfo[0] - 1);
        long resetTime = LocalDateTime.now().plusHours(rateLimitInfo[2]).toEpochSecond(ZoneOffset.UTC);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-RateLimit-Limit", String.valueOf(rateLimitInfo[1]));
        headers.add("X-RateLimit-Remaining", String.valueOf(remaining));
        headers.add("X-RateLimit-Reset", String.valueOf(resetTime));

        return ResponseEntity.ok()
                .headers(headers)
                .body(FeedbackResponseDto.success(saved.getId()));
    }

    /**
     * Get all feedbacks - admin/librarian only.
     * Returns sanitized DTOs without sensitive user data.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<List<FeedbackAdminDto>> getAllFeedbacks(
            @RequestParam(required = false) FeedbackStatus status
    ) {
        List<Feedback> feedbacks = status != null
                ? feedbackService.getFeedbacksByStatus(status)
                : feedbackService.getAllFeedbacks();

        // Convert to admin DTOs to hide sensitive data
        List<FeedbackAdminDto> dtos = feedbacks.stream()
                .map(FeedbackAdminDto::fromEntity)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    /**
     * Update feedback status - admin/librarian only.
     * Returns sanitized DTO.
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<FeedbackAdminDto> updateStatus(
            @PathVariable Long id,
            @RequestParam FeedbackStatus status
    ) {
        Feedback updated = feedbackService.updateStatus(id, status);
        return ResponseEntity.ok(FeedbackAdminDto.fromEntity(updated));
    }

    /**
     * Extract client IP address with trusted proxy validation.
     * Only trusts X-Forwarded-For header if request comes from a trusted proxy.
     */
    private String getClientIp(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();

        // Only check proxy headers if request comes from trusted proxy
        if (trustedProxies != null && !trustedProxies.isEmpty() && trustedProxies.contains(remoteAddr)) {
            String xForwardedFor = request.getHeader("X-Forwarded-For");
            if (xForwardedFor != null && !xForwardedFor.isBlank()) {
                // Take the first IP in the chain (original client)
                String clientIp = xForwardedFor.split(",")[0].trim();
                if (isValidIp(clientIp)) {
                    return clientIp;
                }
            }

            String xRealIp = request.getHeader("X-Real-IP");
            if (xRealIp != null && !xRealIp.isBlank() && isValidIp(xRealIp.trim())) {
                return xRealIp.trim();
            }
        }

        return remoteAddr;
    }

    /**
     * Basic IP address validation.
     */
    private boolean isValidIp(String ip) {
        if (ip == null || ip.isBlank()) return false;
        // IPv4 pattern
        if (ip.matches("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$")) return true;
        // IPv6 pattern (simplified)
        if (ip.contains(":") && ip.length() <= 45) return true;
        return false;
    }
}

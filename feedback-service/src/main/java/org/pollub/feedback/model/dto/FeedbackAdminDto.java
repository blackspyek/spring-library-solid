package org.pollub.feedback.model.dto;



import org.pollub.feedback.model.Feedback;
import org.pollub.feedback.model.FeedbackCategory;
import org.pollub.feedback.model.FeedbackStatus;

import java.time.LocalDateTime;

/**
 * Admin DTO for viewing feedbacks without exposing sensitive data.
 * Excludes: full User entity, raw IP address.
 */
public record FeedbackAdminDto(
        Long id,
        FeedbackCategory category,
        String message,
        String pageUrl,
        FeedbackStatus status,
        LocalDateTime createdAt,
        LocalDateTime resolvedAt,
        String maskedIp
) {
    /**
     * Convert entity to admin DTO with sensitive data masked.
     */
    public static FeedbackAdminDto fromEntity(Feedback feedback) {
        String submitterName = null;
        boolean anonymous = true;
        
        return new FeedbackAdminDto(
                feedback.getId(),
                feedback.getCategory(),
                feedback.getMessage(),
                feedback.getPageUrl(),
                feedback.getStatus(),
                feedback.getCreatedAt(),
                feedback.getResolvedAt(),
                maskIp(feedback.getIpAddress())
        );
    }
    
    /**
     * Mask IP address for privacy (show first 3 octets only).
     */
    private static String maskIp(String ip) {
        if (ip == null || ip.isBlank()) return "unknown";
        int lastDot = ip.lastIndexOf('.');
        if (lastDot > 0) {
            return ip.substring(0, lastDot) + ".***";
        }
        // IPv6 or other format - just mask last segment
        int lastColon = ip.lastIndexOf(':');
        if (lastColon > 0) {
            return ip.substring(0, lastColon) + ":****";
        }
        return "***";
    }
}

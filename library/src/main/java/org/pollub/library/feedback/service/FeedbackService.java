package org.pollub.library.feedback.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pollub.library.exception.RateLimitExceededException;
import org.pollub.library.exception.ResourceNotFoundException;
import org.pollub.library.feedback.model.Feedback;
import org.pollub.library.feedback.model.FeedbackStatus;
import org.pollub.library.feedback.model.dto.FeedbackRequestDto;
import org.pollub.library.feedback.repository.FeedbackRepository;
import org.pollub.library.user.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedbackService implements IFeedbackService {
    
    private final FeedbackRepository feedbackRepository;
    
    /**
     * Maximum number of feedback submissions per IP within the rate limit window.
     */
    @Value("${feedback.rate-limit.max-requests:3}")
    private int maxRequestsPerWindow;
    
    /**
     * Rate limit window in hours.
     */
    @Value("${feedback.rate-limit.window-hours:1}")
    private int windowHours;
    
    @Override
    @Transactional
    public Feedback submitFeedback(FeedbackRequestDto dto, User user, String ipAddress) {
        // Check rate limit
        if (isRateLimitExceeded(ipAddress)) {
            log.warn("Rate limit exceeded for IP: {}", maskIp(ipAddress));
            throw new RateLimitExceededException();
        }
        
        Feedback feedback = Feedback.builder()
                .category(dto.category())
                .message(sanitizeMessage(dto.message()))
                .pageUrl(dto.pageUrl())
                .submittedBy(user)
                .ipAddress(ipAddress)
                .createdAt(LocalDateTime.now())
                .status(FeedbackStatus.NEW)
                .build();
        
        Feedback saved = feedbackRepository.save(feedback);
        log.info("Feedback submitted: id={}, category={}, anonymous={}", 
                saved.getId(), saved.getCategory(), user == null);
        
        return saved;
    }
    
    @Override
    public boolean isRateLimitExceeded(String ipAddress) {
        if (ipAddress == null || ipAddress.isBlank()) {
            log.warn("Rate limit check failed: IP address unknown, blocking request");
            return true; // Block if IP cannot be determined (security measure)
        }
        
        LocalDateTime windowStart = LocalDateTime.now().minusHours(windowHours);
        long count = feedbackRepository.countByIpAddressSince(ipAddress, windowStart);
        
        return count >= maxRequestsPerWindow;
    }
    
    @Override
    public List<Feedback> getAllFeedbacks() {
        return feedbackRepository.findAllByOrderByCreatedAtDesc();
    }
    
    @Override
    public List<Feedback> getFeedbacksByStatus(FeedbackStatus status) {
        return feedbackRepository.findByStatusOrderByCreatedAtDesc(status);
    }
    
    @Override
    @Transactional
    public Feedback updateStatus(Long feedbackId, FeedbackStatus newStatus) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found: " + feedbackId));
        
        feedback.setStatus(newStatus);
        
        if (newStatus == FeedbackStatus.RESOLVED || newStatus == FeedbackStatus.DISMISSED) {
            feedback.setResolvedAt(LocalDateTime.now());
        }
        
        return feedbackRepository.save(feedback);
    }
    
    @Override
    public int[] getRateLimitInfo(String ipAddress) {
        int currentCount = 0;
        if (ipAddress != null && !ipAddress.isBlank()) {
            LocalDateTime windowStart = LocalDateTime.now().minusHours(windowHours);
            currentCount = (int) feedbackRepository.countByIpAddressSince(ipAddress, windowStart);
        }
        return new int[] { currentCount, maxRequestsPerWindow, windowHours };
    }
    
    /**
     * Sanitize message content to prevent XSS.
     * Removes ALL HTML tags to prevent script injection via any vector
     * (script tags, event handlers, data URIs, SVG payloads, etc.)
     */
    private String sanitizeMessage(String message) {
        if (message == null) return null;
        return message
                .replaceAll("<[^>]*>", "") // Remove all HTML tags
                .replaceAll("(?i)javascript:", "") // Remove javascript: URLs
                .replaceAll("(?i)data:", "") // Remove data: URLs
                .replaceAll("(?i)vbscript:", "") // Remove vbscript: URLs
                .trim();
    }
    
    /**
     * Mask IP address for logging (privacy).
     */
    private String maskIp(String ip) {
        if (ip == null) return "unknown";
        int lastDot = ip.lastIndexOf('.');
        if (lastDot > 0) {
            return ip.substring(0, lastDot) + ".***";
        }
        return "***";
    }
}

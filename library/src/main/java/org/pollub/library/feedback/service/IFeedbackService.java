package org.pollub.library.feedback.service;

import org.pollub.library.feedback.model.Feedback;
import org.pollub.library.feedback.model.FeedbackStatus;
import org.pollub.library.feedback.model.dto.FeedbackRequestDto;
import org.pollub.library.user.model.User;

import java.util.List;

public interface IFeedbackService {
    
    /**
     * Submit new feedback.
     * @param dto Feedback data
     * @param user Optional authenticated user (null for anonymous)
     * @param ipAddress Client IP address for rate limiting
     * @return Saved feedback entity
     * @throws org.pollub.library.exception.RateLimitExceededException if rate limit exceeded
     */
    Feedback submitFeedback(FeedbackRequestDto dto, User user, String ipAddress);
    
    /**
     * Check if the IP has exceeded the rate limit.
     * @param ipAddress Client IP address
     * @return true if rate limit exceeded
     */
    boolean isRateLimitExceeded(String ipAddress);
    
    /**
     * Get all feedbacks for admin/librarian review.
     */
    List<Feedback> getAllFeedbacks();
    
    /**
     * Get feedbacks filtered by status.
     */
    List<Feedback> getFeedbacksByStatus(FeedbackStatus status);
    
    /**
     * Update feedback status.
     */
    Feedback updateStatus(Long feedbackId, FeedbackStatus newStatus);
    
    /**
     * Get rate limit information for an IP address.
     * @return array with [currentCount, maxRequests, windowHours]
     */
    int[] getRateLimitInfo(String ipAddress);
}

package org.pollub.feedback.service;


import org.pollub.feedback.model.Feedback;
import org.pollub.feedback.model.FeedbackStatus;
import org.pollub.feedback.model.dto.FeedbackRequestDto;

import java.util.List;

public interface IFeedbackService {

    /**
     * Submit new feedback.
     * @param dto Feedback data
     * @param ipAddress Client IP address for rate limiting
     * @return Saved feedback entity
     * @throws org.pollub.feedback.exception.RateLimitExceededException if rate limit exceeded
     */
    Feedback submitFeedback(FeedbackRequestDto dto, String ipAddress);

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

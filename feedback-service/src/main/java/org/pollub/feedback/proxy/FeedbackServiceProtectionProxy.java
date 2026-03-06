package org.pollub.feedback.proxy;

import lombok.extern.slf4j.Slf4j;
import org.pollub.feedback.exception.IpAddressBannedException;
import org.pollub.feedback.exception.RateLimitExceededException;
import org.pollub.feedback.model.Feedback;
import org.pollub.feedback.model.FeedbackStatus;
import org.pollub.feedback.model.dto.FeedbackRequestDto;
import org.pollub.feedback.service.IFeedbackService;
import org.pollub.feedback.utils.IpBlacklist;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Protection Proxy (wzorzec Proxy) dla IFeedbackService.
 * Dodaje warstwę kontroli dostępu (rate limiting, IP blacklist)
 * przed delegowaniem do prawdziwego FeedbackService.
 */
//Lab1 - Proxy 2 Start
@Component
@Primary
@Slf4j
public class FeedbackServiceProtectionProxy implements IFeedbackService {

    private final IFeedbackService feedbackService;

    public FeedbackServiceProtectionProxy(@Qualifier("feedbackService") IFeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @Override
    public Feedback submitFeedback(FeedbackRequestDto dto, String ipAddress) {
        if (isRateLimitExceeded(ipAddress)) {
            log.warn("Rate limit exceeded for IP: {}", maskIp(ipAddress));
            throw new RateLimitExceededException();
        }

        if (IpBlacklist.getInstance().isBanned(ipAddress)) {
            log.warn("Blocked banned IP address: {}", maskIp(ipAddress));
            throw new IpAddressBannedException(ipAddress);
        }

        return feedbackService.submitFeedback(dto, ipAddress);
    }

    @Override
    public boolean isRateLimitExceeded(String ipAddress) {
        return feedbackService.isRateLimitExceeded(ipAddress);
    }

    @Override
    public List<Feedback> getAllFeedbacks() {
        return feedbackService.getAllFeedbacks();
    }

    @Override
    public List<Feedback> getFeedbacksByStatus(FeedbackStatus status) {
        return feedbackService.getFeedbacksByStatus(status);
    }

    @Override
    public Feedback updateStatus(Long feedbackId, FeedbackStatus newStatus) {
        return feedbackService.updateStatus(feedbackId, newStatus);
    }

    @Override
    public int[] getRateLimitInfo(String ipAddress) {
        return feedbackService.getRateLimitInfo(ipAddress);
    }

    private String maskIp(String ip) {
        if (ip == null) return "unknown";
        int lastDot = ip.lastIndexOf('.');
        if (lastDot > 0) {
            return ip.substring(0, lastDot) + ".***";
        }
        return "***";
    }
}
//Lab1 - Proxy 2 End

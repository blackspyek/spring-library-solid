package org.pollub.library.feedback.model.dto;

/**
 * Response DTO for feedback submission.
 */
public record FeedbackResponseDto(
        boolean success,
        String message,
        String ticketId
) {
    public static FeedbackResponseDto success(Long feedbackId) {
        return new FeedbackResponseDto(
                true,
                "Dziękujemy za zgłoszenie! Twoja uwaga została przyjęta.",
                "FB-" + feedbackId
        );
    }
    
    public static FeedbackResponseDto error(String message) {
        return new FeedbackResponseDto(false, message, null);
    }
}

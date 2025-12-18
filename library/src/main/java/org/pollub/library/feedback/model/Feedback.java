package org.pollub.library.feedback.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.pollub.library.user.model.User;

import java.time.LocalDateTime;

/**
 * Entity representing a user feedback submission.
 * Can be submitted anonymously or by authenticated users.
 */
@Entity
@Table(name = "feedbacks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeedbackCategory category;
    
    @Column(length = 2000, nullable = false)
    private String message;
    
    @Column(length = 500)
    private String pageUrl;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private FeedbackStatus status = FeedbackStatus.NEW;
    
    /**
     * Optional: user who submitted the feedback (null for anonymous submissions)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by_id")
    private User submittedBy;
    
    /**
     * IP address of the submitter (for rate limiting and auditing)
     */
    @Column(length = 45) // IPv6 max length
    private String ipAddress;
    
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime resolvedAt;
}

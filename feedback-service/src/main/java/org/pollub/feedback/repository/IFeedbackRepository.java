package org.pollub.feedback.repository;

import org.pollub.feedback.model.Feedback;
import org.pollub.feedback.model.FeedbackStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IFeedbackRepository extends JpaRepository<Feedback, Long> {

    /**
     * Find all feedbacks by status, ordered by creation date descending.
     */
    List<Feedback> findByStatusOrderByCreatedAtDesc(FeedbackStatus status);

    /**
     * Find all feedbacks ordered by creation date descending.
     */
    List<Feedback> findAllByOrderByCreatedAtDesc();

    /**
     * Count submissions from a specific IP within a time window.
     * Used for rate limiting.
     */
    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.ipAddress = :ip AND f.createdAt > :since")
    long countByIpAddressSince(@Param("ip") String ipAddress, @Param("since") LocalDateTime since);
}

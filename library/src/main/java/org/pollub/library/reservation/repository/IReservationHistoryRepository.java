package org.pollub.library.reservation.repository;

import org.pollub.library.reservation.model.ReservationHistory;
import org.pollub.library.reservation.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IReservationHistoryRepository extends JpaRepository<ReservationHistory, Long> {
    
    Optional<ReservationHistory> findByItemIdAndStatus(Long itemId, ReservationStatus status);
    
    List<ReservationHistory> findByUserIdAndStatus(Long userId, ReservationStatus status);
    
    long countByUserIdAndStatus(Long userId, ReservationStatus status);
    
    List<ReservationHistory> findByExpiresAtBeforeAndStatus(LocalDateTime dateTime, ReservationStatus status);
    
    @Modifying
    @Query("UPDATE ReservationHistory r SET r.status = :newStatus, r.resolvedAt = :resolvedAt " +
           "WHERE r.expiresAt < :expiryTime AND r.status = :currentStatus")
    int updateExpiredReservations(
            @Param("newStatus") ReservationStatus newStatus,
            @Param("resolvedAt") LocalDateTime resolvedAt,
            @Param("expiryTime") LocalDateTime expiryTime,
            @Param("currentStatus") ReservationStatus currentStatus
    );
}

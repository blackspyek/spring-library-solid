package org.pollub.reservation.repository;

import org.pollub.reservation.model.ReservationHistory;
import org.pollub.reservation.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationHistory, Long> {
    
    List<ReservationHistory> findByUserId(Long userId);
    
    List<ReservationHistory> findByItemId(Long itemId);
    
    List<ReservationHistory> findByStatus(ReservationStatus status);
    
    List<ReservationHistory> findByUserIdAndStatus(Long userId, ReservationStatus status);
    
    Optional<ReservationHistory> findByItemIdAndUserId(Long itemId, Long userId);
    
    Optional<ReservationHistory> findByItemIdAndStatus(Long itemId, ReservationStatus status);
    
    List<ReservationHistory> findByExpiresAtBeforeAndStatus(LocalDateTime expiresBefore, ReservationStatus status);
    
    long countByUserIdAndStatus(Long userId, ReservationStatus status);
    
    List<ReservationHistory> findByUserIdAndStatusOrderByReservedAtDesc(Long userId, ReservationStatus status);

    Optional<ReservationHistory> findByItemIdAndBranchIdAndUserIdAndStatus(Long itemId, Long branchId, Long userId, ReservationStatus status);

}

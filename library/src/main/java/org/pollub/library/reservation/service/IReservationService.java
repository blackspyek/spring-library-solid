package org.pollub.library.reservation.service;

import org.pollub.library.item.model.LibraryItem;
import org.pollub.library.reservation.model.ReservationHistory;
import org.pollub.library.reservation.model.dto.ReservationDto;

import java.util.List;

public interface IReservationService {
    
    ReservationHistory createReservation(ReservationDto dto, Long userId);
    
    LibraryItem cancelReservation(Long itemId, Long userId);
    
    List<ReservationHistory> getUserReservations(Long userId);
    
    long getUserReservationCount(Long userId);
    
    void resolveReservation(Long itemId);
    
    void cleanupExpiredReservations();
}

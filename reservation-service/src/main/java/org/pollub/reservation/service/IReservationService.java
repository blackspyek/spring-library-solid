package org.pollub.reservation.service;

import org.pollub.common.dto.ItemDto;
import org.pollub.common.dto.ReservationItemDto;
import org.pollub.reservation.model.dto.ReservationDto;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface IReservationService {
    ItemDto createReservation(ReservationDto dto, Long userId);
    void cleanupExpiredReservations();

    List<ReservationItemDto> getReservationsByUsername(Long userId);

    void cancelReservation(Long id, Long userId) throws AccessDeniedException;
}

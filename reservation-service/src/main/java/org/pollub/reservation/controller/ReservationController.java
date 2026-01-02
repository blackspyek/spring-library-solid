package org.pollub.reservation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pollub.common.dto.ItemDto;
import org.pollub.common.dto.ReservationItemDto;
import org.pollub.common.security.JwtUserDetails;
import org.pollub.reservation.model.dto.ReservationDto;
import org.pollub.reservation.service.IReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {
    
    private final IReservationService reservationService;

    @GetMapping("/my")
    public ResponseEntity<List<ReservationItemDto>> getMyReservations(
            @AuthenticationPrincipal JwtUserDetails user
    ) {
        List<ReservationItemDto> reservations = reservationService.getReservationsByUsername(user.getUserId());
        return ResponseEntity.ok(reservations);
    }

    @PostMapping()
    public ResponseEntity<ItemDto> createReservation(
            @Valid @RequestBody ReservationDto dto,
            @AuthenticationPrincipal JwtUserDetails user
    ) {
        ItemDto reservation = reservationService.createReservation(dto, user.getUserId());
        return ResponseEntity.ok(reservation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelReservation(
            @PathVariable Long id,
            @AuthenticationPrincipal JwtUserDetails user
    ) throws AccessDeniedException {
        reservationService.cancelReservation(id, user.getUserId());
        return ResponseEntity.noContent().build();
    }


}




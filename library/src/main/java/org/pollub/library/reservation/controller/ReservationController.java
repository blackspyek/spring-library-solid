package org.pollub.library.reservation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pollub.library.item.model.LibraryItem;
import org.pollub.library.reservation.model.ReservationHistory;
import org.pollub.library.reservation.model.dto.ReservationDto;
import org.pollub.library.reservation.service.IReservationService;
import org.pollub.library.user.model.User;
import org.pollub.library.user.service.IUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {
    
    private final IReservationService reservationService;
    private final IUserService userService;
    
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReservationHistory> createReservation(
            @Valid @RequestBody ReservationDto dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userService.findByUsername(userDetails.getUsername());
        ReservationHistory reservation = reservationService.createReservation(dto, user.getId());
        return ResponseEntity.ok(reservation);
    }
    
    @DeleteMapping("/{itemId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LibraryItem> cancelReservation(
            @PathVariable Long itemId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userService.findByUsername(userDetails.getUsername());
        LibraryItem item = reservationService.cancelReservation(itemId, user.getId());
        return ResponseEntity.ok(item);
    }
    
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ReservationHistory>> getMyReservations(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userService.findByUsername(userDetails.getUsername());
        List<ReservationHistory> reservations = reservationService.getUserReservations(user.getId());
        return ResponseEntity.ok(reservations);
    }
    
    @GetMapping("/count")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Long> getReservationCount(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userService.findByUsername(userDetails.getUsername());
        long count = reservationService.getUserReservationCount(user.getId());
        return ResponseEntity.ok(count);
    }
}

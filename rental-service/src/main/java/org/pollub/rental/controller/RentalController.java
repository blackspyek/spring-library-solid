package org.pollub.rental.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pollub.common.dto.ItemDto;
import org.pollub.common.dto.ReservationResponse;
import org.pollub.rental.model.RentalHistory;
import org.pollub.rental.model.dto.RentDto;
import org.pollub.rental.service.IRentalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
public class RentalController {
    
    private final IRentalService rentalService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ItemDto>> getUserActiveRentals(@PathVariable Long userId) {
        List<ItemDto> rentals = rentalService.getActiveRentals(userId);
        return ResponseEntity.ok(rentals);
    }
    
    @GetMapping("/user/{userId}/history")
    public ResponseEntity<List<RentalHistory>> getUserRentalHistory(@PathVariable Long userId) {
        List<RentalHistory> history = rentalService.getUserRentalHistory(userId);
        return ResponseEntity.ok(history);
    }
    
    @GetMapping("/item/{itemId}/history")
    public ResponseEntity<List<RentalHistory>> getItemRentalHistory(@PathVariable Long itemId) {
        List<RentalHistory> history = rentalService.getItemRentalHistory(itemId);
        return ResponseEntity.ok(history);
    }

    @PutMapping("/rent")
    public ResponseEntity<ReservationResponse> rentItem(@RequestBody @Valid RentDto rentDto) {
        ReservationResponse rentedBook = rentalService.rentItem(
                rentDto.getLibraryItemId(),
                rentDto.getUserId(),
                rentDto.getBranchId()
        );
        return ResponseEntity.ok(rentedBook);
    }

    @PutMapping("/{itemId}/return")
    public ResponseEntity<Void> returnItem(
            @PathVariable Long itemId,
            @RequestParam Long branchId
    ) {
        rentalService.returnItem(itemId, branchId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{itemId}/extend")
    public ResponseEntity<Void> extendLoan(
            @PathVariable Long itemId,
            @RequestParam Long branchId,
            @RequestParam(defaultValue = "7") int days
    ) {
        rentalService.extendLoan(itemId, branchId, days);
        return ResponseEntity.noContent().build();
    }



}

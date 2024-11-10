package org.pollub.library.rental.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pollub.library.item.model.LibraryItem;
import org.pollub.library.rental.model.dto.RentDto;
import org.pollub.library.rental.service.IRentalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
public class RentalController {
    private final IRentalService rentalService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LibraryItem>> getUserRentals(@PathVariable Long userId) {
        return ResponseEntity.ok(rentalService.getRentedItems(userId));
    }

    @GetMapping("/available")
    public ResponseEntity<List<LibraryItem>> getAvailableItems() {
        return ResponseEntity.ok(rentalService.getAvailableItems());
    }

    @PostMapping("/rent")
    public ResponseEntity<LibraryItem> rentItem(@Valid @RequestBody RentDto rentDto) {
        return ResponseEntity.ok(rentalService.rentItem(rentDto));
    }

    @PostMapping("/return/{itemId}")
    public ResponseEntity<LibraryItem> returnItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(rentalService.returnItem(itemId));
    }


}

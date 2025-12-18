package org.pollub.library.rental.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pollub.library.item.model.ItemStatus;
import org.pollub.library.item.model.LibraryItem;
import org.pollub.library.rental.model.dto.RentDto;
import org.pollub.library.rental.model.dto.RentalHistoryDto;
import org.pollub.library.rental.service.IRentalService;
import org.pollub.library.rental.service.RentalHistoryExportService;
import org.pollub.library.user.model.User;
import org.pollub.library.user.service.IUserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
public class RentalController {
    private final IRentalService rentalService;
    private final RentalHistoryExportService rentalHistoryExportService;
    private final IUserService userService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LibraryItem>> getUserRentals(@PathVariable Long userId) {
        return ResponseEntity.ok(rentalService.getRentedItems(userId));
    }

    @GetMapping("/available")
    public ResponseEntity<List<LibraryItem>> getAvailableItems() {
        return ResponseEntity.ok(rentalService.getAvailableItems());
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @PostMapping("/rent")
    public ResponseEntity<LibraryItem> rentItem(@Valid @RequestBody RentDto rentDto) {
        return ResponseEntity.ok(rentalService.rentItem(rentDto));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @PostMapping("/return/{itemId}")
    public ResponseEntity<LibraryItem> returnItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(rentalService.returnItem(itemId));
    }

    @GetMapping("/history/recent")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RentalHistoryDto>> getRecentHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "3") int limit) {
        User user = userService.findByUsername(userDetails.getUsername());
        List<RentalHistoryDto> history = rentalHistoryExportService.getRecentHistory(user.getId(), limit);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/history/export")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> exportRentalHistory(@AuthenticationPrincipal UserDetails userDetails) throws IOException {
        User user = userService.findByUsername(userDetails.getUsername());
        byte[] xlsxContent = rentalHistoryExportService.exportToXlsx(user.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "historia-wypozyczen.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(xlsxContent);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @PostMapping("/{itemId}/extend")
    public ResponseEntity<LibraryItem> extendLoan(
            @PathVariable Long itemId,
            @RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(rentalService.extendLoan(itemId, days));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @GetMapping("/all-rented")
    public ResponseEntity<List<LibraryItem>> getAllRentedItems() {
        return ResponseEntity.ok(rentalService.getAllRentedItems());
    }
}

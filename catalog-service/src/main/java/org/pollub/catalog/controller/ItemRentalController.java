package org.pollub.catalog.controller;

import lombok.RequiredArgsConstructor;
import org.pollub.catalog.service.IBranchInventoryService;
import org.pollub.catalog.service.ICatalogService;
import org.pollub.common.dto.RentalHistoryDto;
import org.pollub.common.dto.ReservationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemRentalController {
    private final ICatalogService catalogService;
    private final IBranchInventoryService branchInventoryService;

    @PutMapping("/{itemId}/rent")
    public ResponseEntity<ReservationResponse> markAsRented(
            @PathVariable Long itemId,
            @RequestBody RentalHistoryDto rentalHistoryDto
            ) {
        ReservationResponse response = branchInventoryService.rentCopy(
                itemId, rentalHistoryDto
        );
        return ResponseEntity.ok(response);

    }

    @PutMapping("/{id}/return")
    public ResponseEntity<Void> returnItem(
            @PathVariable Long id,
            @RequestParam Long branchId
    ) {
        branchInventoryService.returnCopy(id, branchId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/extend")
    public ResponseEntity<Void> extendRental(
            @PathVariable Long id,
            @RequestParam Long branchId,
            @RequestParam int days
    ) {
        branchInventoryService.extendRental(id, branchId, days);
        return ResponseEntity.noContent().build(); // 204
    }


}

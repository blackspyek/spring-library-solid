package org.pollub.catalog.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pollub.catalog.model.dto.BranchInventoryDto;
import org.pollub.catalog.model.dto.ReservationCatalogRequestDto;
import org.pollub.catalog.model.dto.UpdateInventoryStatusRequest;
import org.pollub.catalog.service.IBranchInventoryService;
import org.pollub.catalog.service.ICatalogService;
import org.pollub.common.dto.ReservationItemDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemReservationController {

    private final ICatalogService catalogService;
    private final IBranchInventoryService branchInventoryService;

    @PutMapping("/{itemId}/reserve")
    public ResponseEntity<BranchInventoryDto> markAsReserved(
            @PathVariable Long itemId,
            @RequestBody ReservationCatalogRequestDto reservationCatalogRequestDto
    ) {
        BranchInventoryDto branchInventory = branchInventoryService.reserveCopy(
                itemId,
                reservationCatalogRequestDto
        );
        return ResponseEntity.ok(branchInventory);
    }

    // POST for getting because of possible large list of IDs
    @PostMapping("/info/batch")
    public ResponseEntity<List<ReservationItemDto.Item>> getItemsForReservation(
            @RequestBody List<Long> itemIds
    ) {
        List<ReservationItemDto.Item> items = catalogService.getItemsForReservation(itemIds);
        return ResponseEntity.ok(items);
    }

    @PutMapping("/{itemId}/inventory/{branchId}/status")
    public ResponseEntity<Void> updateInventoryStatus(
            @PathVariable Long itemId,
            @PathVariable Long branchId,
            @Valid @RequestBody UpdateInventoryStatusRequest request
    ) {
        String statusStr = request.getStatus();

        branchInventoryService.updateStatus(itemId, branchId, statusStr);
        return ResponseEntity.noContent().build();
    }




}
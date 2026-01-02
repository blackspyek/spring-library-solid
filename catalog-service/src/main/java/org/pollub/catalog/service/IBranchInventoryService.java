package org.pollub.catalog.service;

import org.pollub.catalog.model.BranchInventory;
import org.pollub.catalog.model.dto.BranchInventoryDto;
import org.pollub.catalog.model.dto.ReservationCatalogRequestDto;
import org.pollub.common.dto.RentalHistoryDto;
import org.pollub.common.dto.ReservationResponse;

import java.util.List;

/**
 * Service interface for branch inventory operations.
 */
public interface IBranchInventoryService {

    /**
     * Rent a copy of an item at a specific branch.
     */
    ReservationResponse rentCopy(Long itemId, RentalHistoryDto rentalHistoryDto);

    /**
     * Return a rented copy to a specific branch.
     */
    void returnCopy(Long itemId, Long branchId);

    /**
     * Reserve a copy of an item at a specific branch.
     */
    BranchInventoryDto reserveCopy(Long itemId, ReservationCatalogRequestDto reservationCatalogRequestDto);

    /**
     * Cancel a reservation.
     */
    BranchInventory cancelReservation(Long itemId, Long branchId);

    /**
     * Extend a rental.
     */
    void extendRental(Long itemId, Long branchId, int additionalDays);

    /**
     * Get all inventory records for an item.
     */
    List<BranchInventoryDto> getInventoryForItem(Long itemId);

    /**
     * Get available copies of an item.
     */
    List<BranchInventory> getAvailableCopies(Long itemId);

    /**
     * Get available item IDs at a branch.
     */
    List<Long> getAvailableItemsAtBranch(Long branchId);

    /**
     * Check if item is available at a specific branch.
     */
    boolean isAvailableAtBranch(Long itemId, Long branchId);

    /**
     * Get all branches where an item is available.
     */
    List<Long> getAvailableBranchIds(Long itemId);

    /**
     * Get items rented by a user.
     */
    List<BranchInventory> getRentedByUser(Long userId);

    /**
     * Add a new inventory record (for admin use).
     */
    BranchInventory addInventory(Long itemId, Long branchId);

    void updateStatus(Long itemId, Long branchId, String statusStr);

}

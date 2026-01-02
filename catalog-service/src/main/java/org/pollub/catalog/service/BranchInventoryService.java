package org.pollub.catalog.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pollub.catalog.model.BranchInventory;
import org.pollub.catalog.model.CopyStatus;
import org.pollub.catalog.model.dto.BranchInventoryDto;
import org.pollub.catalog.model.dto.ReservationCatalogRequestDto;
import org.pollub.catalog.repository.IBranchInventoryRepository;
import org.pollub.common.dto.RentalHistoryDto;
import org.pollub.common.dto.ReservationResponse;
import org.pollub.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing per-branch inventory of library items.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BranchInventoryService implements IBranchInventoryService {

    private final IBranchInventoryRepository inventoryRepository;

    @Override
    public ReservationResponse rentCopy(Long itemId, RentalHistoryDto rentalHistoryDto) {
        Long branchId = rentalHistoryDto.getBranchId();
        Long userId = rentalHistoryDto.getUserId();

        BranchInventory inventory = getBranchInventoryOrThrow(itemId, branchId);

        validateReservationOwnership(inventory, userId);
        validateAvailabilityForRent(inventory);

        updateInventoryRecordWithRentData(inventory, CopyStatus.RENTED, userId, rentalHistoryDto.getRentedAt(), rentalHistoryDto.getDueDate());

        clearReservationInfo(inventory);

        try{
            inventoryRepository.save(inventory);
            return ReservationResponse.builder()
                    .itemId(itemId)
                    .branchId(branchId)
                    .userId(userId)
                    .rentedAt(rentalHistoryDto.getRentedAt())
                    .dueDate(rentalHistoryDto.getDueDate())
                    .status(CopyStatus.RENTED.name())
                    .build();
        } catch (Exception e) {
            log.error("Error renting copy of item {} at branch {}: {}", itemId, branchId, e.getMessage());
            throw e;
        }
    }

    private static void clearReservationInfo(BranchInventory inventory) {
        inventory.setReservedByUserId(null);
        inventory.setReservedAt(null);
        inventory.setReservationExpiresAt(null);
    }

    private static void updateInventoryRecordWithRentData(BranchInventory inventory, CopyStatus rented, Long userId, LocalDateTime rentedAt, LocalDateTime dueDate) {
        inventory.setStatus(rented);
        inventory.setRentedByUserId(userId);
        inventory.setRentedAt(rentedAt);
        inventory.setDueDate(dueDate);
        inventory.setRentExtended(false);
    }

    private BranchInventory getBranchInventoryOrThrow(Long itemId, Long branchId) {
        BranchInventory inventory = inventoryRepository.findByItemIdAndBranchId(itemId, branchId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No copy of item " + itemId + " found at branch " + branchId));
        return inventory;
    }

    private void validateReservationOwnership(BranchInventory inventory, Long userId) {
        if (inventory.getStatus() == CopyStatus.RESERVED &&
                !userId.equals(inventory.getReservedByUserId())) {

            throw new IllegalStateException(
                    "Copy is reserved by another user. Reserved by userId: "
                            + inventory.getReservedByUserId()
            );
        }
    }

    private void validateAvailabilityForRent(BranchInventory inventory) {
        if (inventory.getStatus() != CopyStatus.AVAILABLE &&
                inventory.getStatus() != CopyStatus.RESERVED) {

            throw new IllegalStateException(
                    "Copy is not available for rent. Current status: "
                            + inventory.getStatus()
            );
        }
    }





    @Override
    public void returnCopy(Long itemId, Long branchId) {
        log.info("Returning item {} to branch {}", itemId, branchId);

        BranchInventory inventory = getBranchInventoryOrThrow(itemId, branchId);

        if (inventory.getStatus() != CopyStatus.RENTED) {
            throw new IllegalStateException("Copy is not rented. Current status: " + inventory.getStatus());
        }

        updateInventoryRecordWithRentData(inventory, CopyStatus.AVAILABLE, null, null, null);

        try {
            inventoryRepository.save(inventory);
        } catch (Exception e) {
            log.error("Error returning copy of item {} at branch {}: {}", itemId, branchId, e.getMessage());
            throw e;
        }
    }


    @Override
    public BranchInventoryDto reserveCopy(Long itemId, ReservationCatalogRequestDto reservationCatalogRequestDto) {
        Long branchId = reservationCatalogRequestDto.getBranchId();

        BranchInventory inventory = getBranchInventoryOrThrow(itemId, branchId);

        if (inventory.getStatus() != CopyStatus.AVAILABLE) {
            throw new IllegalStateException(
                    "Copy is not available for reservation. Current status: " + inventory.getStatus());
        }

        inventory.setStatus(CopyStatus.RESERVED);
        inventory.setReservedByUserId(
                reservationCatalogRequestDto.getUserId()
        );
        inventory.setReservedAt(LocalDateTime.now());
        inventory.setReservationExpiresAt(
                reservationCatalogRequestDto.getExpiresAt()
        );

        try {
            BranchInventory savedInventory = inventoryRepository.save(inventory);
            return toDto(savedInventory);
        } catch (Exception e) {
            log.error("Error reserving copy of item {} at branch {}: {}", itemId, branchId, e.getMessage());
            throw e;
        }
    }

    @Override
    public BranchInventory cancelReservation(Long itemId, Long branchId) {
        log.info("Cancelling reservation for item {} at branch {}", itemId, branchId);

        BranchInventory inventory = getBranchInventoryOrThrow(itemId, branchId);

        if (inventory.getStatus() != CopyStatus.RESERVED) {
            throw new IllegalStateException("Copy is not reserved. Current status: " + inventory.getStatus());
        }

        inventory.setStatus(CopyStatus.AVAILABLE);
        clearReservationInfo(inventory);

        return inventoryRepository.save(inventory);
    }

    @Override
    @Transactional
    public void extendRental(Long itemId, Long branchId, int additionalDays) {
        log.info("Extending rental for item {} at branch {} by {} days", itemId, branchId, additionalDays);

        BranchInventory inventory = getBranchInventoryOrThrow(itemId, branchId);

        throwIfNotRented(inventory);

        throwIfHaveAlreadyBeenExtended(inventory);

        LocalDateTime newDueDate = inventory.getDueDate() != null 
                ? inventory.getDueDate().plusDays(additionalDays)
                : LocalDateTime.now().plusDays(additionalDays);
        
        inventory.setDueDate(newDueDate);
        inventory.setRentExtended(true);

        try {
            inventoryRepository.save(inventory);
        } catch (Exception e) {
            log.error("Error extending rental for item {} at branch {}: {}", itemId, branchId, e.getMessage());
            throw e;
        }
    }

    private static void throwIfHaveAlreadyBeenExtended(BranchInventory inventory) {
        if (Boolean.TRUE.equals(inventory.getRentExtended())) {
            throw new IllegalStateException("Rental has already been extended once.");
        }
    }

    private static void throwIfNotRented(BranchInventory inventory) {
        if (inventory.getStatus() != CopyStatus.RENTED) {
            throw new IllegalStateException("Copy is not rented. Cannot extend.");
        }
    }

    @Override
    public List<BranchInventoryDto> getInventoryForItem(Long itemId) {
        return inventoryRepository.findByItemId(itemId).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<BranchInventory> getAvailableCopies(Long itemId) {
        return inventoryRepository.findByItemId(itemId).stream()
                .filter(inv -> inv.getStatus() == CopyStatus.AVAILABLE)
                .toList();
    }

    @Override
    public List<Long> getAvailableItemsAtBranch(Long branchId) {
        return inventoryRepository.findItemIdsByBranchIdAndStatus(branchId, CopyStatus.AVAILABLE);
    }

    @Override
    public boolean isAvailableAtBranch(Long itemId, Long branchId) {
        return inventoryRepository.existsByItemIdAndBranchIdAndStatus(itemId, branchId, CopyStatus.AVAILABLE);
    }

    @Override
    public List<Long> getAvailableBranchIds(Long itemId) {
        return inventoryRepository.findAvailableBranchIds(itemId);
    }

    @Override
    public List<BranchInventory> getRentedByUser(Long userId) {
        return inventoryRepository.findByRentedByUserId(userId);
    }

    @Override
    @Transactional
    public BranchInventory addInventory(Long itemId, Long branchId) {
        log.info("Adding inventory for item {} at branch {}", itemId, branchId);
        
        if (inventoryRepository.existsByItemIdAndBranchId(itemId, branchId)) {
            throw new IllegalStateException("Inventory already exists for item " + itemId + " at branch " + branchId);
        }

        BranchInventory inventory = BranchInventory.builder()
                .itemId(itemId)
                .branchId(branchId)
                .status(CopyStatus.AVAILABLE)
                .build();

        return inventoryRepository.save(inventory);
    }

    @Override
    @Transactional
    public void updateStatus(Long itemId, Long branchId, String statusStr) {
        log.info("Updating status for item {} at branch {} to {}", itemId, branchId, statusStr);

        BranchInventory inventory = getBranchInventoryOrThrow(itemId, branchId);

        CopyStatus status;
        try {
            status = CopyStatus.valueOf(statusStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + statusStr);
        }

        inventory.setStatus(status);
        inventoryRepository.save(inventory);

    }

    private BranchInventoryDto toDto(BranchInventory inventory) {
        return BranchInventoryDto.builder()
                .id(inventory.getId())
                .itemId(inventory.getItemId())
                .branchId(inventory.getBranchId())
                .status(inventory.getStatus().name())
                .rentedByUserId(inventory.getRentedByUserId())
                .rentedAt(inventory.getRentedAt())
                .dueDate(inventory.getDueDate())
                .rentExtended(inventory.getRentExtended())
                .reservedByUserId(inventory.getReservedByUserId())
                .reservedAt(inventory.getReservedAt())
                .reservationExpiresAt(inventory.getReservationExpiresAt())
                .build();
    }
}

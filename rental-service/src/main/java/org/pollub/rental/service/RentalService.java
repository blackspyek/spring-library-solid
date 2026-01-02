package org.pollub.rental.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pollub.common.dto.ItemDto;
import org.pollub.common.dto.RentalHistoryDto;
import org.pollub.common.dto.ReservationResponse;
import org.pollub.rental.client.CatalogServiceClient;
import org.pollub.rental.model.RentalHistory;
import org.pollub.rental.model.RentalStatus;
import org.pollub.rental.repository.IRentalHistoryRepository;
import org.pollub.rental.utils.IRentalValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RentalService implements IRentalService {
    private static final int DAYS_TO_RENT = 14;

    private final IRentalHistoryRepository rentalHistoryRepository;
    private final CatalogServiceClient catalogServiceClient;
    private final IRentalValidator rentalValidator;

    public List<ItemDto> getActiveRentals(Long userId) {
        return catalogServiceClient.getItemsByUser(userId);
    }

    public List<RentalHistory> getUserRentalHistory(Long userId) {
        return rentalHistoryRepository.findByUserIdOrderByReturnedAtDesc(userId);
    }

    public List<RentalHistory> getItemRentalHistory(Long itemId) {
        return rentalHistoryRepository.findByItemId(itemId);
    }

    @Override
    @Transactional
    public ReservationResponse rentItem(Long itemId, Long userId, Long branchId) {
        this.rentalValidator.validateAbilityToRentOrThrow(userId, itemId);

        RentalHistory rentalHistory = RentalHistory.builder()
                .itemId(itemId)
                .userId(userId)
                .branchId(branchId)
                .status(RentalStatus.RENTED)
                .rentedAt(LocalDateTime.now())
                .isExtended(false)
                .dueDate(LocalDateTime.now().plusDays(DAYS_TO_RENT))
                .build();
        
        try{
            rentalHistoryRepository.save(rentalHistory);
            log.info("Rental history saved: {}", rentalHistory);
        } catch (Exception e){
            log.error("Error saving rental history for itemId: {}, userId: {}. Error: {}", itemId, userId, e.getMessage());
            throw e;
        }
        return catalogServiceClient.markAsRented(
                toRentalCatalogRequestDto(rentalHistory)
        );
    }

    @Override
    public void returnItem(Long itemId, Long branchId) {
        RentalHistory rentalHistory = getRentalHistory(itemId, branchId);

        rentalHistory.setReturnedAt(LocalDateTime.now());
        rentalHistory.setStatus(
                RentalStatus.RETURNED
        );
        try{
            rentalHistoryRepository.save(rentalHistory);
        }
        catch (Exception e){
            log.error("Error updating rental history for return of itemId: {}. Error: {}", itemId, e.getMessage());
            throw e;
        }

        catalogServiceClient.markAsReturned(itemId, branchId);

    }

    @Override
    public void extendLoan(Long itemId, Long branchId, int days) {
        RentalHistory rentalHistory = getRentalHistory(itemId, branchId);

        throwIfHaveBeenAlreadyRentedBefore(itemId, rentalHistory);

        extendLoanRecord(days, rentalHistory);
        try{
            rentalHistoryRepository.save(rentalHistory);
        }
        catch (Exception e){
            log.error("Error updating rental history for extension of itemId: {}. Error: {}", itemId, e.getMessage());
            throw e;
        }

        catalogServiceClient.extendRental(itemId, branchId, days);

    }

    private static void extendLoanRecord(int days, RentalHistory rentalHistory) {
        rentalHistory.setDueDate(
                rentalHistory.getDueDate().plusDays(days)
        );
        rentalHistory.setIsExtended(true);
    }

    private static void throwIfHaveBeenAlreadyRentedBefore(Long itemId, RentalHistory rentalHistory) {
        if (rentalHistory.getIsExtended() == true) {
            throw new IllegalStateException(
                    "Rental for itemId: " + itemId + " has already been extended."
            );
        }
    }

    private RentalHistory getRentalHistory(Long itemId, Long branchId) {
        return rentalHistoryRepository
                .findByItemIdAndBranchId(itemId, branchId)
                .orElseThrow(() -> new IllegalStateException(
                        "No active rental found for itemId: " + itemId
                ));
    }

    private RentalHistoryDto toRentalCatalogRequestDto(RentalHistory rentalHistory) {
        return RentalHistoryDto.builder()
                .id(rentalHistory.getId())
                .itemId(rentalHistory.getItemId())
                .userId(rentalHistory.getUserId())
                .branchId(rentalHistory.getBranchId())
                .rentedAt(rentalHistory.getRentedAt())
                .dueDate(rentalHistory.getDueDate())
                .returnedAt(rentalHistory.getReturnedAt())
                .build();
    }
}


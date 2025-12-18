package org.pollub.library.rental.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.pollub.library.branch.model.LibraryBranch;
import org.pollub.library.branch.repository.ILibraryBranchRepository;
import org.pollub.library.exception.RentalException;
import org.pollub.library.item.model.ItemStatus;
import org.pollub.library.item.model.LibraryItem;
import org.pollub.library.item.repository.ILibraryItemRepository;
import org.pollub.library.rental.utils.IRentalValidator;
import org.pollub.library.rental.model.RentalHistory;
import org.pollub.library.rental.model.dto.RentDto;
import org.pollub.library.rental.repository.IRentalHistoryRepository;
import org.pollub.library.user.model.User;
import org.pollub.library.user.service.IUserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RentalService implements IRentalService{
    private final ILibraryItemRepository<LibraryItem> libraryItemRepository;
    private final IUserService userService;
    private final IRentalValidator rentalValidator;
    private final IRentalHistoryRepository rentalHistoryRepository;
    private final ILibraryBranchRepository libraryBranchRepository;

    @Override
    public List<LibraryItem> getRentedItems(long userId) {
        userService.findById(userId);
        return libraryItemRepository.findByRentedByUserId(userId);
    }

    @Override
    public List<LibraryItem> getAvailableItems() {
        return libraryItemRepository.findByStatus(ItemStatus.AVAILABLE);
    }

    @Override
    public LibraryItem rentItem(RentDto rentDto) {
        LibraryItem libraryItem = getLibraryItemOrThrow(rentDto.getLibraryItemId());
        LibraryBranch branch = getBranchOrThrow(rentDto.getBranchId());

        User user = userService.findById(rentDto.getUserId());
        this.rentalValidator.validateAbilityToRentOrThrow(user.getId(), libraryItem);
        setLibraryItemToBeRentedByUser(libraryItem, user, branch);

        return saveOrThrow(libraryItem);
    }

    private LibraryBranch getBranchOrThrow(Long branchId) {
        return libraryBranchRepository.findById(branchId)
                .orElseThrow(() -> new RentalException("Library branch not found"));
    }

    private LibraryItem getLibraryItemOrThrow(long itemId) {
        return libraryItemRepository.findById(itemId)
                .orElseThrow(() -> new RentalException("LibraryItem not found"));
    }

    private LibraryItem saveOrThrow(LibraryItem libraryItem) {
        LibraryItem savedLibraryItem = libraryItemRepository.save(libraryItem);
        return Optional.of(savedLibraryItem)
                .orElseThrow(() -> new RentalException("LibraryItem cannot be saved"));
    }

    private void setLibraryItemToBeRentedByUser(LibraryItem libraryItem, User user, LibraryBranch branch) {
        libraryItem.setRentedByUser(user);
        libraryItem.setRentedFromBranch(branch);
        libraryItem.setStatus(ItemStatus.RENTED);
        libraryItem.setRentedAt(LocalDateTime.now());
        libraryItem.setDueDate(libraryItem.calculateDueTime());
        libraryItem.setRentExtended(false);
    }

    @Override
    public LibraryItem returnItem(long itemId) {
        LibraryItem libraryItem = getLibraryItemOrThrow(itemId);

        this.rentalValidator.checkAbilityToReturnOrThrow(libraryItem);

        saveRentalHistory(libraryItem);

        updateBookToBeAvailableToRent(libraryItem);

        return saveOrThrow(libraryItem);

    }

    private void saveRentalHistory(LibraryItem libraryItem) {
        if (libraryItem.getRentedByUser() != null && libraryItem.getRentedAt() != null) {
            if (libraryItem.getRentedFromBranch() == null) {
                return;
            }
            RentalHistory history = RentalHistory.builder()
                    .item(libraryItem)
                    .user(libraryItem.getRentedByUser())
                    .branch(libraryItem.getRentedFromBranch())
                    .rentedAt(libraryItem.getRentedAt())
                    .returnedAt(LocalDateTime.now())
                    .build();
            rentalHistoryRepository.save(history);
        }
    }

    private static void updateBookToBeAvailableToRent(LibraryItem libraryItem) {
        libraryItem.setRentedByUser(null);
        libraryItem.setRentedFromBranch(null);
        libraryItem.setStatus(ItemStatus.AVAILABLE);
        libraryItem.setRentedAt(null);
        libraryItem.setDueDate(null);
        libraryItem.setRentExtended(false);
    }

    @Override
    public LibraryItem extendRental(long itemId) {
        LibraryItem libraryItem = getLibraryItemOrThrow(itemId);

        if (libraryItem.getStatus() != ItemStatus.RENTED) {
            throw new RentalException("Item is not currently rented");
        }

        if (Boolean.TRUE.equals(libraryItem.getRentExtended())) {
            throw new RentalException("Rental has already been extended. Extension is allowed only once.");
        }

        libraryItem.setDueDate(libraryItem.getDueDate().plusDays(14));
        libraryItem.setRentExtended(true);

        return saveOrThrow(libraryItem);
    }

    @Override
    public List<LibraryItem> getAllRentedItems() {
        return libraryItemRepository.findByStatus(ItemStatus.RENTED);
    }
}

package org.pollub.library.rental.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.pollub.library.exception.RentalException;
import org.pollub.library.item.model.ItemStatus;
import org.pollub.library.item.model.LibraryItem;
import org.pollub.library.item.repository.ILibraryItemRepository;
import org.pollub.library.rental.utils.IRentalValidator;
import org.pollub.library.rental.model.dto.RentDto;
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

        User user = userService.findById(rentDto.getUserId());
        this.rentalValidator.validateAbilityToRentOrThrow(user.getId(), libraryItem);
        setLibraryItemToBeRentedByUser(libraryItem, user);

        return saveOrThrow(libraryItem);
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

    private void setLibraryItemToBeRentedByUser(LibraryItem libraryItem, User user) {
        libraryItem.setRentedByUser(user);
        libraryItem.setStatus(ItemStatus.RENTED);
        libraryItem.setRentedAt(LocalDateTime.now());
        libraryItem.setDueDate(libraryItem.calculateDueTime());
    }

    @Override
    public LibraryItem returnItem(long itemId) {
        LibraryItem libraryItem = getLibraryItemOrThrow(itemId);

        this.rentalValidator.checkAbilityToReturnOrThrow(libraryItem);

        updateBookToBeAvailableToRent(libraryItem);

        return saveOrThrow(libraryItem);

    }

    private static void updateBookToBeAvailableToRent(LibraryItem libraryItem) {
        libraryItem.setRentedByUser(null);
        libraryItem.setStatus(ItemStatus.AVAILABLE);
        libraryItem.setRentedAt(null);
        libraryItem.setDueDate(null);
    }
}

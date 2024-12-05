package org.pollub.library.rental.utils;

import lombok.RequiredArgsConstructor;
import org.pollub.library.exception.RentalException;
import org.pollub.library.item.model.ItemStatus;
import org.pollub.library.item.model.LibraryItem;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RentalValidator implements IRentalValidator {
    private final IRentalPolicy rentalPolicy;
    @Override
    public void validateAbilityToRentOrThrow(long userId, LibraryItem libraryItem) {
        throwIfNotAvailableToRent(libraryItem);
        throwIfUserCannotRent(userId);
    }

    @Override
    public void throwIfUserCannotRent(long userId) {
        if (!rentalPolicy.canUserRentItem(userId)) {
            throw new RentalException("User cannot rent more items");
        }
    }

    @Override
    public void throwIfNotAvailableToRent(LibraryItem libraryItem) {
        if (libraryItem.getStatus() != ItemStatus.AVAILABLE) {
            throw new RentalException("LibraryItem is not available");
        }
    }

    @Override
    public void checkAbilityToReturnOrThrow(LibraryItem libraryItem) {
        if (libraryItem.getStatus() != ItemStatus.RENTED) {
            throw new RentalException("Item is not currently rented");
        }
    }
}

package org.pollub.library.rental.utils;

import org.pollub.library.item.model.LibraryItem;

public interface IRentalValidator {
    void validateAbilityToRentOrThrow(long userId, LibraryItem libraryItem);
    void throwIfUserCannotRent(long userId);
    void throwIfNotAvailableToRent(LibraryItem libraryItem);
    void checkAbilityToReturnOrThrow(LibraryItem libraryItem);
}

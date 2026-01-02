package org.pollub.rental.utils;

public interface IRentalValidator {
    void validateAbilityToRentOrThrow(long userId, long libraryItem);
    void throwIfUserCannotRent(long userId);
}

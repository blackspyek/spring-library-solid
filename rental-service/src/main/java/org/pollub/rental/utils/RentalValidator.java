package org.pollub.rental.utils;

import lombok.RequiredArgsConstructor;
import org.pollub.rental.exception.RentalException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RentalValidator implements IRentalValidator {
    private final IRentalPolicy rentalPolicy;
    @Override
    public void validateAbilityToRentOrThrow(long userId, long libraryItem) {
        throwIfUserCannotRent(userId);
    }
    @Override
    public void throwIfUserCannotRent(long userId) {
        if (!rentalPolicy.canUserRentItem(userId)) {
            throw new RentalException("User cannot rent more items");
        }
    }

}

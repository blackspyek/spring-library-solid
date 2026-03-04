package org.pollub.rental.bridge;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pollub.rental.utils.IRentalValidator;
import org.springframework.stereotype.Component;

//start L2 Bridge implementation
/**
 * Standard implementation of IValidationBridge.
 * Delegates to existing IRentalValidator.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class StandardValidationBridge implements IValidationBridge {
    
    private final IRentalValidator rentalValidator;
    
    @Override
    public void validateAbilityToRentOrThrow(Long userId, Long itemId) {
        log.info("Validating rental ability for user: {}, item: {}", userId, itemId);
        rentalValidator.validateAbilityToRentOrThrow(userId, itemId);
    }
    
    @Override
    public boolean canExtendRental(Long itemId) {
        log.info("Checking if rental can be extended for item: {}", itemId);
        return true;
    }
}
//end L2 Bridge implementation
package org.pollub.rental.bridge;

//start L2 Bridge interface
/**
 * Bridge for abstracting validation logic.
 * Decouples RentalService from concrete validation implementations.
 */
public interface IValidationBridge {
    
    /**
     * Validate if a user can rent an item.
     * Throws exception if validation fails.
     */
    void validateAbilityToRentOrThrow(Long userId, Long itemId);
    
    /**
     * Check if rental can be extended.
     */
    boolean canExtendRental(Long itemId);
}
//end L2 Bridge interface
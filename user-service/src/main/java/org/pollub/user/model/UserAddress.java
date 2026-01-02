package org.pollub.user.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Embeddable address entity for users.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAddress {
    
    @NotBlank(message = "Street is required")
    private String street;
    
    @NotBlank(message = "City is required")
    private String city;
    
    @NotBlank(message = "Postal code is required")
    private String postalCode;
    
    @NotBlank(message = "Country is required")
    private String country;
    
    @NotBlank(message = "Building number is required")
    private String buildingNumber;
    
    private String apartmentNumber;
}

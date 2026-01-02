package org.pollub.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user address in registration request.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDto {
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

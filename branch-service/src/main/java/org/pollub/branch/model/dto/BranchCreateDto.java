package org.pollub.branch.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BranchCreateDto {
    
    @NotBlank(message = "Branch number is required")
    private String branchNumber;
    
    private String name;
    
    @NotBlank(message = "City is required")
    private String city;
    
    @NotBlank(message = "Address is required")
    private String address;
    
    @NotNull(message = "Latitude is required")
    private Double latitude;
    
    @NotNull(message = "Longitude is required")
    private Double longitude;
    
    private String phone;
    
    private String email;
    
    private String openingHours;
}

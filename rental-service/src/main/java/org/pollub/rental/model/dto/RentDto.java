package org.pollub.rental.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentDto {
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Item ID is required")
    private Long libraryItemId;
    
    @NotNull(message = "Branch ID is required")
    private Long branchId;
}

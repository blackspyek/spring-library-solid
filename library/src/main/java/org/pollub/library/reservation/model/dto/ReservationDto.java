package org.pollub.library.reservation.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDto {
    
    @NotNull(message = "Library item ID is required")
    private Long libraryItemId;
    
    @NotNull(message = "Branch ID is required")
    private Long branchId;
}

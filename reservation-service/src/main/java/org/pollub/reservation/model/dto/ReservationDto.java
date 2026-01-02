package org.pollub.reservation.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDto {

    @NotNull(message = "Item ID is required")
    private Long itemId;
    
    @NotNull(message = "Branch ID is required")
    private Long branchId;


}

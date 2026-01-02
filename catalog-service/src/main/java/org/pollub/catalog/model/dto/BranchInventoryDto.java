package org.pollub.catalog.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for branch inventory information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchInventoryDto {
    private Long id;
    private Long itemId;
    private Long branchId;
    private String status;
    
    // Rental info
    private Long rentedByUserId;
    private LocalDateTime rentedAt;
    private LocalDateTime dueDate;
    private Boolean rentExtended;
    
    // Reservation info
    private Long reservedByUserId;
    private LocalDateTime reservedAt;
    private LocalDateTime reservationExpiresAt;
}

package org.pollub.catalog.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentalHistoryRequest {
    private Long itemId;
    private Long userId;
    private Long branchId;
    private LocalDateTime rentedAt;
    private LocalDateTime returnedAt;
}

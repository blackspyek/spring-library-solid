package org.pollub.catalog.model.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReservationCatalogRequestDto {
    private Long id;
    private Long itemId;
    private Long userId;
    private Long branchId;

    private LocalDateTime reservedAt;
    private LocalDateTime expiresAt;
    private LocalDateTime resolvedAt;

    private String status;
}

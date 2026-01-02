package org.pollub.reservation.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.pollub.common.dto.ItemDto;
import org.pollub.reservation.model.ReservationStatus;

import java.time.LocalDateTime;

/**
 * Response DTO for reservations with enriched item data.
 * Contains full item details fetched from catalog-service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponseDto {

    private Long id;

    /**
     * Full item data from catalog-service.
     */
    private ItemDto item;

    /**
     * Branch ID where the reservation was made.
     * Frontend can look up branch details from its branch store.
     */
    private Long branchId;

    private Long userId;

    private LocalDateTime reservedAt;

    private LocalDateTime expiresAt;

    private LocalDateTime resolvedAt;

    private ReservationStatus status;
}

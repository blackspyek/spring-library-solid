package org.pollub.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponse {
    private Long itemId;
    private Long userId;
    private Long branchId;
    private LocalDateTime rentedAt;
    private LocalDateTime dueDate;
    private String status;
}

package org.pollub.common.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReservationItemDto {
    private Long id;
    private Long branchId;
    private LocalDateTime expiresAt;
    private Item item;

    @Data
    @Builder
    public static class Item {
        private Long id;
        private String title;
        private String imageUrl;
    }
}
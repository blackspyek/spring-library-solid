package org.pollub.catalog.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookAvailabilityDto {
    private Long id;
    private String title;
    private String author;
    private String status;
    private String imageUrl;
    private Integer daysUntilDue;
    private Set<Long> availableAtBranches;
}

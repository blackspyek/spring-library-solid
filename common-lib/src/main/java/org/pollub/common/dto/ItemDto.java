package org.pollub.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for library item data shared across services.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private String itemType; // BOOK or MOVIE
    private String status;
    private Integer releaseYear;
    private Boolean isBestseller;
    
    // Book-specific fields
    private String author;
    private String isbn;
    private Integer pageCount;
    
    // Movie-specific fields
    private String director;
    private Integer durationMinutes;
    
    // Rental-specific fields (populated when fetching user loans)
    private java.time.LocalDateTime dueDate;
    private Long rentedFromBranchId;
    private Boolean rentExtended;
}

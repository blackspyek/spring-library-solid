package org.pollub.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SingleBookDto {

    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private LocalDateTime rentedAt;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private Integer releaseYear;
    private String status;
    private Integer pageCount;
    private String isbn;
    private String paperType;
    private String publisher;
    private Integer shelfNumber;
    private String author;
    private String genre;
    private String libraryLocation;
    private Boolean bestseller;
    private List<Long> availableAtBranchesIds;
    private Long rentedFromBranchId;
    private Boolean rentExtended;
}


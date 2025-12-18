package org.pollub.library.item.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.pollub.library.branch.model.LibraryBranch;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookAvailabilityDto {
    private Long id;
    private String title;
    private String author;
    private String status;
    private String imageUrl;
    private Integer daysUntilDue;
    private Set<LibraryBranch> availableAtBranches;
}

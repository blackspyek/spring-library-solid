package org.pollub.library.rental.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentalHistoryDto {
    private Long id;
    private String itemTitle;
    private String itemAuthor;
    private String branchName;
    private String branchAddress;
    private String rentedAt;
    private String returnedAt;
}

package org.pollub.rental.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RentalLastHistoryDto {
    private Long id;
    private String itemTitle;
    private String itemAuthor;
    private String branchName;
    private String branchAddress;
    private String rentedAt;
    private String returnedAt;
}

package org.pollub.rental.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HistoryCatalogResponse {
    private Long itemId;
    private String itemTitle;
    private String itemAuthor;
    private String branchName;
    private String branchAddress;
}

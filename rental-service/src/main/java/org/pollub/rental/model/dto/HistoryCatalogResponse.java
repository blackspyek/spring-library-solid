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

    //Lab1 - Flyweight 2 Method Start
    public static final HistoryCatalogResponse DEFAULT_UNKNOWN = HistoryCatalogResponse.builder()
            .itemTitle("Brak danych")
            .itemAuthor("-")
            .branchName("Brak danych")
            .branchAddress("Brak danych")
            .build();
    //Lab1 - Flyweight 2 Method End

}

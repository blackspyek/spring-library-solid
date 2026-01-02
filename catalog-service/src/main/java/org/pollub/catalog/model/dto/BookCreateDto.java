package org.pollub.catalog.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookCreateDto {
    private String title;
    private String author;
    private Integer pageCount;
    private String paperType;
    private String publisher;
    private Integer shelfNumber;
    private String genre;
    private String isbn;
    private String description;
}

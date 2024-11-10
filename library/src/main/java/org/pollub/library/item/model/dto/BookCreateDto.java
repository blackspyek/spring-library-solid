package org.pollub.library.item.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.ISBN;

@Data
public class BookCreateDto {
    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotBlank(message = "Author is required")
    private String author;

    @NotNull(message = "Page count is required")
    @Positive(message = "Page count must be positive")
    private Integer pageCount;

    @NotBlank(message = "Paper type is required")
    private String paperType;

    @NotBlank(message = "Publisher is required")
    private String publisher;

    @NotNull(message = "Shelf number is required")
    @Positive(message = "Shelf number must be positive")
    private Integer shelfNumber;

    @NotBlank(message = "Genre is required")
    private String genre;

    @NotBlank(message = "ISBN is required")
    @ISBN(message = "Invalid ISBN number")
    private String isbn;
}


package org.pollub.library.item.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class MovieCreateDto {
    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotBlank(message = "Director is required")
    private String director;

    @NotBlank(message = "Resolution is required")
    private String resolution;

    @NotNull(message = "Duration is required")
    @Positive(message = "Duration must be positive")
    private Integer duration;

    @NotBlank(message = "File format is required")
    private String fileFormat;

    @NotBlank(message = "Digital rights is required")
    private String digitalRights;

    @NotBlank(message = "Genre is required")
    private String genre;

    @NotNull(message = "Shelf number is required")
    @Positive(message = "Shelf number must be positive")
    private Integer shelfNumber;
}

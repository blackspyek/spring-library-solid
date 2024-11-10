package org.pollub.library.rental.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class RentDto {
    @NotNull(message = "Library item id is required")
    @Positive(message = "Library item id must be a positive number")
    private Long libraryItemId;

    @NotNull(message = "User id is required")
    @Positive(message = "User id must be a positive number")
    private Long userId;
}

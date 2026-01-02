package org.pollub.feedback.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.pollub.feedback.model.FeedbackCategory;

/**
 * DTO for submitting feedback.
 */
public record FeedbackRequestDto(
        @NotNull(message = "Kategoria jest wymagana")
        FeedbackCategory category,

        @NotBlank(message = "Treść zgłoszenia jest wymagana")
        @Size(min = 10, max = 2000, message = "Treść musi mieć od 10 do 2000 znaków")
        String message,

        @Size(max = 500, message = "URL strony może mieć maksymalnie 500 znaków")
        String pageUrl
) {}

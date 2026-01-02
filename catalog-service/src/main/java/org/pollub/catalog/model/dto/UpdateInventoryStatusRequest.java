package org.pollub.catalog.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateInventoryStatusRequest {

    @NotBlank(message = "Status cannot be blank")
    private String status;
}
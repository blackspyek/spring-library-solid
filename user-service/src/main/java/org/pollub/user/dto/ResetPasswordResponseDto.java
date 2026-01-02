package org.pollub.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordResponseDto {
    
    private String email;
    private String temporaryPassword;
    private boolean success;
    private String message;
}

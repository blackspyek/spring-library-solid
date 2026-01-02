package org.pollub.auth.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterUserDto {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    private String password;
    
    @NotBlank(message = "First name is required")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^(\\+48)?\\d{9}$", message = "Phone must be a valid Polish number (9 digits, optionally with +48 prefix)")
    private String phone;
    
    @NotBlank(message = "PESEL is required")
    @Size(min = 11, max = 11, message = "PESEL must be 11 digits")
    private String pesel;
    
    @NotNull(message = "Address is required")
    @Valid
    private AddressDto address;
}

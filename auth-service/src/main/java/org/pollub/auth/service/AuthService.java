package org.pollub.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pollub.auth.client.UserServiceClient;
import org.pollub.auth.dto.*;
import org.pollub.auth.security.JwtTokenProvider;
import org.pollub.auth.util.PasswordGenerator;
import org.pollub.common.dto.UserAddressDto;
import org.pollub.common.dto.UserDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Authentication service for login and registration.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements IAuthService {
    private final UserServiceClient userServiceClient;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    public AuthResponse login(LoginUserDto request) {
        log.info("Login attempt for: {}", request.getUsernameOrEmail());
        
        // Validate credentials directly via user-service
        UserDto validatedUser = userServiceClient.validateCredentials(
                request.getUsernameOrEmail(), 
                request.getPassword()
        ).orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        
        log.debug("=== DEBUG AuthService.login() ===");
        log.debug("validatedUser.isMustChangePassword() = {}", validatedUser.isMustChangePassword());
        System.out.println("=== DEBUG AuthService.login() ===");
        System.out.println("validatedUser.isMustChangePassword() = " + validatedUser.isMustChangePassword());
        
        // Generate JWT token
        String token = jwtTokenProvider.generateToken(
                validatedUser.getId(),
                validatedUser.getUsername(),
                validatedUser.getRoles()
        );
        
        log.info("Login successful for user: {}", validatedUser.getUsername());
        
        AuthResponse response = AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getExpirationMs() / 1000)
                .userId(validatedUser.getId())
                .username(validatedUser.getUsername())
                .email(validatedUser.getEmail())
                .roles(validatedUser.getRoles())
                .employeeOfBranch(validatedUser.getEmployeeBranchId())
                .mustChangePassword(validatedUser.isMustChangePassword())
                .build();
        
        System.out.println("AuthResponse.mustChangePassword = " + response.isMustChangePassword());
        
        return response;
    }

    @Override
    public AuthResponse register(RegisterUserDto request) {
        log.info("Registration attempt for: {}", request.getEmail());
        
        // Generate temporary password
        String temporaryPassword = PasswordGenerator.generatePassword();
        
        UserAddressDto addressDto = UserAddressDto.builder()
                .street(request.getAddress().getStreet())
                .city(request.getAddress().getCity())
                .postalCode(request.getAddress().getPostalCode())
                .country(request.getAddress().getCountry())
                .buildingNumber(request.getAddress().getBuildingNumber())
                .apartmentNumber(request.getAddress().getApartmentNumber())
                .build();

        
        UserDto newUser = UserDto.builder()
                .username(request.getEmail())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(temporaryPassword)
                .pesel(request.getPesel())
                .phone(request.getPhone())
                .address(addressDto)
                .build();

        
        UserDto createdUser = userServiceClient.createUser(newUser);
        

        emailService.sendTemporaryPasswordEmail(createdUser.getEmail(), temporaryPassword);

        String token = jwtTokenProvider.generateToken(
                createdUser.getId(),
                createdUser.getUsername(),
                createdUser.getRoles()
        );

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getExpirationMs() / 1000)
                .userId(createdUser.getId())
                .username(createdUser.getUsername())
                .email(createdUser.getEmail())
                .roles(createdUser.getRoles())
                .mustChangePassword(true)
                .build();
    }
    
    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }
    
    public Long getUserIdFromToken(String token) {
        return jwtTokenProvider.getUserIdFromToken(token);
    }
    
    public String getUsernameFromToken(String token) {
        return jwtTokenProvider.getUsernameFromToken(token);
    }
    
    public String getRoleFromToken(String token) {
        return jwtTokenProvider.getRoleFromToken(token);
    }
    public AuthResponse getCurrentUser(String username) {
        // Fetch full user details from user-service
        UserDto user = userServiceClient.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));


        // Return response with user details (token is not needed here as it's already validated)
        return AuthResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(
                       user.getRoles()
                )
                .employeeOfBranch(user.getEmployeeBranchId())
                .mustChangePassword(user.isMustChangePassword())
                .build();
    }

    /**
     * Reset password for a user identified by email and PESEL.
     * Generates a new temporary password, updates the user, and sends an email with the new password.
     */
    @Override
    public ResetPasswordResponseDto resetPassword(ResetPasswordRequestDto request) {
        log.info("Password reset attempt for email: {}", request.getEmail());
        
        // Call user-service to reset password and get the new temporary password
        var userServiceResponse = userServiceClient.resetPassword(request.getEmail(), request.getPesel());
        
        if (userServiceResponse.isEmpty()) {
            log.warn("Password reset failed - user-service returned empty response for email: {}", request.getEmail());
            return ResetPasswordResponseDto.builder()
                    .success(false)
                    .message("Jeśli podane dane są poprawne, nowe hasło zostanie wysłane na podany adres email.")
                    .build();
        }
        
        var response = userServiceResponse.get();
        
        // If user was found and password was reset, send email
        if (response.success() && response.temporaryPassword() != null) {
            try {
                emailService.sendPasswordResetEmail(response.email(), response.temporaryPassword());
                log.info("Password reset successful for email: {}", response.email());
            } catch (Exception e) {
                log.error("Failed to send password reset email to: {}", response.email(), e);
                return ResetPasswordResponseDto.builder()
                        .success(false)
                        .message("Wystąpił błąd podczas wysyłania emaila. Spróbuj ponownie później.")
                        .build();
            }
        }
        
        // Always return a generic success message for security
        return ResetPasswordResponseDto.builder()
                .success(true)
                .message("Jeśli podane dane są poprawne, nowe hasło zostanie wysłane na podany adres email.")
                .build();
    }
}

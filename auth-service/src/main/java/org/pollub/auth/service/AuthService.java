package org.pollub.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pollub.auth.client.UserServiceClient;
import org.pollub.auth.dto.AuthResponse;
import org.pollub.auth.dto.LoginUserDto;
import org.pollub.auth.dto.RegisterUserDto;
import org.pollub.auth.security.JwtTokenProvider;
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

    @Override
    public AuthResponse login(LoginUserDto request) {
        log.info("Login attempt for: {}", request.getUsernameOrEmail());
        
        // Validate credentials directly via user-service
        UserDto validatedUser = userServiceClient.validateCredentials(
                request.getUsernameOrEmail(), 
                request.getPassword()
        ).orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        
        // Generate JWT token
        String token = jwtTokenProvider.generateToken(
                validatedUser.getId(),
                validatedUser.getUsername(),
                validatedUser.getRoles()
        );
        
        log.info("Login successful for user: {}", validatedUser.getUsername());
        
        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getExpirationMs() / 1000)
                .userId(validatedUser.getId())
                .username(validatedUser.getUsername())
                .email(validatedUser.getEmail())
                .roles(validatedUser.getRoles())
                .employeeOfBranch(validatedUser.getEmployeeBranchId())
                .build();
    }

    @Override
    public AuthResponse register(RegisterUserDto request) {
        log.info("Registration attempt for: {}", request.getEmail());
        
        UserDto newUser = UserDto.builder()
                .username(request.getEmail())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(request.getPassword())
                .build();
        
        UserDto createdUser = userServiceClient.createUser(newUser);
        
        // Generate JWT token
        String token = jwtTokenProvider.generateToken(
                createdUser.getId(),
                createdUser.getUsername(),
                createdUser.getRoles()
        );

        log.info("Registration successful for user: {}", createdUser.getUsername());
        
        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getExpirationMs() / 1000)
                .userId(createdUser.getId())
                .username(createdUser.getUsername())
                .email(createdUser.getEmail())
                .roles(createdUser.getRoles())
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
                .build();
    }
}

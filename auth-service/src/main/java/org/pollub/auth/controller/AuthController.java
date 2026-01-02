package org.pollub.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pollub.auth.dto.AuthResponse;
import org.pollub.auth.dto.LoginUserDto;
import org.pollub.auth.dto.RegisterUserDto;
import org.pollub.auth.dto.ResetPasswordRequestDto;
import org.pollub.auth.dto.ResetPasswordResponseDto;
import org.pollub.auth.security.JwtTokenProvider;
import org.pollub.auth.service.IAuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    
    private final IAuthService authService;
    private final JwtTokenProvider jwtTokenProvider;


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginUserDto request) {
        AuthResponse response = authService.login(request);
        ResponseCookie cookie = createJwtCookie(response.getAccessToken());
        AuthResponse bodyResponse = AuthResponse.builder()
                .username(response.getUsername())
                .roles(response.getRoles())
                .employeeOfBranch(response.getEmployeeOfBranch())
                .userId(response.getUserId())
                .email(response.getEmail())
                .mustChangePassword(response.isMustChangePassword())
                .build();

        log.info("User {} logged in successfully", response.getUsername());
        //log debug info about mustChangePassword
        log.debug("=== DEBUG AuthController.login() ===");
        log.debug("response.isMustChangePassword() = {}", response.isMustChangePassword());


        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(bodyResponse);
    }

    private ResponseCookie createJwtCookie(String token) {
        long expirationMillis = jwtTokenProvider.getExpirationMs();
        long expirationSeconds = TimeUnit.MILLISECONDS.toSeconds(expirationMillis);

        return ResponseCookie.from("jwt-token", token)
                .httpOnly(true)
                .path("/")
                .maxAge(expirationSeconds)
                .build();

    }
    
    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterUserDto request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("valid", false));
        }
        
        String token = authHeader.substring(7);
        
        if (authService.validateToken(token)) {
            return ResponseEntity.ok(Map.of(
                    "valid", true,
                    "userId", authService.getUserIdFromToken(token),
                    "username", authService.getUsernameFromToken(token),
                    "role", authService.getRoleFromToken(token)
            ));
        }
        
        return ResponseEntity.status(401).body(Map.of("valid", false));
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie cookie = ResponseCookie.from("jwt-token", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AuthResponse> getCurrentUser() {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(authService.getCurrentUser(username));
    }

    /**
     * Reset password endpoint - verifies email and PESEL, generates new password, and sends email.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ResetPasswordResponseDto> resetPassword(@Valid @RequestBody ResetPasswordRequestDto request) {
        log.info("Password reset request received for email: {}", request.getEmail());
        ResetPasswordResponseDto response = authService.resetPassword(request);
        return ResponseEntity.ok(response);
    }
}

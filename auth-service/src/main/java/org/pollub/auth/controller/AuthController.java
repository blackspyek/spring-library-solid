package org.pollub.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pollub.auth.dto.AuthResponse;
import org.pollub.auth.dto.LoginUserDto;
import org.pollub.auth.dto.RegisterUserDto;
import org.pollub.auth.dto.ResetPasswordRequestDto;
import org.pollub.auth.dto.ResetPasswordResponseDto;
import org.pollub.auth.facade.AuthFacade;
import org.pollub.auth.facade.LoginResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    //Lab1 - Facade 2 Method Start
    private final AuthFacade authFacade;


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginUserDto request) {
        LoginResult result = authFacade.login(request);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, result.getJwtCookie().toString())
                .body(result.getAuthResponse());
    }
    
    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterUserDto request) {
        try {
            AuthResponse response = authFacade.register(request);
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
        Map<String, Object> result = authFacade.validateAndExtractUser(token);

        if (result != null) {
            return ResponseEntity.ok(result);
        }
        
        return ResponseEntity.status(401).body(Map.of("valid", false));
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie cookie = authFacade.createLogoutCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AuthResponse> getCurrentUser() {
        String username = getContext().getAuthentication().getName();
        return ResponseEntity.ok(authFacade.getCurrentUser(username));
    }

    /**
     * Reset password endpoint - verifies email and PESEL, generates new password, and sends email.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ResetPasswordResponseDto> resetPassword(@Valid @RequestBody ResetPasswordRequestDto request) {
        return ResponseEntity.ok(authFacade.resetPassword(request));
    }
    //Lab1 - Facade 2 Method End

}

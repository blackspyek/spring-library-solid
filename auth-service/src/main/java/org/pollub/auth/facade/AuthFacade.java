package org.pollub.auth.facade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pollub.auth.dto.*;
import org.pollub.auth.security.JwtTokenProvider;
import org.pollub.auth.service.IAuthService;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Facade (wzorzec Facade) upraszczający operacje uwierzytelniania.
 * Ukrywa złożoność interakcji między IAuthService, JwtTokenProvider
 * oraz logiką tworzenia cookie JWT za jednym spójnym interfejsem.
 */
//Lab1 - Facade 2 Method Start
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthFacade {

    private final IAuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Logowanie użytkownika — walidacja, generowanie tokenu, tworzenie cookie.
     * Zwraca LoginResult zawierający odpowiedź (bez tokenu w body) i cookie JWT.
     */
    public LoginResult login(LoginUserDto request) {
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

        return new LoginResult(bodyResponse, cookie);
    }

    /**
     * Rejestracja nowego użytkownika.
     */
    public AuthResponse register(RegisterUserDto request) {
        return authService.register(request);
    }

    /**
     * Walidacja tokenu i ekstrakcja danych użytkownika.
     * Zwraca mapę z danymi użytkownika lub null jeśli token jest nieprawidłowy.
     */
    public Map<String, Object> validateAndExtractUser(String token) {
        if (!authService.validateToken(token)) {
            return null;
        }

        return Map.of(
                "valid", true,
                "userId", authService.getUserIdFromToken(token),
                "username", authService.getUsernameFromToken(token),
                "role", authService.getRoleFromToken(token)
        );
    }

    /**
     * Pobieranie danych aktualnego użytkownika po nazwie.
     */
    public AuthResponse getCurrentUser(String username) {
        return authService.getCurrentUser(username);
    }

    /**
     * Tworzenie cookie wylogowania (pusty JWT, maxAge=0).
     */
    public ResponseCookie createLogoutCookie() {
        return ResponseCookie.from("jwt-token", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();
    }

    /**
     * Reset hasła użytkownika.
     */
    public ResetPasswordResponseDto resetPassword(ResetPasswordRequestDto request) {
        log.info("Password reset request received for email: {}", request.getEmail());
        return authService.resetPassword(request);
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
}
//Lab1 - Facade 2 Method End

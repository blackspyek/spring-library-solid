package org.pollub.auth.facade;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.pollub.auth.dto.AuthResponse;
import org.springframework.http.ResponseCookie;

/**
 * Wynik operacji logowania z fasady — łączy odpowiedź uwierzytelniania z cookie JWT.
 * Używany przez AuthFacade do zwrócenia pełnego rezultatu logowania w jednym obiekcie.
 */
@Getter
@AllArgsConstructor
public class LoginResult {
    private final AuthResponse authResponse;
    private final ResponseCookie jwtCookie;
}

package org.pollub.library.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pollub.library.auth.model.AuthResponse;
import org.pollub.library.auth.model.LoginUserDto;
import org.pollub.library.auth.model.RegisterUserDto;
import org.pollub.library.auth.service.IAuthenticationServiceFacade;
import org.pollub.library.auth.service.jwt.JwtTokenService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {
    private final IAuthenticationServiceFacade authServiceFacade;
    private final JwtTokenService jwtTokenService;


    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterUserDto request) {
        Boolean response = authServiceFacade.register(request);

        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginUserDto request) {
        AuthResponse response = authServiceFacade.login(request);
        ResponseCookie cookie = createJwtCookie(response.token());
        AuthResponse bodyResponse = new AuthResponse(null,response.roles(), response.username());


        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(bodyResponse);
    }
    private ResponseCookie createJwtCookie(String token) {
        long expirationMillis = jwtTokenService.getExpirationTime();
        long expirationSeconds = TimeUnit.MILLISECONDS.toSeconds(expirationMillis);

        return ResponseCookie.from("jwt-token", token)
                .httpOnly(true)
                .path("/")
                .maxAge(expirationSeconds)
                .build();

    }
}

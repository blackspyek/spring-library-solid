package org.pollub.library.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pollub.library.auth.model.ApiResponse;
import org.pollub.library.auth.model.AuthResponse;
import org.pollub.library.auth.model.LoginUserDto;
import org.pollub.library.auth.model.RegisterUserDto;
import org.pollub.library.auth.service.IAuthenticationServiceFacade;
import org.pollub.library.user.model.Role;
import org.pollub.library.user.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final IAuthenticationServiceFacade authServiceFacade;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterUserDto request) {
        AuthResponse response = authServiceFacade.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginUserDto request) {
        AuthResponse response = authServiceFacade.login(request);
        return ResponseEntity.ok(response);
    }

}
package org.pollub.auth.service;

import org.pollub.auth.dto.AuthResponse;
import org.pollub.auth.dto.LoginUserDto;
import org.pollub.auth.dto.RegisterUserDto;
import org.pollub.auth.dto.ResetPasswordRequestDto;
import org.pollub.auth.dto.ResetPasswordResponseDto;

public interface IAuthService {

    AuthResponse register(RegisterUserDto request);

    AuthResponse login(LoginUserDto request);

    boolean validateToken(String token);

    Long getUserIdFromToken(String token);

    String getUsernameFromToken(String token);

    String getRoleFromToken(String token);

    AuthResponse getCurrentUser(String username);

    ResetPasswordResponseDto resetPassword(ResetPasswordRequestDto request);
}

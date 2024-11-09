package org.pollub.library.auth.service;

import lombok.RequiredArgsConstructor;
import org.pollub.library.auth.model.ApiResponse;
import org.pollub.library.auth.model.AuthResponse;
import org.pollub.library.auth.model.LoginUserDto;
import org.pollub.library.auth.model.RegisterUserDto;
import org.pollub.library.auth.service.jwt.JwtTokenService;
import org.pollub.library.user.model.Role;
import org.pollub.library.user.model.User;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceFacade implements IAuthenticationServiceFacade {
    private final AuthenticationService authenticationService;
    private final JwtTokenService jwtTokenService;


    @Override
    public AuthResponse register(RegisterUserDto userDto) {
        var user = authenticationService.registerUser(userDto);
        var token = jwtTokenService.generateToken(user);
        return new AuthResponse(token);
    }

    @Override
    public AuthResponse login(LoginUserDto userDto) {
        var user = authenticationService.authenticateUser(userDto);
        var token = jwtTokenService.generateToken(user);
        return new AuthResponse(token);
    }

}

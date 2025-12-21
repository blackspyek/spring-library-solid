package org.pollub.library.auth.service;

import lombok.RequiredArgsConstructor;
import org.pollub.library.auth.model.AuthResponse;
import org.pollub.library.auth.model.LoginUserDto;
import org.pollub.library.auth.model.RegisterUserDto;
import org.pollub.library.auth.service.jwt.JwtTokenService;
import org.pollub.library.user.model.Role;
import org.pollub.library.user.model.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceFacade implements IAuthenticationServiceFacade {
    private final AuthenticationService authenticationService;
    private final JwtTokenService jwtTokenService;

    @Override
    public Boolean register(RegisterUserDto userDto) {
        var user = authenticationService.registerUser(userDto);
        var token = generateTokenForUser(user);
        return true;
    }

    @Override
    public AuthResponse login(LoginUserDto userDto) {
        var user = authenticationService.authenticateUser(userDto);
        var token = generateTokenForUser(user);
        Role[] roles = user.getRoles().toArray(new Role[0]);
        return new AuthResponse(token, roles, user.getUsername(), user.getEmployeeBranch());
    }

    private String generateTokenForUser(User user) {
        return jwtTokenService.generateToken(user);
    }


}

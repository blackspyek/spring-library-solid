package org.pollub.library.auth.service.util;

import org.pollub.library.auth.model.LoginUserDto;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationStrategy {
    private final AuthenticationManager authenticationManager;

    public AuthenticationStrategy(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
    public void authenticate(LoginUserDto loginUserDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUserDto.getUsername(),
                        loginUserDto.getPassword()
                )
        );
    }
}

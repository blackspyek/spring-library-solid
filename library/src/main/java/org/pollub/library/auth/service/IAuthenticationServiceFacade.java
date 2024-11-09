package org.pollub.library.auth.service;

import org.pollub.library.auth.model.AuthResponse;
import org.pollub.library.auth.model.LoginUserDto;
import org.pollub.library.auth.model.RegisterUserDto;


public interface IAuthenticationServiceFacade {

    AuthResponse register(RegisterUserDto request);

    AuthResponse login(LoginUserDto request);

}

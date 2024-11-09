package org.pollub.library.auth.service;

import org.pollub.library.auth.model.LoginUserDto;
import org.pollub.library.auth.model.RegisterUserDto;
import org.pollub.library.user.model.User;


public interface IAuthenticationService {
    User registerUser(RegisterUserDto registerUserDto);
    User authenticateUser(LoginUserDto loginUserDto);
}

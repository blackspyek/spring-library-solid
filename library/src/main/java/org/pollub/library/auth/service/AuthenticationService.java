package org.pollub.library.auth.service;

import lombok.RequiredArgsConstructor;
import org.pollub.library.auth.model.LoginUserDto;
import org.pollub.library.auth.model.RegisterUserDto;
import org.pollub.library.auth.service.util.AuthenticationStrategy;
import org.pollub.library.auth.service.util.UserFactory;
import org.pollub.library.auth.service.util.UserValidator;
import org.pollub.library.exception.UserNotFoundException;
import org.pollub.library.user.repository.IUserRepository;
import org.pollub.library.user.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements IAuthenticationService {
    private final IUserRepository userRepository;
    private final UserFactory userFactory;
    private final UserValidator userValidator;
    private final AuthenticationStrategy authenticationStrategy;

    @Override
    @Transactional
    public User registerUser(RegisterUserDto registerUserDto) {
        userValidator.validateNewUser(registerUserDto);
        User user = userFactory.createUser(registerUserDto);
        return userRepository.save(user);
    }

    @Override
    public User authenticateUser(LoginUserDto loginUserDto) {
        User user = userRepository.findByEmail(loginUserDto.getEmail())
                .orElseThrow(() -> new UserNotFoundException(loginUserDto.getEmail()));

        userValidator.validateUserStatus(user);
        authenticationStrategy.authenticate(loginUserDto);

        return user;
    }




}

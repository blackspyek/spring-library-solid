package org.pollub.library.auth.service.util;

import lombok.RequiredArgsConstructor;
import org.pollub.library.auth.model.RegisterUserDto;
import org.pollub.library.exception.DisabledUserException;
import org.pollub.library.exception.UserAlreadyExistsException;
import org.pollub.library.user.repository.IUserRepository;
import org.pollub.library.user.model.User;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {
    private final IUserRepository userRepository;

    public void validateNewUser(RegisterUserDto registerUserDto) {
        String email = registerUserDto.getEmail().toLowerCase();
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException(email);
        }
    }

    public void validateUserStatus(User user) {
        if (!user.isEnabled()) {
            throw new DisabledUserException(user.getUsername());
        }
    }
}

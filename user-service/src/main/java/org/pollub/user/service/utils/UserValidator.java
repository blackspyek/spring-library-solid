package org.pollub.user.service.utils;

import lombok.RequiredArgsConstructor;
import org.pollub.user.exception.DisabledUserException;
import org.pollub.user.exception.UserAlreadyExistsException;
import org.pollub.user.model.User;
import org.pollub.user.repository.IUserRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {
    private final IUserRepository userRepository;

    public void validateNewUser(User user) {
        String email = user.getEmail().toLowerCase();
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

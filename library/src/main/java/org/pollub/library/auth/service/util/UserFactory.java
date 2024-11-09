package org.pollub.library.auth.service.util;

import lombok.RequiredArgsConstructor;
import org.pollub.library.auth.model.RegisterUserDto;
import org.pollub.library.user.model.Role;
import org.pollub.library.user.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserFactory {
    private final PasswordEncoder passwordEncoder;
    private final UidGenerator uidGenerator;

    public User createUser(RegisterUserDto registerUserDto) {
        User user = new User();
        user.setUsername(uidGenerator.generateUid());
        user.setEmail(registerUserDto.getEmail().toLowerCase());
        user.setPassword(passwordEncoder.encode(registerUserDto.getPassword()));
        user.setRoles(Set.of(Role.ROLE_READER));
        user.setEnabled(true);
        return user;
    }
}
package org.pollub.user.service.utils;

import lombok.RequiredArgsConstructor;
import org.pollub.user.model.Role;
import org.pollub.user.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserFactory {
    private final PasswordEncoder passwordEncoder;
    private final UidGenerator uidGenerator;

    public User createUser(User userDto) {
        User user = new User();
        user.setUsername(userDto.getEmail());
        user.setEmail(userDto.getEmail().toLowerCase());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRoles(Set.of(Role.ROLE_READER));
        user.setReaderId(
                uidGenerator.generateUid()
        );
        user.setEnabled(true);
        user.setPesel(userDto.getPesel());
        user.setAddress(userDto.getAddress());
        user.setPhone(userDto.getPhone());
        user.setName(userDto.getName());
        user.setSurname(userDto.getSurname());

        return user;
    }

    public User createUser(String username, String email, Set<Role> roles, String password) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(roles);
        user.setEnabled(true);
        user.setReaderId(uidGenerator.generateUid());
        return user;
    }
}
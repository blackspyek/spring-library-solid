package org.pollub.user.service.utils;

import lombok.RequiredArgsConstructor;
import org.pollub.user.model.Role;
import org.pollub.user.model.User;
import org.pollub.user.model.UserAddress;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

//Lab1 - Factory 3 Method Start
@Component
@RequiredArgsConstructor
public class AdminUserFactory implements IUserFactory {
    private final PasswordEncoder passwordEncoder;
    private final UidGenerator uidGenerator;

    private final User templateAdmin = User.builder()
            .roles(Set.of(Role.ROLE_ADMIN))
            .enabled(true)
            .address(UserAddress.builder()
                    .street("Admin Street")
                    .city("Admin City")
                    .postalCode("00-000")
                    .country("Poland")
                    .buildingNumber("1")
                    .build())
            .build();

    @Override
    public User createUser(User userDto) {
        //Lab1 - Prototype Start
        //Lab1 - Builder Start
        return templateAdmin.clone().toBuilder()
                .username(userDto.getEmail())
                .email(userDto.getEmail().toLowerCase())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .readerId(uidGenerator.generateUid())
                .pesel(userDto.getPesel())
                .address(userDto.getAddress() != null ? userDto.getAddress() : templateAdmin.getAddress())
                .phone(userDto.getPhone())
                .name(userDto.getName())
                .surname(userDto.getSurname())
                .mustChangePassword(true)
                .build();
        //Lab1 End Builder
        //Lab1 End Prototype
    }

    @Override
    public User createUser(String username, String email, String password, String name, String surname) {
        //Lab1 - Prototype Start
        //Lab1 - Builder Start
        return templateAdmin.clone().toBuilder()
                .username(username)
                .email(email.toLowerCase())
                .password(passwordEncoder.encode(password))
                .readerId(uidGenerator.generateUid())
                .name(name)
                .surname(surname)
                .mustChangePassword(false)
                .build();
        //Lab1 End Builder
        //Lab1 End Prototype
    }
}
//Lab1 End Factory 3 Method

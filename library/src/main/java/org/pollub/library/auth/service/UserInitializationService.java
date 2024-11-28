package org.pollub.library.auth.service;

import jakarta.transaction.Transactional;

import org.pollub.library.auth.service.util.UserFactory;
import org.pollub.library.user.model.Role;
import org.pollub.library.user.model.User;
import org.pollub.library.user.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserInitializationService {

    private final UserFactory userFactory;
    private final IUserRepository userRepository;

    public UserInitializationService(UserFactory userFactory, IUserRepository userRepository) {
        this.userFactory = userFactory;
        this.userRepository = userRepository;
    }

    @Value("${admin.password}")
    private String adminPassword;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initializeUsers() {
        if (userRepository.count() == 0) {
            User admin = userFactory.createUser(
                    "admin",
                    "admin@test.pl",
                    Set.of(Role.ROLE_ADMIN),
                    adminPassword
            );
            userRepository.save(admin);
            User librarian = userFactory.createUser(
                    "librarian",
                    "librarian@test.pl",
                    Set.of(Role.ROLE_LIBRARIAN),
                    adminPassword
            );
            userRepository.save(librarian);
        }

    }
}

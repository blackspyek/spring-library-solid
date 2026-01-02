package org.pollub.user.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pollub.user.model.Role;
import org.pollub.user.model.User;
import org.pollub.user.repository.IUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Initializes default admin and librarian users if the database is empty.
 * Only runs when there are no users in the system.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final IUserRepository IUserRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) {
        if (IUserRepository.count() == 0) {
            log.info("No users found. Creating default admin and librarian...");
            
            // Create Admin user
            User admin = User.builder()
                    .username("admin")
                    .email("admin@library.com")
                    .password(passwordEncoder.encode("admin123"))
                    .name("System")
                    .surname("Administrator")
                    .roles(Set.of(Role.ROLE_ADMIN))
                    .enabled(true)
                    .build();
            IUserRepository.save(admin);
            log.info("Created admin user: admin@library.com / admin123");
            
            // Create Librarian user
            User librarian = User.builder()
                    .username("librarian")
                    .email("librarian@library.com")
                    .password(passwordEncoder.encode("librarian123"))
                    .name("Default")
                    .surname("Librarian")
                    .roles(Set.of(Role.ROLE_LIBRARIAN))
                    .employeeBranchId(1L)
                    .enabled(true)
                    .build();
            IUserRepository.save(librarian);
            log.info("Created librarian user: librarian@library.com / librarian123");
            
            log.info("Default users created successfully!");
        } else {
            log.info("Users already exist. Skipping default user creation.");
        }
    }
}

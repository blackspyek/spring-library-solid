package org.pollub.user.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pollub.user.model.User;
import org.pollub.user.repository.IUserRepository;
import org.pollub.user.service.utils.AdminUserFactory;
import org.pollub.user.service.utils.UserFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final IUserRepository IUserRepository;
    //Lab1 - Factory Method Start
    private final AdminUserFactory adminUserFactory;
    private final UserFactory userFactory;
    //Lab1 End Factory Method
    
    @Override
    public void run(String... args) {
        if (IUserRepository.count() == 0) {
            log.info("No users found. Creating default admin and librarian...");

            //Lab1 - Factory Method Start
            User admin = adminUserFactory.createUser(
                    "admin", "admin@library.com", "admin123", "System", "Administrator"
            );
            IUserRepository.save(admin);
            log.info("Created admin user: admin@library.com / admin123");
            
            User librarian = userFactory.createUser(
                    "librarian", "librarian@library.com", "librarian123", "Default", "Librarian"
            );
            IUserRepository.save(librarian);
            log.info("Created librarian user: librarian@library.com / librarian123");
            //Lab1 End Factory Method
            
            log.info("Default users created successfully!");
        } else {
            log.info("Users already exist. Skipping default user creation.");
        }
    }
}

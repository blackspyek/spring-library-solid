package org.pollub.rental.runner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pollub.rental.service.RentalReminderService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Command line runner for sending rental reminder emails.
 * Activated only when the 'job' profile is active.
 * Used by Kubernetes CronJob to run the reminder task once and exit.
 */
@Component
@Profile("job")
@RequiredArgsConstructor
@Slf4j
public class RentalReminderRunner implements CommandLineRunner {

    private final RentalReminderService rentalReminderService;
    private final ApplicationContext applicationContext;

    @Override
    public void run(String... args) {
        log.info("Starting rental reminder runner...");
        try {
            rentalReminderService.sendReminders();
            log.info("Rental reminder runner completed successfully.");
        } catch (Exception e) {
            log.error("Rental reminder runner failed.", e);
            System.exit(1);
        }
        // Explicitly shutdown Spring context and exit
        int exitCode = SpringApplication.exit(applicationContext, () -> 0);
        System.exit(exitCode);
    }
}

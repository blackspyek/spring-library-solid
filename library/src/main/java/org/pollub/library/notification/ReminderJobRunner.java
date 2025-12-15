package org.pollub.library.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReminderJobRunner implements CommandLineRunner {

    private final ReminderService reminderService;

    @Value("${app.mode:web}")
    private String appMode;

    @Override
    public void run(String... args) {
        if (!"reminder".equalsIgnoreCase(appMode)) {
            log.info("Running in web mode, skipping reminder job");
            return;
        }

        log.info("Starting reminder job...");

        try {
            reminderService.processReturnReminders();
            log.info("Reminder job completed successfully");
        } catch (Exception e) {
            log.error("Reminder job failed: {}", e.getMessage(), e);
            System.exit(1);
        }

        System.exit(0);
    }
}

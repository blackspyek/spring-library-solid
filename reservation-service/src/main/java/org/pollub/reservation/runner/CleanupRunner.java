package org.pollub.reservation.runner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pollub.reservation.service.IReservationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Command line runner for cleaning up expired reservations.
 * Activated only when the 'job' profile is active.
 * Used by Kubernetes CronJob to run the cleanup task once and exit.
 */
@Component
@Profile("job")
@RequiredArgsConstructor
@Slf4j
public class CleanupRunner implements CommandLineRunner {

    private final IReservationService reservationService;
    private final ApplicationContext applicationContext;

    @Override
    public void run(String... args) {
        log.info("Starting reservation cleanup job...");
        try {
            reservationService.cleanupExpiredReservations();
            log.info("Reservation cleanup job completed successfully.");
        } catch (Exception e) {
            log.error("Reservation cleanup job failed.", e);
            System.exit(1);
        }
        // Explicitly shutdown Spring context and exit
        int exitCode = SpringApplication.exit(applicationContext, () -> 0);
        System.exit(exitCode);
    }
}

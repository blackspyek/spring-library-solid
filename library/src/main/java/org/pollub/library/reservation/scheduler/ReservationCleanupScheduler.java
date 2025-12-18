package org.pollub.library.reservation.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pollub.library.reservation.service.IReservationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationCleanupScheduler {
    
    private final IReservationService reservationService;
    
    /**
     * Runs every hour to clean up expired reservations.
     * Uses database-level locking through the UPDATE query,
     * so multiple pods running simultaneously will not cause issues.
     */
    @Scheduled(cron = "0 0 * * * *")
    public void cleanupExpiredReservations() {
        log.info("Starting expired reservations cleanup...");
        try {
            reservationService.cleanupExpiredReservations();
            log.info("Expired reservations cleanup completed successfully");
        } catch (Exception e) {
            log.error("Error during reservation cleanup: {}", e.getMessage(), e);
        }
    }
}

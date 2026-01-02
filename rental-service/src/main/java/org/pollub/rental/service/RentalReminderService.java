package org.pollub.rental.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pollub.rental.client.CatalogServiceClient;
import org.pollub.rental.client.UserServiceClient;
import org.pollub.rental.model.RentalHistory;
import org.pollub.rental.model.RentalStatus;
import org.pollub.rental.repository.IRentalHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Service for sending rental reminder emails to users
 * whose rentals expire in 3 days.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RentalReminderService {

    private static final int DAYS_BEFORE_DUE = 3;

    private final IRentalHistoryRepository rentalHistoryRepository;
    private final UserServiceClient userServiceClient;
    private final CatalogServiceClient catalogServiceClient;
    private final EmailService emailService;

    /**
     * Find all rentals expiring in 3 days and send reminder emails.
     */
    public void sendReminders() {
        log.info("Starting rental reminder job...");

        // Calculate date range for rentals due in 3 days
        LocalDate targetDate = LocalDate.now().plusDays(DAYS_BEFORE_DUE);
        LocalDateTime startOfDay = targetDate.atStartOfDay();
        LocalDateTime endOfDay = targetDate.atTime(LocalTime.MAX);

        List<RentalHistory> expiringRentals = rentalHistoryRepository
                .findByDueDateBetweenAndStatus(startOfDay, endOfDay, RentalStatus.RENTED);

        log.info("Found {} rentals expiring in {} days", expiringRentals.size(), DAYS_BEFORE_DUE);

        int successCount = 0;
        int failCount = 0;

        for (RentalHistory rental : expiringRentals) {
            try {
                // Get user email
                String email = userServiceClient.getUserEmail(rental.getUserId());
                if (email == null) {
                    log.warn("Could not find email for user {}, skipping reminder for rental {}",
                            rental.getUserId(), rental.getId());
                    failCount++;
                    continue;
                }

                // Get item title
                String itemTitle = getItemTitle(rental.getItemId());

                // Send reminder email
                emailService.sendRentalReminderEmail(email, itemTitle, rental.getDueDate());
                successCount++;

            } catch (Exception e) {
                log.error("Error processing reminder for rental {}: {}", rental.getId(), e.getMessage());
                failCount++;
            }
        }

        log.info("Rental reminder job completed. Sent: {}, Failed: {}", successCount, failCount);
    }

    private String getItemTitle(Long itemId) {
        try {
            var item = catalogServiceClient.getItemById(itemId);
            return item != null ? item.getTitle() : "Nieznany tytuł";
        } catch (Exception e) {
            log.warn("Could not fetch title for item {}: {}", itemId, e.getMessage());
            return "Nieznany tytuł";
        }
    }
}

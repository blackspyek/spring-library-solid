package org.pollub.library.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pollub.library.item.model.LibraryItem;
import org.pollub.library.item.repository.ILibraryItemRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReminderService {

    private final ILibraryItemRepository<LibraryItem> libraryItemRepository;
    private final EmailService emailService;

    @Value("${reminder.days-before:3}")
    private int daysBefore;

    @Transactional
    public void processReturnReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime targetDateStart = now.plusDays(daysBefore).with(LocalTime.MIN);
        LocalDateTime targetDateEnd = now.plusDays(daysBefore).with(LocalTime.MAX);

        log.info("Processing return reminders for items due between {} and {}", targetDateStart, targetDateEnd);

        List<LibraryItem> itemsDue = libraryItemRepository.findItemsDueForReminder(targetDateStart, targetDateEnd);

        log.info("Found {} items requiring reminder", itemsDue.size());

        int successCount = 0;
        int failCount = 0;

        for (LibraryItem item : itemsDue) {
            try {
                emailService.sendReturnReminder(item.getRentedByUser(), item);
                item.setReminderSentAt(LocalDateTime.now());
                libraryItemRepository.save(item);
                successCount++;
            } catch (Exception e) {
                log.error("Failed to process reminder for item {}: {}", item.getId(), e.getMessage());
                failCount++;
            }
        }

        log.info("Reminder processing completed. Success: {}, Failed: {}", successCount, failCount);
    }
}

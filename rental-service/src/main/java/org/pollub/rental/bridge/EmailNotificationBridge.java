package org.pollub.rental.bridge;

//START L2 Bridge Implementation
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pollub.rental.service.EmailService;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

/**
 * Email-based implementation of INotificationBridge.
 * Uses EmailService to send rental reminders via email.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationBridge implements INotificationBridge {
    
    private final EmailService emailService;
    
    @Override
    public void sendRentalReminder(String recipient, String itemTitle, LocalDateTime dueDate) {
        log.info("Sending email reminder to {} for item: {}", recipient, itemTitle);
        emailService.sendRentalReminderEmail(recipient, itemTitle, dueDate);
    }
    
    @Override
    public String getNotificationType() {
        return "EMAIL";
    }
}
//END L2 Bridge Implementation
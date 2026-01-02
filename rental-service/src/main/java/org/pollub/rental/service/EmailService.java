package org.pollub.rental.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Service for sending rental reminder emails.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${mail.from:noreply@library.local}")
    private String fromAddress;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    /**
     * Send a rental reminder email to the user.
     *
     * @param email    recipient email address
     * @param itemTitle title of the rented item
     * @param dueDate  due date of the rental
     */
    public void sendRentalReminderEmail(String email, String itemTitle, LocalDateTime dueDate) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Przypomnienie o zwrocie - " + itemTitle);
            message.setText(buildReminderEmailBody(itemTitle, dueDate));
            message.setFrom(fromAddress);

            mailSender.send(message);
            log.info("Rental reminder email sent to: {} for item: {}", email, itemTitle);
        } catch (Exception e) {
            log.error("Failed to send rental reminder email to: {} for item: {}", email, itemTitle, e);
            // Don't throw - continue processing other reminders
        }
    }

    private String buildReminderEmailBody(String itemTitle, LocalDateTime dueDate) {
        return "Szanowny Czytelniku,\n\n" +
                "Przypominamy, że za 3 dni upływa termin zwrotu wypożyczonej pozycji:\n\n" +
                "Tytuł: " + itemTitle + "\n" +
                "Termin zwrotu: " + dueDate.format(DATE_FORMATTER) + "\n\n" +
                "Prosimy o terminowy zwrot lub przedłużenie wypożyczenia w systemie bibliotecznym.\n\n" +
                "W przypadku pytań, prosimy o kontakt z biblioteką.\n\n" +
                "Pozdrawiamy,\n" +
                "System Biblioteczny";
    }
}

package org.pollub.library.notification;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pollub.library.item.model.LibraryItem;
import org.pollub.library.user.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${reminder.from-email}")
    private String fromEmail;

    public void sendReturnReminder(User user, LibraryItem item) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(user.getEmail());
            helper.setSubject("Przypomnienie o terminie zwrotu: " + item.getTitle());

            Context context = new Context();
            context.setVariable("userName", user.getName() != null ? user.getName() : user.getUsername());
            context.setVariable("bookTitle", item.getTitle());
            context.setVariable("dueDate", item.getDueDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            context.setVariable("dueDaysLeft", 3);

            String htmlContent = templateEngine.process("return-reminder", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Reminder email sent to {} for item: {}", user.getEmail(), item.getTitle());

        } catch (MessagingException e) {
            log.error("Failed to send reminder email to {}: {}", user.getEmail(), e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }
}

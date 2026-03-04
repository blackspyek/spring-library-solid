package org.pollub.auth.adapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

//start L2 Adapter Implementation
/**
 * Spring Mail implementation of IMailAdapter.
 * Adapts JavaMailSender to IMailAdapter interface.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SpringMailAdapter implements IMailAdapter {

    private final JavaMailSender mailSender;

    @Override
    public void sendEmail(String to, String subject, String body, String from) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom(from);

            mailSender.send(message);
            log.info("Email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
//end L2 Adapter Implementation
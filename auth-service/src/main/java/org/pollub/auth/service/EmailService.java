package org.pollub.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service for sending emails to users.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    /**
     * Sends a temporary password email to the user.
     */
    public void sendTemporaryPasswordEmail(String email, String password) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Rejestracja w bibliotece - tymczasowe hasło");
            message.setText(buildPasswordEmailBody(email, password));
            message.setFrom("library-system@library.local");
            
            mailSender.send(message);
            log.info("Temporary password email sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send temporary password email to: {}", email, e);
            // Don't throw exception - user is created even if email fails
        }
    }

    /**
     * Sends a password reset email to the user with the new temporary password.
     */
    public void sendPasswordResetEmail(String email, String newPassword) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Resetowanie hasła - System Biblioteczny");
            message.setText(buildPasswordResetEmailBody(email, newPassword));
            message.setFrom("library-system@library.local");
            
            mailSender.send(message);
            log.info("Password reset email sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", email, e);
            throw new RuntimeException("Nie udało się wysłać emaila z nowym hasłem. Spróbuj ponownie później.");
        }
    }
    
    private String buildPasswordEmailBody(String email, String password) {
        return "Witaj!\n\n" +
                "Twoje konto w systemie bibliotecznym zostało utworzone.\n\n" +
                "Dane logowania:\n" +
                "Email (Login): " + email + "\n" +
                "Hasło tymczasowe: " + password + "\n\n" +
                "Po pierwszym logowaniu będziesz zmuszony zmienić hasło na bezpieczne.\n\n" +
                "Link do logowania: https://library.local\n\n" +
                "Jeśli nie rejestrowałeś się, zignoruj tę wiadomość.\n\n" +
                "Pozdrawiamy,\n" +
                "System Biblioteczny";
    }

    private String buildPasswordResetEmailBody(String email, String newPassword) {
        return "Witaj!\n\n" +
                "Otrzymaliśmy prośbę o zresetowanie hasła do Twojego konta w systemie bibliotecznym.\n\n" +
                "Twoje nowe dane logowania:\n" +
                "Email (Login): " + email + "\n" +
                "Nowe hasło tymczasowe: " + newPassword + "\n\n" +
                "Po zalogowaniu się będziesz zobowiązany do zmiany hasła na własne.\n\n" +
                "Jeśli nie prosiłeś o zresetowanie hasła, skontaktuj się z administratorem systemu.\n\n" +
                "Pozdrawiamy,\n" +
                "System Biblioteczny";
    }
}

package org.pollub.auth.adapter;

//start L2 Adapter Interface
/**
 * Adapter interface for mail sending abstraction.
 * Allows switching between different mail providers without changing EmailService.
 */
public interface IMailAdapter {
    /**
     * Sends an email message.
     * @param to recipient email address
     * @param subject email subject
     * @param body email body (text)
     * @param from sender email address
     */
    void sendEmail(String to, String subject, String body, String from);
}
//end L2 Adapter Interface
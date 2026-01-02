package org.pollub.rental.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClient for communicating with user-service to fetch user emails.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserServiceClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${services.user.url:http://user-service}")
    private String userServiceUrl;

    /**
     * Get user email by user ID.
     * 
     * @param userId the user ID
     * @return user email or null if not found
     */
    public String getUserEmail(Long userId) {
        try {
            UserEmailResponse response = webClientBuilder.build()
                    .get()
                    .uri(userServiceUrl + "/api/users/{id}", userId)
                    .retrieve()
                    .bodyToMono(UserEmailResponse.class)
                    .block();
            return response != null ? response.getEmail() : null;
        } catch (Exception e) {
            log.warn("Failed to get email for user {}: {}", userId, e.getMessage());
            return null;
        }
    }

    /**
     * Simple DTO for extracting email from user response.
     */
    @lombok.Data
    public static class UserEmailResponse {
        private Long id;
        private String email;
        private String firstName;
        private String lastName;
    }
}

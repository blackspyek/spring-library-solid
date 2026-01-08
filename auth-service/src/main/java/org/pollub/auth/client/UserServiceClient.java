package org.pollub.auth.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pollub.common.dto.BranchDto;
import org.pollub.common.dto.UserDto;
import org.pollub.common.exception.ServiceException;
import org.pollub.common.exception.UserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * WebClient for communicating with user-service.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserServiceClient {
    
    private final WebClient.Builder webClientBuilder;
    
    @Value("${services.user.url:http://user-service}")
    private String userServiceUrl;
    
    public Optional<UserDto> findByEmail(String email) {
        try {
            UserDto user = webClientBuilder.build()
                    .get()
                    .uri(userServiceUrl + "/api/users/email/{email}", email)
                    .retrieve()
                    .bodyToMono(UserDto.class)
                    .block();
            return Optional.ofNullable(user);
        } catch (WebClientResponseException.NotFound e) {
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error fetching user by email", e);
            throw new ServiceException("user-service", "Failed to fetch user by email", e);
        }
    }
    
    public Optional<UserDto> findByUsername(String username) {
        try {
            UserDto user = webClientBuilder.build()
                    .get()
                    .uri(userServiceUrl + "/api/users/username/{username}", username)
                    .retrieve()
                    .bodyToMono(UserDto.class)
                    .block();
            return Optional.ofNullable(user);
        } catch (WebClientResponseException.NotFound e) {
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error fetching user by username", e);
            throw new ServiceException("user-service", "Failed to fetch user by username", e);
        }
    }

    public UserDto createUser(UserDto userDto) {
        return webClientBuilder.build()
                .post()
                .uri(userServiceUrl + "/api/users")
                .bodyValue(userDto)
                .retrieve()
                .onStatus(
                        status -> status.isSameCodeAs(HttpStatus.CONFLICT),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorMessage -> Mono.error(new UserAlreadyExistsException(errorMessage)))
                )
                .bodyToMono(UserDto.class)
                .block();
    }
    
    public Optional<UserDto> validateCredentials(String usernameOrEmail, String password) {
        try {
            UserDto user = webClientBuilder.build()
                    .post()
                    .uri(userServiceUrl + "/api/users/validate")
                    .bodyValue(new CredentialsDto(usernameOrEmail, password))
                    .retrieve()
                    .bodyToMono(UserDto.class)
                    .block();
            return Optional.ofNullable(user);
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return Optional.empty();
            }
            log.error("Error validating credentials", e);
            throw new ServiceException("user-service", "Failed to validate credentials", e);
        } catch (Exception e) {
            log.error("Error validating credentials", e);
            throw new ServiceException("user-service", "Failed to validate credentials", e);
        }
    }
    
    // Simple DTO for credentials
    public record CredentialsDto(String usernameOrEmail, String password) {}

    public Optional<BranchDto> getEmployeeBranch(String username) {
        try {
            BranchDto branch = webClientBuilder.build()
                    .get()
                    .uri(userServiceUrl + "/api/users/username/{username}/branch", username)
                    .retrieve()
                    .bodyToMono(BranchDto.class)
                    .block();
            return Optional.ofNullable(branch);
        } catch (Exception e) {
            log.error("Error fetching employee branch for username: {}", username, e);
            return Optional.empty();
        }
    }

    /**
     * Reset password for a user identified by email and PESEL.
     * Returns the new temporary password if successful.
     */
    public Optional<ResetPasswordResponseDto> resetPassword(String email, String pesel) {
        try {
            ResetPasswordResponseDto response = webClientBuilder.build()
                    .post()
                    .uri(userServiceUrl + "/api/users/reset-password")
                    .bodyValue(new ResetPasswordRequestDto(email, pesel))
                    .retrieve()
                    .bodyToMono(ResetPasswordResponseDto.class)
                    .block();
            return Optional.ofNullable(response);
        } catch (Exception e) {
            log.error("Error resetting password for email: {}", email, e);
            return Optional.empty();
        }
    }

    // DTOs for reset password
    public record ResetPasswordRequestDto(String email, String pesel) {}

    public record ResetPasswordResponseDto(String email, String temporaryPassword, boolean success, String message) {}
}

package org.pollub.branch.client;

import lombok.RequiredArgsConstructor;
import org.pollub.common.dto.UserDto;
import org.pollub.common.exception.ServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

/**
 * WebClient for communicating with user-service.
 */
@Component
@RequiredArgsConstructor
public class UserServiceClient {
    
    private final WebClient.Builder webClientBuilder;
    
    @Value("${services.user.url:http://user-service}")
    private String userServiceUrl;
    
    public List<UserDto> getEmployeesByBranch(Long branchId) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri(userServiceUrl + "/api/users/branch/{branchId}/employees", branchId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<UserDto>>() {})
                    .block();
        } catch (Exception e) {
            throw new ServiceException("user-service", "Failed to get employees for branch " + branchId, e);
        }
    }
    
    public UserDto getUserById(Long userId) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri(userServiceUrl + "/api/users/{id}", userId)
                    .retrieve()
                    .bodyToMono(UserDto.class)
                    .block();
        } catch (Exception e) {
            throw new ServiceException("user-service", "Failed to get user " + userId, e);
        }
    }
}

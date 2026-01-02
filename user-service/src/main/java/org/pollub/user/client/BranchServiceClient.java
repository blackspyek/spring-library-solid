package org.pollub.user.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pollub.common.dto.BranchDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class BranchServiceClient {
    
    private final WebClient.Builder webClientBuilder;
    
    @Value("${services.branch.url:http://branch-service}")
    private String branchServiceUrl;
    
    public Optional<BranchDto> getBranchById(Long id) {
        try {
            BranchDto branch = webClientBuilder.build()
                    .get()
                    .uri(branchServiceUrl + "/api/branches/" + id)
                    .retrieve()
                    .bodyToMono(BranchDto.class)
                    .block();
            return Optional.ofNullable(branch);
        } catch (Exception e) {
            log.error("Error fetching branch with id: {}", id, e);
            return Optional.empty();
        }
    }
}

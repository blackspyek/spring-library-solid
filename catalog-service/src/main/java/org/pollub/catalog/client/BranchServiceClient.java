package org.pollub.catalog.client;

import lombok.RequiredArgsConstructor;
import org.pollub.common.exception.ServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class BranchServiceClient {
    
    private final WebClient.Builder webClientBuilder;
    
    @Value("${services.branch.url:http://branch-service}")
    private String branchServiceUrl;
    
    /**
     * Fetch branch information by ID.
     * @param branchId the branch ID
     * @return BranchResponse with branch details (name, address, city)
     */
    public BranchResponse getBranchById(Long branchId) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri(branchServiceUrl + "/api/branches/{id}", branchId)
                    .retrieve()
                    .bodyToMono(BranchResponse.class)
                    .block();
        } catch (Exception e) {
            throw new ServiceException("branch-service", "Failed to get branch " + branchId, e);
        }
    }

    /**
     * Fetch branch information for multiple branch IDs in a single request.
     * @param branchIds list of branch IDs
     * @return Map of branchId -> BranchResponse
     */
    public Map<Long, BranchResponse> getBranchesByIds(List<Long> branchIds) {
        try {
            return webClientBuilder.build()
                    .post()
                    .uri(branchServiceUrl + "/api/branches/batch")
                    .bodyValue(branchIds)
                    .retrieve()
                    .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<Long, BranchResponse>>() {})
                    .block();
        } catch (Exception e) {
            throw new ServiceException("branch-service", "Failed to get branches by ids", e);
        }
    }
}

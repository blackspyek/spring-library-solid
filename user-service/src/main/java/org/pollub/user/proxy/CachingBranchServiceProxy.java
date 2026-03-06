package org.pollub.user.proxy;

import lombok.extern.slf4j.Slf4j;
import org.pollub.common.dto.BranchDto;
import org.pollub.user.client.IBranchServiceClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Caching Proxy (wzorzec Proxy) dla IBranchServiceClient.
 * Cachuje wyniki getBranchById() w pamięci, eliminując powtarzalne
 * wywołania HTTP do branch-service dla tych samych oddziałów.
 */
//Lab1 - Proxy 3 Start
@Component
@Primary
@Slf4j
public class CachingBranchServiceProxy implements IBranchServiceClient {

    private final IBranchServiceClient branchServiceClient;
    private final ConcurrentHashMap<Long, Optional<BranchDto>> cache = new ConcurrentHashMap<>();

    public CachingBranchServiceProxy(@Qualifier("branchServiceClient") IBranchServiceClient branchServiceClient) {
        this.branchServiceClient = branchServiceClient;
    }

    @Override
    public Optional<BranchDto> getBranchById(Long id) {
        return cache.computeIfAbsent(id, key -> {
            log.debug("Cache miss for branch id: {}, fetching from branch-service", key);
            return branchServiceClient.getBranchById(key);
        });
    }
}
//Lab1 - Proxy 3 End

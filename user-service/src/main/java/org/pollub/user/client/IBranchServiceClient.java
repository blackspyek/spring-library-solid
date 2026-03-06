package org.pollub.user.client;

import org.pollub.common.dto.BranchDto;

import java.util.Optional;

/**
 * Interfejs dla klienta serwisu oddziałów.
 * Wymagany przez wzorzec Proxy — pozwala na transparentne opakowanie
 * BranchServiceClient klasą CachingBranchServiceProxy.
 */
public interface IBranchServiceClient {
    Optional<BranchDto> getBranchById(Long id);
}

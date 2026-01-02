package org.pollub.reservation.client;

import lombok.RequiredArgsConstructor;
import org.pollub.common.dto.ItemDto;
import org.pollub.common.dto.ReservationItemDto;
import org.pollub.common.exception.ServiceException;
import org.pollub.reservation.model.dto.ReservationCatalogRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

/**
 * WebClient for communicating with catalog-service.
 */
@Component
@RequiredArgsConstructor
public class CatalogServiceClient {
    
    private final WebClient.Builder webClientBuilder;
    
    @Value("${services.catalog.url:http://catalog-service}")
    private String catalogServiceUrl;

    public ItemDto markAsReserved(ReservationCatalogRequestDto reservation) {
        try {
            return webClientBuilder.baseUrl(catalogServiceUrl).build()
                    .put()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/items/{id}/reserve")
                            .build(reservation.getItemId()))
                    .bodyValue(reservation)
                    .retrieve()
                    .bodyToMono(ItemDto.class)
                    .block();
        } catch (Exception e) {
            throw new ServiceException("catalog-service", "Failed to reserve item " + reservation.getId() + ": " + e.getMessage(), e);
        }
    }

    public List<ReservationItemDto.Item> getItemsInfo(List<Long> itemIds) {
        try{
            return webClientBuilder.baseUrl(catalogServiceUrl).build()
                    .post()
                    .uri("/api/items/info/batch")
                    .bodyValue(itemIds)
                    .retrieve()
                    .bodyToFlux(ReservationItemDto.Item.class)
                    .collectList()
                    .block();
        } catch (Exception e) {
            throw new ServiceException("catalog-service", "Failed to fetch items info: " + e.getMessage(), e);
        }
    }

    public void updateStatus(Long itemId, Long branchId, String status) {
        try {
            webClientBuilder.baseUrl(catalogServiceUrl).build()
                    .put()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/items/{itemId}/inventory/{branchId}/status")
                            .build(itemId, branchId))
                    .bodyValue(Map.of("status", status))
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (Exception e) {
            throw new ServiceException("catalog-service",
                    "Failed to update status for item " + itemId + " in branch " + branchId, e);
        }
    }
}

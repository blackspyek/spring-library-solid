package org.pollub.rental.client;

import lombok.RequiredArgsConstructor;
import org.pollub.common.dto.ItemDto;
import org.pollub.common.dto.RentalHistoryDto;
import org.pollub.common.dto.ReservationResponse;
import org.pollub.common.exception.ServiceException;
import org.pollub.rental.model.dto.HistoryCatalogResponse;
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
    
    public ItemDto getItemById(Long itemId) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri(catalogServiceUrl + "/api/items/{id}", itemId)
                    .retrieve()
                    .bodyToMono(ItemDto.class)
                    .block();
        } catch (Exception e) {
            throw new ServiceException("catalog-service", "Failed to get item " + itemId, e);
        }
    }
    
    public ReservationResponse markAsRented(RentalHistoryDto rentalHistoryDto) {
        try {
            return webClientBuilder.build()
                    .put()
                    .uri(catalogServiceUrl + "/api/items/{id}/rent", rentalHistoryDto.getItemId())
                    .bodyValue(rentalHistoryDto)
                    .retrieve()
                    .bodyToMono(ReservationResponse.class)
                    .block();
        } catch (Exception e) {
            throw new ServiceException("catalog-service", "Failed to rent item " + rentalHistoryDto.getId(), e);
        }
    }
    
    /**
     * Mark an item as returned at a specific branch.
     * @param itemId the item ID
     * @param branchId the branch where the item was rented from
     */
    public void markAsReturned(Long itemId, Long branchId) {
        try {
            webClientBuilder.build()
                    .put()
                    .uri(catalogServiceUrl + "/api/items/{id}/return?branchId={branchId}", itemId, branchId)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (Exception e) {
            throw new ServiceException("catalog-service", "Failed to return item " + itemId, e);
        }
    }
    
    /**
     * Extend rental for an item at a specific branch.
     *
     * @param itemId   the item ID
     * @param branchId the branch where the item was rented from
     * @param days     number of days to extend
     */
    public void extendRental(Long itemId, Long branchId, int days) {
        try {
            webClientBuilder.build()
                    .put()
                    .uri(catalogServiceUrl + "/api/items/{id}/extend?branchId={branchId}&days={days}", itemId, branchId, days)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (Exception e) {
            throw new ServiceException("catalog-service", "Failed to extend rental for item " + itemId, e);
        }
    }
    
    public List<ItemDto> getItemsByUser(Long userId) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri(catalogServiceUrl + "/api/items/user/{userId}", userId)
                    .retrieve()
                    .bodyToFlux(ItemDto.class)
                    .collectList()
                    .block();
        } catch (Exception e) {
            throw new ServiceException("catalog-service", "Failed to get items for user " + userId, e);
        }
    }

    /**
     * Fetch history catalog data for a list of item IDs.
     * Returns enriched data with item titles, authors, branch names and addresses.
     *
     * @param itemIds list of item IDs to fetch data for
     * @return map of itemId -> HistoryCatalogResponse
     */
    public Map<Long, HistoryCatalogResponse> getHistoryCatalogDataByItemIds(List<Long> itemIds) {
        try {
            return webClientBuilder.build()
                    .post()
                    .uri(catalogServiceUrl + "/api/items/history-data")
                    .bodyValue(itemIds)
                    .retrieve()
                    .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<Long, HistoryCatalogResponse>>() {})
                    .block();
        } catch (Exception e) {
            throw new ServiceException("catalog-service", "Failed to get history catalog data for items", e);
        }
    }

}

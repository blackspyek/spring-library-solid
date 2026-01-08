package org.pollub.catalog.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Client for communicating with reservation-service.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationServiceClient {
    
    private final WebClient.Builder webClientBuilder;
    
    @Value("${services.reservation.url:http://reservation-service}")
    private String reservationServiceUrl;
    
    /**
     * Mark a reservation as fulfilled when the reserved book is rented.
     * This is called when a book that was reserved is being rented by the user who reserved it.
     * 
     * @param itemId the item ID
     * @param branchId the branch ID
     * @param userId the user ID who made the reservation
     */
    public void fulfillReservation(Long itemId, Long branchId, Long userId) {
        try {
            webClientBuilder.build()
                    .put()
                    .uri(reservationServiceUrl + "/api/reservations/fulfill?itemId={itemId}&branchId={branchId}&userId={userId}",
                            itemId, branchId, userId)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
            log.info("Reservation fulfilled for itemId: {}, branchId: {}, userId: {}", itemId, branchId, userId);
        } catch (Exception e) {
            log.error("Failed to fulfill reservation for itemId: {}, branchId: {}, userId: {}. Error: {}", 
                    itemId, branchId, userId, e.getMessage());
            throw new RuntimeException("Reservation fulfillment failed: " + e.getMessage(), e);
        }
    }
}

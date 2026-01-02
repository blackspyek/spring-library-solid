package org.pollub.reservation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pollub.common.dto.ItemDto;
import org.pollub.common.dto.ReservationItemDto;
import org.pollub.reservation.client.CatalogServiceClient;
import org.pollub.reservation.model.ReservationHistory;
import org.pollub.reservation.model.ReservationStatus;
import org.pollub.reservation.model.dto.ReservationCatalogRequestDto;
import org.pollub.reservation.model.dto.ReservationDto;
import org.pollub.reservation.repository.ReservationRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ReservationService implements  IReservationService {
    
    private static final int RESERVATION_DAYS = 3;
    private static final int MAX_ACTIVE_RESERVATIONS = 3;
    
    private final ReservationRepository reservationRepository;
    private final CatalogServiceClient catalogServiceClient;

    @Override
    @Transactional
    public ItemDto createReservation(ReservationDto dto, Long userId) {
        // Check active reservations count
        long activeCount = reservationRepository.countByUserIdAndStatus(userId, ReservationStatus.ACTIVE);
        if (activeCount >= MAX_ACTIVE_RESERVATIONS) {
            throw new IllegalStateException("User has reached the maximum number of active reservations");
        }

        // Create reservation
        ReservationHistory reservation = ReservationHistory.builder()
                .itemId(dto.getItemId())
                .branchId(dto.getBranchId())
                .userId(userId)
                .reservedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(RESERVATION_DAYS))
                .status(ReservationStatus.ACTIVE)
                .build();

        try {
            reservationRepository.save(reservation);
        } catch (Exception e) {
            log.error("Failed to update item status in catalog service for reservation {}", reservation.getId(), e);
            throw new RuntimeException("Failed to update item status in catalog service {}", e);
        }
        return catalogServiceClient.markAsReserved(
                toReservationCatalogRequestDto(reservation)
        );

    }
    private ReservationCatalogRequestDto toReservationCatalogRequestDto(
            ReservationHistory reservation
    ) {
        return ReservationCatalogRequestDto.builder()
                .id(reservation.getId())
                .itemId(reservation.getItemId())
                .userId(reservation.getUserId())
                .branchId(reservation.getBranchId())
                .reservedAt(reservation.getReservedAt())
                .expiresAt(reservation.getExpiresAt())
                .resolvedAt(reservation.getResolvedAt())
                .status(reservation.getStatus().name())
                .build();

    }

    @Override
    public List<ReservationItemDto> getReservationsByUsername(Long userId) {
        List<ReservationHistory> reservations = reservationRepository.findByUserIdAndStatusOrderByReservedAtDesc(userId, ReservationStatus.ACTIVE);

        List<Long> itemIds = reservations.stream()
                .map(ReservationHistory::getItemId)
                .distinct()
                .toList();

        List<ReservationItemDto.Item> items = catalogServiceClient.getItemsInfo(itemIds);
        return reservations.stream()
                .map(reservation -> {
                    ReservationItemDto.Item item = items.stream()
                            .filter(i -> i.getId().equals(reservation.getItemId()))
                            .findFirst()
                            .orElseThrow(() -> new IllegalStateException("Item info not found for itemId: " + reservation.getItemId()));
                    return toReservationItemDto(reservation, item);
                })
                .toList();

    }

    @Override
    @Transactional
    public void cancelReservation(Long id, Long userId) throws AccessDeniedException {
        ReservationHistory reservation = reservationRepository.findById(id).orElseThrow(() ->
                new IllegalStateException("Reservation not found: " + id)
        );
        if (!reservation.getUserId().equals(userId)) {
            throw new AccessDeniedException(
                    "User " + userId + " is not authorized to cancel reservation " + id
            );
        }
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setResolvedAt(LocalDateTime.now());
        reservationRepository.save(reservation);

        catalogServiceClient.updateStatus(
                reservation.getItemId(),
                reservation.getBranchId(),
                "AVAILABLE"
        );

    }

    private ReservationItemDto toReservationItemDto(ReservationHistory reservation, ReservationItemDto.Item item) {
        ReservationItemDto.Item itemDto = ReservationItemDto.Item.builder()
                .id(item.getId())
                .title(item.getTitle())
                .imageUrl(item.getImageUrl())
                .build();

        return ReservationItemDto.builder()
                .id(reservation.getId())
                .branchId(reservation.getBranchId())
                .expiresAt(reservation.getExpiresAt())
                .item(itemDto)
                .build();
    }


    @Override
    @Transactional
    public void cleanupExpiredReservations() {
        List<ReservationHistory> expired = reservationRepository
                .findByExpiresAtBeforeAndStatus(LocalDateTime.now(), ReservationStatus.ACTIVE);

        for (ReservationHistory reservation : expired) {
            reservation.setStatus(ReservationStatus.EXPIRED);
            reservation.setResolvedAt(LocalDateTime.now());
            reservationRepository.save(reservation);


            catalogServiceClient.updateStatus(
                    reservation.getItemId(),
                    reservation.getBranchId(),
                    "AVAILABLE"
            );

        }

        log.info("Processed {} expired reservations", expired.size());
    }


}

package org.pollub.library.reservation.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.pollub.library.branch.model.LibraryBranch;
import org.pollub.library.branch.repository.ILibraryBranchRepository;
import org.pollub.library.exception.ReservationException;
import org.pollub.library.item.model.ItemStatus;
import org.pollub.library.item.model.LibraryItem;
import org.pollub.library.item.repository.ILibraryItemRepository;
import org.pollub.library.reservation.model.ReservationHistory;
import org.pollub.library.reservation.model.ReservationStatus;
import org.pollub.library.reservation.model.dto.ReservationDto;
import org.pollub.library.reservation.repository.IReservationHistoryRepository;
import org.pollub.library.user.model.User;
import org.pollub.library.user.service.IUserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService implements IReservationService {
    
    private static final int MAX_RESERVATIONS_PER_USER = 5;
    private static final int RESERVATION_DAYS = 2;
    
    private final ILibraryItemRepository<LibraryItem> libraryItemRepository;
    private final IReservationHistoryRepository reservationHistoryRepository;
    private final ILibraryBranchRepository libraryBranchRepository;
    private final IUserService userService;
    
    @Override
    public ReservationHistory createReservation(ReservationDto dto, Long userId) {
        User user = userService.findById(userId);
        LibraryItem item = getLibraryItemOrThrow(dto.getLibraryItemId());
        LibraryBranch branch = getBranchOrThrow(dto.getBranchId());
        
        validateReservation(user, item, branch);
        
        // Update item status
        item.setStatus(ItemStatus.RESERVED);
        libraryItemRepository.save(item);
        
        // Create reservation history
        LocalDateTime now = LocalDateTime.now();
        ReservationHistory reservation = ReservationHistory.builder()
                .item(item)
                .user(user)
                .branch(branch)
                .reservedAt(now)
                .expiresAt(now.plusDays(RESERVATION_DAYS))
                .status(ReservationStatus.RUNNING)
                .build();
        
        return reservationHistoryRepository.save(reservation);
    }
    
    @Override
    public LibraryItem cancelReservation(Long itemId, Long userId) {
        ReservationHistory reservation = reservationHistoryRepository
                .findByItemIdAndStatus(itemId, ReservationStatus.RUNNING)
                .orElseThrow(() -> new ReservationException("No active reservation found for this item"));
        
        if (!reservation.getUser().getId().equals(userId)) {
            throw new ReservationException("You can only cancel your own reservations");
        }
        
        // Update reservation status
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setResolvedAt(LocalDateTime.now());
        reservationHistoryRepository.save(reservation);
        
        // Update item status
        LibraryItem item = reservation.getItem();
        item.setStatus(ItemStatus.AVAILABLE);
        return libraryItemRepository.save(item);
    }
    
    @Override
    public List<ReservationHistory> getUserReservations(Long userId) {
        return reservationHistoryRepository.findByUserIdAndStatus(userId, ReservationStatus.RUNNING);
    }
    
    @Override
    public long getUserReservationCount(Long userId) {
        return reservationHistoryRepository.countByUserIdAndStatus(userId, ReservationStatus.RUNNING);
    }
    
    @Override
    public void resolveReservation(Long itemId) {
        reservationHistoryRepository.findByItemIdAndStatus(itemId, ReservationStatus.RUNNING)
                .ifPresent(reservation -> {
                    reservation.setStatus(ReservationStatus.RESOLVED);
                    reservation.setResolvedAt(LocalDateTime.now());
                    reservationHistoryRepository.save(reservation);
                });
    }
    
    @Override
    public void cleanupExpiredReservations() {
        LocalDateTime now = LocalDateTime.now();
        
        // Find expired reservations
        List<ReservationHistory> expiredReservations = reservationHistoryRepository
                .findByExpiresAtBeforeAndStatus(now, ReservationStatus.RUNNING);
        
        for (ReservationHistory reservation : expiredReservations) {
            // Update item status to available
            LibraryItem item = reservation.getItem();
            if (item.getStatus() == ItemStatus.RESERVED) {
                item.setStatus(ItemStatus.AVAILABLE);
                libraryItemRepository.save(item);
            }
            
            // Update reservation status
            reservation.setStatus(ReservationStatus.EXPIRED);
            reservation.setResolvedAt(now);
            reservationHistoryRepository.save(reservation);
        }
    }
    
    private void validateReservation(User user, LibraryItem item, LibraryBranch branch) {
        // Check max reservations limit
        long currentCount = reservationHistoryRepository.countByUserIdAndStatus(
                user.getId(), ReservationStatus.RUNNING);
        if (currentCount >= MAX_RESERVATIONS_PER_USER) {
            throw new ReservationException(
                    "Maksymalnie możesz zarezerwować " + MAX_RESERVATIONS_PER_USER + " książek"
            );
        }
        
        // Check item availability
        if (item.getStatus() != ItemStatus.AVAILABLE) {
            throw new ReservationException("Ta książka nie jest dostępna do rezerwacji");
        }
        
        // Check if item is available at selected branch
        if (!item.getAvailableAtBranches().contains(branch)) {
            throw new ReservationException("Ta książka nie jest dostępna w wybranej bibliotece");
        }
    }
    
    private LibraryItem getLibraryItemOrThrow(Long itemId) {
        return libraryItemRepository.findById(itemId)
                .orElseThrow(() -> new ReservationException("Książka nie została znaleziona"));
    }
    
    private LibraryBranch getBranchOrThrow(Long branchId) {
        return libraryBranchRepository.findById(branchId)
                .orElseThrow(() -> new ReservationException("Biblioteka nie została znaleziona"));
    }
}

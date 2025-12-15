package org.pollub.library.item.repository;

import org.pollub.library.item.model.ItemStatus;
import org.pollub.library.item.model.LibraryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ILibraryItemRepository<T extends LibraryItem> extends JpaRepository<T, Long> {
    List<T> findByRentedByUserId(Long userId);
    List<T> findByStatus(ItemStatus status);

    @Query("SELECT li FROM LibraryItem li JOIN FETCH li.rentedByUser u " +
           "WHERE li.status = org.pollub.library.item.model.ItemStatus.RENTED " +
           "AND li.reminderSentAt IS NULL " +
           "AND li.dueDate >= :startDate AND li.dueDate < :endDate " +
           "AND u.notificationSettings.returnReminder = true")
    List<LibraryItem> findItemsDueForReminder(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}

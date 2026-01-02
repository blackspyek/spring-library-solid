package org.pollub.rental.repository;

import org.pollub.rental.model.RentalStatus;
import org.pollub.rental.model.RentalHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IRentalHistoryRepository extends JpaRepository<RentalHistory, Long> {
    int countByUserIdAndStatus(Long userId, RentalStatus status);

    long countByItemId(Long itemId);

    Optional<RentalHistory> findByItemIdAndBranchId(Long itemId, Long branchId);

    List<RentalHistory> findByItemId(Long itemId);

    @Query("SELECT rh FROM RentalHistory rh WHERE rh.userId = :userId ORDER BY rh.returnedAt DESC")
    List<RentalHistory> findByUserIdOrderByReturnedAtDesc(@Param("userId") Long userId);

    @Query("SELECT rh FROM RentalHistory rh WHERE rh.userId = :userId ORDER BY rh.returnedAt DESC")
    List<RentalHistory> findByUserIdWithItemOrderByReturnedAtDesc(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT rh FROM RentalHistory rh WHERE rh.userId = :userId AND rh.returnedAt IS NOT NULL ORDER BY rh.returnedAt DESC")
    List<RentalHistory> findCompletedByUserIdOrderByReturnedAtDesc(@Param("userId") Long userId, Pageable pageable);

    @Query(value = """
            SELECT rh.item_id
            FROM rental_history rh
            GROUP BY rh.item_id
            ORDER BY COUNT(rh.id) DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<Long> findTopItemIdsByRentalCount(@Param("limit") int limit);
}

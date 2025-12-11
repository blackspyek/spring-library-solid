package org.pollub.library.rental.repository;

import org.pollub.library.rental.model.RentalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IRentalHistoryRepository extends JpaRepository<RentalHistory, Long> {

    long countByItemId(Long itemId);

    @Query(value = """
            SELECT rh.item_id
            FROM rental_history rh
            GROUP BY rh.item_id
            ORDER BY COUNT(rh.id) DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<Long> findTopItemIdsByRentalCount(@Param("limit") int limit);
}

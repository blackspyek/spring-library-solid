package org.pollub.library.item.repository;

import org.pollub.library.item.model.ItemStatus;
import org.pollub.library.item.model.LibraryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ILibraryItemRepository<T extends LibraryItem> extends JpaRepository<T, Long> {
    List<T> findByRentedByUserId(Long userId);
    List<T> findByStatus(ItemStatus status);

    @Query("SELECT DISTINCT i FROM LibraryItem i JOIN i.availableAtBranches b WHERE i.status = :status AND b.id = :branchId")
    List<T> findByStatusAndBranchId(@Param("status") ItemStatus status, @Param("branchId") Long branchId);
}

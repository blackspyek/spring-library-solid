package org.pollub.catalog.repository;

import org.pollub.catalog.model.BranchInventory;
import org.pollub.catalog.model.CopyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for branch inventory operations.
 */
@Repository
public interface IBranchInventoryRepository extends JpaRepository<BranchInventory, Long> {

    /**
     * Find all inventory records for a specific item.
     */
    List<BranchInventory> findByItemId(Long itemId);

    /**
     * Find inventory for a specific item at a specific branch.
     */
    Optional<BranchInventory> findByItemIdAndBranchId(Long itemId, Long branchId);

    /**
     * Find all inventory at a branch with a specific status.
     */
    List<BranchInventory> findByBranchIdAndStatus(Long branchId, CopyStatus status);

    /**
     * Find all items rented by a specific user.
     */
    List<BranchInventory> findByRentedByUserId(Long userId);

    /**
     * Find all items reserved by a specific user.
     */
    List<BranchInventory> findByReservedByUserId(Long userId);

    /**
     * Get item IDs available at a specific branch.
     */
    @Query("SELECT bi.itemId FROM BranchInventory bi WHERE bi.branchId = :branchId AND bi.status = :status")
    List<Long> findItemIdsByBranchIdAndStatus(@Param("branchId") Long branchId, @Param("status") CopyStatus status);

    /**
     * Check if a copy exists at a branch with a specific status.
     */
    boolean existsByItemIdAndBranchIdAndStatus(Long itemId, Long branchId, CopyStatus status);

    /**
     * Check if any copy of an item exists at a branch.
     */
    boolean existsByItemIdAndBranchId(Long itemId, Long branchId);

    /**
     * Count available copies of an item across all branches.
     */
    @Query("SELECT COUNT(bi) FROM BranchInventory bi WHERE bi.itemId = :itemId AND bi.status = 'AVAILABLE'")
    long countAvailableCopies(@Param("itemId") Long itemId);

    /**
     * Find all branches where an item is available.
     */
    @Query("SELECT bi.branchId FROM BranchInventory bi WHERE bi.itemId = :itemId AND bi.status = 'AVAILABLE'")
    List<Long> findAvailableBranchIds(@Param("itemId") Long itemId);
}

package org.pollub.catalog.repository;

import org.pollub.catalog.model.LibraryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for library items.
 * Note: Status and availability tracking is now handled by BranchInventory.
 */
@Repository
public interface ILibraryItemRepository<T extends LibraryItem> extends JpaRepository<T, Long> {
    // Rental status queries are now in IBranchInventoryRepository
}
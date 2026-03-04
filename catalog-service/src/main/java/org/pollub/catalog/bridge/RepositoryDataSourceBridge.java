package org.pollub.catalog.bridge;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pollub.catalog.model.BranchInventory;
import org.pollub.catalog.model.CopyStatus;
import org.pollub.catalog.model.LibraryItem;
import org.pollub.catalog.repository.IBranchInventoryRepository;
import org.pollub.catalog.repository.ILibraryItemRepository;
import org.springframework.stereotype.Component;

import java.util.List;

//start L2 Bridge implementation
/**
 * Repository-based implementation of IDataSourceBridge.
 * Fetches data from database via repositories.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RepositoryDataSourceBridge implements IDataSourceBridge {
    
    private final ILibraryItemRepository<LibraryItem> libraryItemRepository;
    private final IBranchInventoryRepository branchInventoryRepository;
    
    @Override
    public List<LibraryItem> findAll() {
        log.info("Fetching all items from repository");
        return libraryItemRepository.findAll();
    }
    
    @Override
    public List<LibraryItem> findByBranchId(Long branchId) {
        log.info("Fetching items for branch: {}", branchId);
        
        List<Long> itemIds = branchInventoryRepository.findAll().stream()
                .filter(inv -> inv.getBranchId().equals(branchId))
                .map(BranchInventory::getItemId)
                .distinct()
                .toList();
        
        return libraryItemRepository.findAllById(itemIds);
    }
    
    @Override
    public List<LibraryItem> findAvailable() {
        log.info("Fetching available items from repository");
        
        List<Long> availableItemIds = branchInventoryRepository.findAll().stream()
                .filter(inv -> inv.getStatus() == CopyStatus.AVAILABLE)
                .map(BranchInventory::getItemId)
                .distinct()
                .toList();
        
        return libraryItemRepository.findAllById(availableItemIds);
    }
    
    @Override
    public List<LibraryItem> findRented() {
        log.info("Fetching rented items from repository");
        
        List<Long> rentedItemIds = branchInventoryRepository.findAll().stream()
                .filter(inv -> inv.getStatus() == CopyStatus.RENTED)
                .map(BranchInventory::getItemId)
                .distinct()
                .toList();
        
        return libraryItemRepository.findAllById(rentedItemIds);
    }
    
    @Override
    public List<LibraryItem> findByUserId(Long userId) {
        log.info("Fetching items for user: {}", userId);
        
        List<Long> itemIds = branchInventoryRepository.findByRentedByUserId(userId).stream()
                .map(BranchInventory::getItemId)
                .distinct()
                .toList();
        
        return libraryItemRepository.findAllById(itemIds);
    }
}
//end L2 Bridge implementation
package org.pollub.catalog.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pollub.catalog.client.BranchServiceClient;
import org.pollub.catalog.model.Book;
import org.pollub.catalog.model.BranchInventory;
import org.pollub.catalog.model.CopyStatus;
import org.pollub.catalog.model.LibraryItem;
import org.pollub.catalog.model.dto.HistoryCatalogResponse;
import org.pollub.catalog.repository.IBranchInventoryRepository;
import org.pollub.catalog.repository.ILibraryItemRepository;
import org.pollub.common.dto.ReservationItemDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CatalogService implements ICatalogService {

    private final ILibraryItemRepository<LibraryItem> libraryItemRepository;
    private final IBranchInventoryRepository branchInventoryRepository;
    private final IBranchInventoryService branchInventoryService;
    private final BranchServiceClient branchServiceClient;

    @Override
    public List<LibraryItem> findAll() {
        return libraryItemRepository.findAll();
    }

    @Override
    public LibraryItem findById(Long id) {
        return libraryItemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Item not found: " + id));
    }

    @Override
    public List<LibraryItem> findAvailable() {
        // An item is considered available if it has at least one available copy
        List<Long> availableItemIds = branchInventoryRepository.findAll().stream()
                .filter(inv -> inv.getStatus() == CopyStatus.AVAILABLE)
                .map(BranchInventory::getItemId)
                .distinct()
                .toList();
        
        return libraryItemRepository.findAllById(availableItemIds);
    }

    @Override
    public List<LibraryItem> findRented() {
        List<Long> rentedItemIds = branchInventoryRepository.findAll().stream()
                .filter(inv -> inv.getStatus() == CopyStatus.RENTED)
                .map(BranchInventory::getItemId)
                .distinct()
                .toList();
        
        return libraryItemRepository.findAllById(rentedItemIds);
    }

    @Override
    public List<LibraryItem> findByUserId(Long userId) {
        // Find items rented by this user (from inventory)
        List<Long> itemIds = branchInventoryRepository.findByRentedByUserId(userId).stream()
                .map(BranchInventory::getItemId)
                .distinct()
                .toList();
        
        return libraryItemRepository.findAllById(itemIds);
    }

    @Override
    public List<LibraryItem> findByBranchId(Long branchId) {
        // Find items that have inventory at this branch
        List<Long> itemIds = branchInventoryRepository.findAll().stream()
                .filter(inv -> inv.getBranchId().equals(branchId))
                .map(BranchInventory::getItemId)
                .distinct()
                .toList();
        
        return libraryItemRepository.findAllById(itemIds);
    }

    @Override
    public List<LibraryItem> findAvailableByBranch(Long branchId) {
        List<Long> itemIds = branchInventoryService.getAvailableItemsAtBranch(branchId);
        return libraryItemRepository.findAllById(itemIds);
    }

    @Override
    public List<LibraryItem> searchItems(String query) {
        if (query == null || query.isBlank()) return findAll();
        String q = query.toLowerCase();
        return libraryItemRepository.findAll().stream()
                .filter(i -> containsIgnoreCase(i.getTitle(), q) || containsIgnoreCase(i.getDescription(), q)
                        || (i instanceof Book b && containsIgnoreCase(b.getAuthor(), q))
                        || (i instanceof Book bb && containsIgnoreCase(bb.getIsbn(), q)))
                .collect(Collectors.toList());
    }

    private boolean containsIgnoreCase(String field, String q) {
        return field != null && field.toLowerCase().contains(q);
    }

    @Override
    public List<LibraryItem> findBestsellers() {
        return libraryItemRepository.findAll().stream()
                .filter(LibraryItem::getIsBestseller)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(Long id) {
        if (!libraryItemRepository.existsById(id)) {
            throw new NoSuchElementException("Item not found: " + id);
        }
        libraryItemRepository.deleteById(id);
    }

    @Override
    public List<ReservationItemDto.Item> getItemsForReservation(List<Long> itemIds) {
        List<LibraryItem> items = libraryItemRepository.findAllById(itemIds);
        return items.stream()
                .map(i -> ReservationItemDto.Item.builder()
                        .id(i.getId())
                        .title(i.getTitle())
                        .imageUrl(i.getImageUrl())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public Map<Long, HistoryCatalogResponse> getHistoryCatalogDataByItemIds(List<Long> itemIds) {
        // Get all branch inventory records for the given items
        List<BranchInventory> branchInventories = branchInventoryRepository.findAll().stream()
                .filter(inv -> itemIds.contains(inv.getItemId()))
                .toList();

        // Extract unique branch IDs
        List<Long> branchIds = branchInventories.stream()
                .map(BranchInventory::getBranchId)
                .distinct()
                .toList();

        // Fetch all branch information in one request
        Map<Long, org.pollub.catalog.client.BranchResponse> branchDataMap = new java.util.HashMap<>();
        try {
            if (!branchIds.isEmpty()) {
                Map<Long, org.pollub.catalog.client.BranchResponse> fetchedBranches = branchServiceClient.getBranchesByIds(branchIds);
                if (fetchedBranches != null) {
                    branchDataMap.putAll(fetchedBranches);
                }
            }
        } catch (Exception e) {
            log.warn("Failed to fetch branch data in batch", e);
        }

        // Map to HistoryCatalogResponse with itemId as key
        Map<Long, HistoryCatalogResponse> result = new java.util.HashMap<>();
        for (BranchInventory inv : branchInventories) {
            LibraryItem item = libraryItemRepository.findById(inv.getItemId()).orElse(null);
            if (item == null) {
                continue;
            }

            String itemTitle = item.getTitle();
            String itemAuthor = "-";
            if (item instanceof Book book) {
                itemAuthor = book.getAuthor() != null ? book.getAuthor() : "-";
            }

            // Get branch information from the pre-fetched map
            String branchName = "Brak danych";
            String branchAddress = "Brak danych";
            
            org.pollub.catalog.client.BranchResponse branchResponse = branchDataMap.get(inv.getBranchId());
            if (branchResponse != null) {
                branchName = branchResponse.getName() != null ? branchResponse.getName() : "Brak danych";
                branchAddress = (branchResponse.getAddress() != null ? branchResponse.getAddress() : "Brak danych") +
                        ", " +
                        (branchResponse.getCity() != null ? branchResponse.getCity() : "Brak danych");
            }

            result.put(inv.getItemId(), HistoryCatalogResponse.builder()
                    .itemId(inv.getItemId())
                    .itemTitle(itemTitle)
                    .itemAuthor(itemAuthor)
                    .branchName(branchName)
                    .branchAddress(branchAddress)
                    .build());
        }

        return result;
    }

}

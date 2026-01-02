package org.pollub.catalog.controller;

import lombok.RequiredArgsConstructor;
import org.pollub.catalog.model.Book;
import org.pollub.catalog.model.BranchInventory;
import org.pollub.catalog.model.LibraryItem;
import org.pollub.catalog.model.MovieDisc;
import org.pollub.catalog.model.dto.BranchInventoryDto;
import org.pollub.catalog.model.dto.HistoryCatalogResponse;
import org.pollub.catalog.service.IBranchInventoryService;
import org.pollub.catalog.service.ICatalogService;
import org.pollub.common.dto.ItemDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {
    
    private final ICatalogService catalogService;
    private final IBranchInventoryService branchInventoryService;
    
    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllItems() {
        List<ItemDto> items = catalogService.findAll().stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(items);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(toDto(catalogService.findById(id)));
    }
    
    @GetMapping("/available")
    public ResponseEntity<List<ItemDto>> getAvailableItems() {
        List<ItemDto> items = catalogService.findAvailable().stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(items);
    }
    
    @GetMapping("/rented")
    public ResponseEntity<List<ItemDto>> getRentedItems() {
        List<ItemDto> items = catalogService.findRented().stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(items);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ItemDto>> getItemsByUser(@PathVariable Long userId) {
        List<BranchInventory> rentedInventory = branchInventoryService.getRentedByUser(userId);
        
        List<ItemDto> items = rentedInventory.stream()
                .map(inventory -> {
                    LibraryItem item = catalogService.findById(inventory.getItemId());
                    ItemDto dto = toDto(item);
                    
                    // Log debug info
                    if (inventory.getDueDate() == null) {
                        System.out.println("WARNING: DueDate is null for inventory " + inventory.getId());
                    }
                    if (inventory.getBranchId() == null) {
                        System.out.println("WARNING: BranchId is null for inventory " + inventory.getId());
                    } 
                    
                    dto.setDueDate(inventory.getDueDate());
                    dto.setRentedFromBranchId(inventory.getBranchId());
                    dto.setRentExtended(inventory.getRentExtended());
                    return dto;
                })
                .toList();
                
        return ResponseEntity.ok(items);
    }
    
    @GetMapping("/branch/{branchId}")
    public ResponseEntity<List<ItemDto>> getItemsByBranch(@PathVariable Long branchId) {
        List<ItemDto> items = catalogService.findByBranchId(branchId).stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(items);
    }
    
    @GetMapping("/branch/{branchId}/available")
    public ResponseEntity<List<ItemDto>> getAvailableByBranch(@PathVariable Long branchId) {
        List<ItemDto> items = catalogService.findAvailableByBranch(branchId).stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(items);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestParam String query) {
        List<ItemDto> items = catalogService.searchItems(query).stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(items);
    }
    
    @GetMapping("/bestsellers")
    public ResponseEntity<List<ItemDto>> getBestsellers() {
        List<ItemDto> items = catalogService.findBestsellers().stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(items);
    }
    
    // ===== Inventory Endpoints =====
    
    @GetMapping("/{id}/inventory")
    public ResponseEntity<List<BranchInventoryDto>> getItemInventory(@PathVariable Long id) {
        return ResponseEntity.ok(branchInventoryService.getInventoryForItem(id));
    }
    
    @GetMapping("/{id}/available-branches")
    public ResponseEntity<List<Long>> getAvailableBranches(@PathVariable Long id) {
        return ResponseEntity.ok(branchInventoryService.getAvailableBranchIds(id));
    }
    
    @PostMapping("/{id}/inventory")
    public ResponseEntity<BranchInventoryDto> addInventory(@PathVariable Long id,
                                                            @RequestParam Long branchId) {
        BranchInventory inventory = branchInventoryService.addInventory(id, branchId);
        return ResponseEntity.ok(toBranchInventoryDto(inventory));
    }

    //    @PutMapping("/{id}/extend")
//    public ResponseEntity<ItemDto> extendRental(@PathVariable Long id,
//                                                 @RequestParam Long branchId,
//                                                 @RequestParam(defaultValue = "7") int days) {
//        return ResponseEntity.ok(toDto(catalogService.extendRental(id, branchId, days)));
//    }

    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        catalogService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get history catalog data for a list of item IDs.
     * Returns enriched data with item titles, authors, branch names and addresses.
     * 
     * @param itemIds list of item IDs
     * @return map of itemId -> HistoryCatalogResponse
     */
    @PostMapping("/history-data")
    public ResponseEntity<Map<Long, HistoryCatalogResponse>> getHistoryCatalogData(@RequestBody List<Long> itemIds) {
        Map<Long, HistoryCatalogResponse> responses = catalogService.getHistoryCatalogDataByItemIds(itemIds);
        return ResponseEntity.ok(responses);
    }
    
    private ItemDto toDto(LibraryItem item) {
        // Compute overall status based on inventory
        List<Long> availableBranches = branchInventoryService.getAvailableBranchIds(item.getId());
        String overallStatus = availableBranches.isEmpty() ? "UNAVAILABLE" : "AVAILABLE";
        
        ItemDto.ItemDtoBuilder builder = ItemDto.builder()
                .id(item.getId())
                .title(item.getTitle())
                .description(item.getDescription())
                .imageUrl(item.getImageUrl())
                .itemType(item.getItemType().name())
                .status(overallStatus)
                .releaseYear(item.getReleaseYear())
                .isBestseller(item.getIsBestseller());
        
        if (item instanceof Book book) {
            builder.author(book.getAuthor())
                   .isbn(book.getIsbn())
                   .pageCount(book.getPageCount());
        } else if (item instanceof MovieDisc movie) {
            builder.director(movie.getDirector())
                   .durationMinutes(movie.getDuration());
        }
        
        return builder.build();
    }
    
    private BranchInventoryDto toBranchInventoryDto(BranchInventory inventory) {
        return BranchInventoryDto.builder()
                .id(inventory.getId())
                .itemId(inventory.getItemId())
                .branchId(inventory.getBranchId())
                .status(inventory.getStatus().name())
                .rentedByUserId(inventory.getRentedByUserId())
                .rentedAt(inventory.getRentedAt())
                .dueDate(inventory.getDueDate())
                .rentExtended(inventory.getRentExtended())
                .reservedByUserId(inventory.getReservedByUserId())
                .reservedAt(inventory.getReservedAt())
                .reservationExpiresAt(inventory.getReservationExpiresAt())
                .build();
    }
}

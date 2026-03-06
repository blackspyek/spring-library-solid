package org.pollub.catalog.controller;

import lombok.RequiredArgsConstructor;
import org.pollub.catalog.facade.CatalogFacade;
import org.pollub.catalog.model.dto.BranchInventoryDto;
import org.pollub.catalog.model.dto.HistoryCatalogResponse;
import org.pollub.common.dto.ItemDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    //Lab1 - Facade 1 Method Start
    private final CatalogFacade catalogFacade;
    
    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllItems() {
        return ResponseEntity.ok(catalogFacade.getAllItems());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(catalogFacade.getItemById(id));
    }
    
    @GetMapping("/available")
    public ResponseEntity<List<ItemDto>> getAvailableItems() {
        return ResponseEntity.ok(catalogFacade.getAvailableItems());
    }
    
    @GetMapping("/rented")
    public ResponseEntity<List<ItemDto>> getRentedItems() {
        return ResponseEntity.ok(catalogFacade.getRentedItems());
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ItemDto>> getItemsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(catalogFacade.getItemsByUser(userId));
    }
    
    @GetMapping("/branch/{branchId}")
    public ResponseEntity<List<ItemDto>> getItemsByBranch(@PathVariable Long branchId) {
        return ResponseEntity.ok(catalogFacade.getItemsByBranch(branchId));
    }
    
    @GetMapping("/branch/{branchId}/available")
    public ResponseEntity<List<ItemDto>> getAvailableByBranch(@PathVariable Long branchId) {
        return ResponseEntity.ok(catalogFacade.getAvailableByBranch(branchId));
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestParam String query) {
        return ResponseEntity.ok(catalogFacade.searchItems(query));
    }
    
    @GetMapping("/bestsellers")
    public ResponseEntity<List<ItemDto>> getBestsellers() {
        return ResponseEntity.ok(catalogFacade.getBestsellers());
    }

    
    @GetMapping("/{id}/inventory")
    public ResponseEntity<List<BranchInventoryDto>> getItemInventory(@PathVariable Long id) {
        return ResponseEntity.ok(catalogFacade.getItemInventory(id));
    }
    
    @GetMapping("/{id}/available-branches")
    public ResponseEntity<List<Long>> getAvailableBranches(@PathVariable Long id) {
        return ResponseEntity.ok(catalogFacade.getAvailableBranches(id));
    }
    
    @PostMapping("/{id}/inventory")
    public ResponseEntity<BranchInventoryDto> addInventory(@PathVariable Long id,
                                                            @RequestParam Long branchId) {
        return ResponseEntity.ok(catalogFacade.addInventory(id, branchId));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        catalogFacade.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/history-data")
    public ResponseEntity<Map<Long, HistoryCatalogResponse>> getHistoryCatalogData(@RequestBody List<Long> itemIds) {
        return ResponseEntity.ok(catalogFacade.getHistoryCatalogData(itemIds));
    }
    //Lab1 - Facade 1 Method End
}

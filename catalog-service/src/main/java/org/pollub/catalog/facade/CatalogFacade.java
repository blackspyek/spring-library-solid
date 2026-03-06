package org.pollub.catalog.facade;

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
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Facade (wzorzec Facade) upraszczający dostęp do podsystemów katalogowych.
 * Ukrywa złożoność interakcji między ICatalogService i IBranchInventoryService
 * oraz centralizuje logikę mapowania encji na DTO.
 */
@Component
@RequiredArgsConstructor
//Lab1 - Facade 1 Method Start
public class CatalogFacade {

    private final ICatalogService catalogService;
    private final IBranchInventoryService branchInventoryService;

    public List<ItemDto> getAllItems() {
        return catalogService.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public ItemDto getItemById(Long id) {
        return toDto(catalogService.findById(id));
    }

    public List<ItemDto> getAvailableItems() {
        return catalogService.findAvailable().stream()
                .map(this::toDto)
                .toList();
    }

    public List<ItemDto> getRentedItems() {
        return catalogService.findRented().stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Pobiera elementy wypożyczone przez użytkownika, wzbogacone o dane z inwentarza
     * (termin zwrotu, oddział, status przedłużenia).
     */
    public List<ItemDto> getItemsByUser(Long userId) {
        List<BranchInventory> rentedInventory = branchInventoryService.getRentedByUser(userId);

        return rentedInventory.stream()
                .map(inventory -> {
                    LibraryItem item = catalogService.findById(inventory.getItemId());
                    ItemDto dto = toDto(item);

                    dto.setDueDate(inventory.getDueDate());
                    dto.setRentedFromBranchId(inventory.getBranchId());
                    dto.setRentExtended(inventory.getRentExtended());
                    return dto;
                })
                .toList();
    }

    public List<ItemDto> getItemsByBranch(Long branchId) {
        return catalogService.findByBranchId(branchId).stream()
                .map(this::toDto)
                .toList();
    }

    public List<ItemDto> getAvailableByBranch(Long branchId) {
        return catalogService.findAvailableByBranch(branchId).stream()
                .map(this::toDto)
                .toList();
    }

    public List<ItemDto> searchItems(String query) {
        return catalogService.searchItems(query).stream()
                .map(this::toDto)
                .toList();
    }

    public List<ItemDto> getBestsellers() {
        return catalogService.findBestsellers().stream()
                .map(this::toDto)
                .toList();
    }

    public List<BranchInventoryDto> getItemInventory(Long itemId) {
        return branchInventoryService.getInventoryForItem(itemId);
    }

    public List<Long> getAvailableBranches(Long itemId) {
        return branchInventoryService.getAvailableBranchIds(itemId);
    }

    public BranchInventoryDto addInventory(Long itemId, Long branchId) {
        BranchInventory inventory = branchInventoryService.addInventory(itemId, branchId);
        return toBranchInventoryDto(inventory);
    }


    public void deleteItem(Long id) {
        catalogService.deleteItem(id);
    }

    public Map<Long, HistoryCatalogResponse> getHistoryCatalogData(List<Long> itemIds) {
        return catalogService.getHistoryCatalogDataByItemIds(itemIds);
    }


    private ItemDto toDto(LibraryItem item) {
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
//Lab1 - Facade 1 Method End


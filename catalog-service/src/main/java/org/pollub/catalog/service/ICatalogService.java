package org.pollub.catalog.service;

import org.pollub.catalog.model.LibraryItem;
import org.pollub.catalog.model.dto.HistoryCatalogResponse;
import org.pollub.common.dto.ReservationItemDto;

import java.util.List;
import java.util.Map;

public interface ICatalogService {

    List<LibraryItem> findAll();
    LibraryItem findById(Long id);
    List<LibraryItem> findAvailable();
    List<LibraryItem> findRented();
    List<LibraryItem> findByUserId(Long userId);
    List<LibraryItem> findByBranchId(Long branchId);
    List<LibraryItem> findAvailableByBranch(Long branchId);
    List<LibraryItem> searchItems(String query);
    List<LibraryItem> findBestsellers();
    void deleteItem(Long id);

    List<ReservationItemDto.Item> getItemsForReservation(List<Long> itemIds);
    
    Map<Long, HistoryCatalogResponse> getHistoryCatalogDataByItemIds(List<Long> itemIds);
}


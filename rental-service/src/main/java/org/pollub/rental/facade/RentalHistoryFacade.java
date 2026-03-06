package org.pollub.rental.facade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pollub.rental.client.CatalogServiceClient;
import org.pollub.rental.model.RentalHistory;
import org.pollub.rental.model.dto.HistoryCatalogResponse;
import org.pollub.rental.model.dto.RentalLastHistoryDto;
import org.pollub.rental.repository.IRentalHistoryRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Facade (wzorzec Facade) upraszczający pobieranie wzbogaconej historii wypożyczeń.
 * Ukrywa złożoność interakcji między IRentalHistoryRepository i CatalogServiceClient
 * oraz eliminuje duplikację logiki łączenia danych historii z danymi katalogowymi.
 */
@Component
@RequiredArgsConstructor
@Slf4j
//Lab1 - Facade 3 Method Start
public class RentalHistoryFacade {

    private final IRentalHistoryRepository rentalHistoryRepository;
    private final CatalogServiceClient catalogServiceClient;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    /**
     * Pobiera pełną historię wypożyczeń użytkownika wzbogaconą o dane katalogowe
     * (tytuł, autor, oddział).
     */
    public List<RentalLastHistoryDto> getEnrichedHistory(Long userId) {
        List<RentalHistory> history = rentalHistoryRepository.findByUserIdOrderByReturnedAtDesc(userId);
        return enrichWithCatalogData(history);
    }

    /**
     * Pobiera ostatnie N wpisów z historii wypożyczeń użytkownika
     * wzbogacone o dane katalogowe.
     */
    public List<RentalLastHistoryDto> getRecentEnrichedHistory(Long userId, int limit) {
        List<RentalHistory> history = rentalHistoryRepository.findCompletedByUserIdOrderByReturnedAtDesc(
                userId,
                PageRequest.of(0, limit)
        );
        return enrichWithCatalogData(history);
    }

    /**
     * Wspólna logika: pobiera dane katalogowe dla listy wypożyczeń i mapuje na DTO.
     * Eliminuje duplikację, która wcześniej występowała w getRecentHistory() i exportToXlsx().
     */
    private List<RentalLastHistoryDto> enrichWithCatalogData(List<RentalHistory> history) {
        List<Long> itemIds = history.stream()
                .map(RentalHistory::getItemId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, HistoryCatalogResponse> catalogDataMap = new HashMap<>();
        if (!itemIds.isEmpty()) {
            catalogDataMap = catalogServiceClient.getHistoryCatalogDataByItemIds(itemIds);
            if (catalogDataMap == null) {
                catalogDataMap = new HashMap<>();
            }
        }

        final Map<Long, HistoryCatalogResponse> finalCatalogMap = catalogDataMap;
        return history.stream()
                .map(rental -> mapToDto(rental, finalCatalogMap.getOrDefault(rental.getItemId(),
                        //Lab1 - Flyweight 2 Method Start
                        HistoryCatalogResponse.DEFAULT_UNKNOWN)))
                        //Lab1 - Flyweight 2 Method End
                .collect(Collectors.toList());
    }

    private RentalLastHistoryDto mapToDto(RentalHistory rental, HistoryCatalogResponse historyCatalogResponse) {
        String author = historyCatalogResponse.getItemAuthor() != null ? historyCatalogResponse.getItemAuthor() : "-";

        return RentalLastHistoryDto.builder()
                .id(rental.getId())
                .itemTitle(historyCatalogResponse.getItemTitle())
                .itemAuthor(author)
                .branchName(historyCatalogResponse.getBranchName() != null ? historyCatalogResponse.getBranchName() : "Brak danych")
                .branchAddress(historyCatalogResponse.getBranchAddress())
                .rentedAt(rental.getRentedAt() != null ? rental.getRentedAt().format(DATE_FORMATTER) : "-")
                .returnedAt(rental.getReturnedAt() != null ? rental.getReturnedAt().format(DATE_FORMATTER) : "-")
                .build();
    }
}
//Lab1 - Facade 3 Method End
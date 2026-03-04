package org.pollub.rental.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.pollub.rental.adapter.IExportAdapter;
import org.pollub.rental.client.CatalogServiceClient;
import org.pollub.rental.model.RentalHistory;
import org.pollub.rental.model.dto.HistoryCatalogResponse;
import org.pollub.rental.model.dto.RentalLastHistoryDto;
import org.pollub.rental.repository.IRentalHistoryRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RentalHistoryExportService implements IRentalHistoryExportService {

    private final IRentalHistoryRepository rentalHistoryRepository;
    private final CatalogServiceClient catalogServiceClient;
    private final IExportAdapter exportAdapter;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public List<RentalLastHistoryDto> getRecentHistory(Long userId, int limit) {
        List<RentalHistory> history = rentalHistoryRepository.findCompletedByUserIdOrderByReturnedAtDesc(
                userId,
                PageRequest.of(0, limit)
        );

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
                        HistoryCatalogResponse.builder()
                                .itemTitle("Brak danych")
                                .itemAuthor("-")
                                .branchName("Brak danych")
                                .branchAddress("Brak danych")
                                .build())))
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

    @Override
    public byte[] exportToXlsx(Long userId) throws IOException {
        log.info("Starting export process for user: {}", userId);

        List<RentalHistory> history = rentalHistoryRepository.findByUserIdOrderByReturnedAtDesc(userId);

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
        List<RentalLastHistoryDto> historyDtos = history.stream()
                .map(rental -> mapToDto(rental, finalCatalogMap.getOrDefault(rental.getItemId(),
                        HistoryCatalogResponse.builder()
                                .itemTitle("Brak danych")
                                .itemAuthor("-")
                                .branchName("Brak danych")
                                .branchAddress("Brak danych")
                                .build())))
                .collect(Collectors.toList());

        // L2 Adapter: Delegowanie eksportu do adaptera
        return exportAdapter.export(historyDtos);
    }
}
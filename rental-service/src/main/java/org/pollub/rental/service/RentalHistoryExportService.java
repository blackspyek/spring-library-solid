package org.pollub.rental.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

    public byte[] exportToXlsx(Long userId) throws IOException {
        List<RentalHistory> history = rentalHistoryRepository.findByUserIdOrderByReturnedAtDesc(userId);

        // Extract item IDs from rental history
        List<Long> itemIds = history.stream()
                .map(RentalHistory::getItemId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, HistoryCatalogResponse> catalogDataMap = new HashMap<>();
        if (!itemIds.isEmpty()) {
            catalogDataMap = catalogServiceClient.getHistoryCatalogDataByItemIds(itemIds);
            if (catalogDataMap == null) {
                catalogDataMap = new java.util.HashMap<>();
            }
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Historia wypożyczeń");

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            Row headerRow = sheet.createRow(0);
            String[] headers = {"Tytuł", "Autor", "Filia", "Adres filii", "Data wypożyczenia", "Data zwrotu"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (RentalHistory rental : history) {
                Row row = sheet.createRow(rowNum++);

                // Get catalog data for this rental, with defaults
                HistoryCatalogResponse catalogData = catalogDataMap.getOrDefault(rental.getItemId(),
                        HistoryCatalogResponse.builder()
                                .itemTitle("Brak danych")
                                .itemAuthor("-")
                                .branchName("Brak danych")
                                .branchAddress("Brak danych")
                                .build());

                // Title
                Cell titleCell = row.createCell(0);
                titleCell.setCellValue(catalogData.getItemTitle() != null ? catalogData.getItemTitle() : "Brak danych");
                titleCell.setCellStyle(dataStyle);

                // Author
                Cell authorCell = row.createCell(1);
                authorCell.setCellValue(catalogData.getItemAuthor() != null ? catalogData.getItemAuthor() : "-");
                authorCell.setCellStyle(dataStyle);

                // Branch name
                Cell branchCell = row.createCell(2);
                branchCell.setCellValue(catalogData.getBranchName() != null ? catalogData.getBranchName() : "Brak danych");
                branchCell.setCellStyle(dataStyle);

                // Branch address
                Cell addressCell = row.createCell(3);
                addressCell.setCellValue(catalogData.getBranchAddress() != null ? catalogData.getBranchAddress() : "Brak danych");
                addressCell.setCellStyle(dataStyle);

                // Rented at
                Cell rentedAtCell = row.createCell(4);
                rentedAtCell.setCellValue(rental.getRentedAt() != null ? rental.getRentedAt().format(DATE_FORMATTER) : "-");
                rentedAtCell.setCellStyle(dataStyle);

                // Returned at
                Cell returnedAtCell = row.createCell(5);
                returnedAtCell.setCellValue(rental.getReturnedAt() != null ? rental.getReturnedAt().format(DATE_FORMATTER) : "-");
                returnedAtCell.setCellStyle(dataStyle);
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
}
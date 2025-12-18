package org.pollub.library.rental.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.pollub.library.item.model.Book;
import org.pollub.library.rental.model.RentalHistory;
import org.pollub.library.rental.model.dto.RentalHistoryDto;
import org.pollub.library.rental.repository.IRentalHistoryRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RentalHistoryExportService {

    private final IRentalHistoryRepository rentalHistoryRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public List<RentalHistoryDto> getRecentHistory(Long userId, int limit) {
        List<RentalHistory> history = rentalHistoryRepository.findByUserIdWithItemOrderByReturnedAtDesc(
                userId,
                PageRequest.of(0, limit)
        );

        return history.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private RentalHistoryDto mapToDto(RentalHistory rental) {
        String author = "-";
        if (rental.getItem() instanceof Book book) {
            author = book.getAuthor() != null ? book.getAuthor() : "-";
        }

        return RentalHistoryDto.builder()
                .id(rental.getId())
                .itemTitle(rental.getItem().getTitle())
                .itemAuthor(author)
                .branchName(rental.getBranch() != null ? rental.getBranch().getName() : "Brak danych")
                .branchAddress(rental.getBranch() != null
                        ? rental.getBranch().getAddress() + ", " + rental.getBranch().getCity()
                        : "Brak danych")
                .rentedAt(rental.getRentedAt().format(DATE_FORMATTER))
                .returnedAt(rental.getReturnedAt().format(DATE_FORMATTER))
                .build();
    }

    public byte[] exportToXlsx(Long userId) throws IOException {
        List<RentalHistory> history = rentalHistoryRepository.findByUserIdOrderByReturnedAtDesc(userId);

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

                Cell titleCell = row.createCell(0);
                titleCell.setCellValue(rental.getItem().getTitle());
                titleCell.setCellStyle(dataStyle);

                Cell authorCell = row.createCell(1);

                if (rental.getItem() instanceof Book book) {
                    authorCell.setCellValue(book.getAuthor() != null ? book.getAuthor() : "-");
                } else {
                    authorCell.setCellValue("-");
                }
                authorCell.setCellStyle(dataStyle);

                // Branch name
                Cell branchCell = row.createCell(2);
                branchCell.setCellValue(rental.getBranch() != null ? rental.getBranch().getName() : "Brak danych");
                branchCell.setCellStyle(dataStyle);

                // Branch address
                Cell addressCell = row.createCell(3);
                if (rental.getBranch() != null) {
                    addressCell.setCellValue(rental.getBranch().getAddress() + ", " + rental.getBranch().getCity());
                } else {
                    addressCell.setCellValue("Brak danych");
                }
                addressCell.setCellStyle(dataStyle);

                // Rented at
                Cell rentedAtCell = row.createCell(4);
                rentedAtCell.setCellValue(rental.getRentedAt().format(DATE_FORMATTER));
                rentedAtCell.setCellStyle(dataStyle);

                // Returned at
                Cell returnedAtCell = row.createCell(5);
                returnedAtCell.setCellValue(rental.getReturnedAt().format(DATE_FORMATTER));
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

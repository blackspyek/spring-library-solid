package org.pollub.rental.adapter;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.pollub.rental.model.dto.RentalLastHistoryDto;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * XLSX export adapter using Apache POI library.
 * Implements IExportAdapter interface for Excel format exports.
 */
@Component
@Slf4j
public class XlsxExportAdapter implements IExportAdapter {

    private static final String SHEET_NAME = "Historia wypożyczeń";
    private static final String[] HEADERS = {
            "ID", "Tytuł", "Autor", "Biblioteka", "Adres", "Data wypożyczenia", "Data zwrotu"
    };

    @Override
    public byte[] export(List<RentalLastHistoryDto> historyData) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(SHEET_NAME);

            // Create header row with styling
            CellStyle headerStyle = createHeaderStyle(workbook);
            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }

            // Add data rows
            int rowNum = 1;
            for (RentalLastHistoryDto data : historyData) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(data.getId());
                row.createCell(1).setCellValue(data.getItemTitle());
                row.createCell(2).setCellValue(data.getItemAuthor());
                row.createCell(3).setCellValue(data.getBranchName());
                row.createCell(4).setCellValue(data.getBranchAddress());
                row.createCell(5).setCellValue(data.getRentedAt());
                row.createCell(6).setCellValue(data.getReturnedAt());
            }

            // Auto-size columns
            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            log.info("XLSX export completed for {} records", historyData.size());
            return outputStream.toByteArray();
        }
    }

    @Override
    public String getContentType() {
        return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    }

    @Override
    public String getFileExtension() {
        return "xlsx";
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }
}


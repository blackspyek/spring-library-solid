package org.pollub.rental.service;

import org.pollub.rental.model.dto.RentalLastHistoryDto;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public interface IRentalHistoryExportService {
    DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    List<RentalLastHistoryDto> getRecentHistory(Long userId, int limit);
    byte[] exportToXlsx(Long userId) throws IOException;
}

package org.pollub.rental.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pollub.rental.adapter.IExportAdapter;
import org.pollub.rental.facade.RentalHistoryFacade;
import org.pollub.rental.model.dto.RentalLastHistoryDto;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class RentalHistoryExportService implements IRentalHistoryExportService {

    //Lab1 - Facade 3 Method Start
    private final RentalHistoryFacade rentalHistoryFacade;
    private final IExportAdapter exportAdapter;

    public List<RentalLastHistoryDto> getRecentHistory(Long userId, int limit) {
        return rentalHistoryFacade.getRecentEnrichedHistory(userId, limit);
    }

    @Override
    public byte[] exportToXlsx(Long userId) throws IOException {
        log.info("Starting export process for user: {}", userId);

        List<RentalLastHistoryDto> historyDtos = rentalHistoryFacade.getEnrichedHistory(userId);

        // L2 Adapter: Delegowanie eksportu do adaptera
        return exportAdapter.export(historyDtos);
    }
    //Lab1 - Facade 3 Method End
}
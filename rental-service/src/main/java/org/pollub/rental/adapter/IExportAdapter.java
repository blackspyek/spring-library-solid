package org.pollub.rental.adapter;

import org.pollub.rental.model.dto.RentalLastHistoryDto;
import java.io.IOException;
import java.util.List;

//start L2 Adapter interface
/**
 * Adapter interface for exporting rental history in different formats.
 * Abstracts away specific export format implementations.
 */
public interface IExportAdapter {

    /**
     * Export rental history to bytes in specific format.
     * @param historyData list of rental history DTOs to export
     * @return byte array containing exported data
     * @throws IOException if export fails
     */
    byte[] export(List<RentalLastHistoryDto> historyData) throws IOException;

    /**
     * Get the content type for this export format.
     * @return MIME type (e.g., "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
     */
    String getContentType();

    /**
     * Get the file extension for this export format.
     * @return extension without dot (e.g., "xlsx", "csv", "pdf")
     */
    String getFileExtension();
}
//end L2 Adapter interface
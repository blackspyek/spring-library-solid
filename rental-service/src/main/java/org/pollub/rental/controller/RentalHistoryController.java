package org.pollub.rental.controller;

import lombok.RequiredArgsConstructor;
import org.pollub.common.security.JwtUserDetails;
import org.pollub.rental.model.dto.RentalLastHistoryDto;
import org.pollub.rental.service.IRentalHistoryExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
public class RentalHistoryController {
    private final IRentalHistoryExportService rentalHistoryExportService;

    @GetMapping("/history/recent")
    public ResponseEntity<List<RentalLastHistoryDto>> getRecentHistory(
            @AuthenticationPrincipal JwtUserDetails userDetails,
            @RequestParam(defaultValue = "3") int limit) {
        List<RentalLastHistoryDto> history = rentalHistoryExportService.getRecentHistory(userDetails.getUserId(), limit);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/history/export")
    public ResponseEntity<byte[]> exportRentalHistory(@AuthenticationPrincipal JwtUserDetails userDetails) throws IOException {
        try{
            byte[] xlsxContent = rentalHistoryExportService.exportToXlsx(
                    userDetails.getUserId()
            );
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "historia-wypozyczen.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(xlsxContent);
        } catch (IOException e){
            return ResponseEntity.internalServerError().build();
        }

    }

}

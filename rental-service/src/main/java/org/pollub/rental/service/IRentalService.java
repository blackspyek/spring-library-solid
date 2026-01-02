package org.pollub.rental.service;

import org.pollub.common.dto.ItemDto;
import org.pollub.common.dto.ReservationResponse;
import org.pollub.rental.model.RentalHistory;

import java.util.List;

public interface IRentalService {
    List<ItemDto> getActiveRentals(Long userId);
    List<RentalHistory> getUserRentalHistory(Long userId);
    List<RentalHistory> getItemRentalHistory(Long itemId);

    ReservationResponse rentItem(Long itemId, Long userId, Long branchId);

    void returnItem(Long itemId, Long branchId);

    void extendLoan(Long itemId, Long branchId, int days);
}

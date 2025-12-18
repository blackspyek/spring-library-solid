package org.pollub.library.rental.service;

import org.pollub.library.item.model.ItemStatus;
import org.pollub.library.item.model.LibraryItem;
import org.pollub.library.rental.model.dto.RentDto;

import java.util.List;

public interface IRentalService {
    LibraryItem rentItem(RentDto rentDto);
    LibraryItem returnItem(long itemId);
    LibraryItem extendRental(long itemId);
    List<LibraryItem> getRentedItems(long userId);
    List<LibraryItem> getAvailableItems();
    List<LibraryItem> getAllRentedItems();
}

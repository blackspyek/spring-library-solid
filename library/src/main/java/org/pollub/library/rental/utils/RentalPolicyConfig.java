package org.pollub.library.rental.utils;

import lombok.RequiredArgsConstructor;
import org.pollub.library.item.model.LibraryItem;
import org.pollub.library.item.repository.ILibraryItemRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RentalPolicyConfig implements IRentalPolicy {
    private final ILibraryItemRepository<LibraryItem> libraryItemRepository;

    @Value("${rental.policy.maxItemsPerUser:5}")
    private int maxItemsPerUser;


    @Override
    public boolean canUserRentItem(Long userId) {
        return libraryItemRepository.findByRentedByUserId(userId).size() < maxItemsPerUser;

    }
}

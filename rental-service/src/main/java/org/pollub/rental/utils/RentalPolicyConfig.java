package org.pollub.rental.utils;

import lombok.RequiredArgsConstructor;
import org.pollub.rental.model.RentalStatus;
import org.pollub.rental.repository.IRentalHistoryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RentalPolicyConfig implements IRentalPolicy {
    private final IRentalHistoryRepository rentalHistoryRepository;

    @Value("${rental.policy.maxItemsPerUser:5}")
    private int maxItemsPerUser;


    @Override
    public boolean canUserRentItem(Long userId) {
        return rentalHistoryRepository.countByUserIdAndStatus(userId, RentalStatus.RENTED) < maxItemsPerUser;

    }
}

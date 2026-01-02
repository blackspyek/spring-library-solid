package org.pollub.rental.utils;

public interface IRentalPolicy {
    boolean canUserRentItem(Long userId);
}

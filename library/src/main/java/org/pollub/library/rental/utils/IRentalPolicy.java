package org.pollub.library.rental.utils;

public interface IRentalPolicy {
    boolean canUserRentItem(Long userId);
}

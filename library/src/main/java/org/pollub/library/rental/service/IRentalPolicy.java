package org.pollub.library.rental.service;

public interface IRentalPolicy {
    boolean canUserRentItem(Long userId);
}

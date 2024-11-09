package org.pollub.library.auth.service.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class UidGenerator {
    private static final Random random = new Random();

    public String generateUid() {
        long randomUid = random.nextLong() % 1_000_000_000_000L;
        return String.format("%012d", randomUid);
    }
}

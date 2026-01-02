package org.pollub.user.service.utils;

import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class UidGenerator {

    public String generateUid() {
        long value = ThreadLocalRandom.current()
                .nextLong(100_000_000_000L);
        return String.format("%011d", value);
    }
}

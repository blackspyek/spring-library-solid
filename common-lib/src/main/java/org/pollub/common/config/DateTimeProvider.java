package org.pollub.common.config;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//Lab1 - Singleton 1 Start
/**
 * Eager Singleton providing consistent date/time operations across all services.
 * Uses eager initialization with static final instance.
 */
public class DateTimeProvider {

    private static final DateTimeProvider INSTANCE = new DateTimeProvider();

    private final DateTimeFormatter dateFormatter;
    private final DateTimeFormatter dateTimeFormatter;

    private DateTimeProvider() {
        this.dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        this.dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    }

    public static DateTimeProvider getInstance() {
        return INSTANCE;
    }

    public LocalDateTime now() {
        return LocalDateTime.now();
    }

}
//Lab1 End Singleton 1

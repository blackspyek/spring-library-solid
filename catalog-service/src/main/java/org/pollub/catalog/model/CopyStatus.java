package org.pollub.catalog.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Status of a specific book copy at a branch.
 */
public enum CopyStatus {
    AVAILABLE,
    RENTED,
    RESERVED;

    //Lab1 - Flyweight 3 Method Start
    public static final List<String> ALL_NAMES = Collections.unmodifiableList(
            Arrays.stream(values())
                    .map(Enum::name)
                    .sorted()
                    .collect(Collectors.toList())
    );
    //Lab1 - Flyweight 3 Method End
}

package org.pollub.catalog.flyweight;

import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

//Lab1 - Simple Factory 1 Start
//Lab1 - Flyweight 1 Start
/**
 * Flyweight factory for Sort objects.
 * Pre-initializes all Sort variants once and shares immutable instances
 * instead of creating new Sort objects on every request.
 */
public class SortFlyweightFactory {

    private static final Map<String, Sort> SORT_CACHE;

    static {
        Map<String, Sort> cache = new HashMap<>();
        cache.put("TITLE_ASC", Sort.by("title").ascending());
        cache.put("TITLE_DESC", Sort.by("title").descending());
        cache.put("AUTHOR_ASC", Sort.by("author").ascending());
        cache.put("AUTHOR_DESC", Sort.by("author").descending());
        SORT_CACHE = Collections.unmodifiableMap(cache);
    }

    private static final Sort DEFAULT_SORT = Sort.by("id").ascending();

    private SortFlyweightFactory() {
    }

    /**
     * Returns a shared (flyweight) Sort instance for the given sort parameter.
     *
     * @param sortParam the sort parameter (e.g., "TITLE_ASC", "AUTHOR_DESC")
     * @return the corresponding shared Sort object, or default sort if not found
     */
    public static Sort getSort(String sortParam) {
        if (sortParam == null) {
            return DEFAULT_SORT;
        }
        return SORT_CACHE.getOrDefault(sortParam, DEFAULT_SORT);
    }
}
//Lab1 - Flyweight 1 End
//Lab1 - Simple Factory 1 End

package org.pollub.catalog.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//Lab1 - Singleton 3 Start
/**
 * Enum Singleton for catalog caching.
 */
public enum CatalogCacheManager {
    INSTANCE;

    private final Map<String, Object> cache = new ConcurrentHashMap<>();

    public void put(String key, Object value) {
        cache.put(key, value);
    }

    public <T> T get(String key, Class<T> type) {
        Object value = cache.get(key);
        if (type.isInstance(value)) {
            return (T) value;
        }
        return null;
    }

    public int size() {
        return cache.size();
    }
}
//Lab1 End Singleton 3

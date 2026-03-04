package org.pollub.catalog.bridge;

import org.pollub.catalog.model.LibraryItem;
import java.util.List;

//start L2 Bridge interface
/**
 * Bridge for abstracting data source access.
 * Decouples CatalogService from concrete data access implementations.
 * Supports: Database, Cache, API, File, etc.
 */
public interface IDataSourceBridge {
    
    /**
     * Retrieve all library items.
     */
    List<LibraryItem> findAll();
    
    /**
     * Find items by branch ID.
     */
    List<LibraryItem> findByBranchId(Long branchId);
    
    /**
     * Find available items.
     */
    List<LibraryItem> findAvailable();
    
    /**
     * Find rented items.
     */
    List<LibraryItem> findRented();
    
    /**
     * Find items by user ID.
     */
    List<LibraryItem> findByUserId(Long userId);
}
//end L2 Bridge interface
package org.pollub.catalog.factory;

import org.pollub.catalog.model.LibraryItem;
import org.pollub.catalog.model.ItemType;

//Lab1 - Factory Method Start
/**
 * Factory Method interface for creating LibraryItem objects.
 * Each implementation creates a specific type of library item (Book, MovieDisc).
 */
public interface LibraryItemFactory {
    LibraryItem createItem(String title, String author, String genre, String description,
                           String imageUrl, Integer releaseYear, Boolean isBestseller);

    ItemType getItemType();
}
//Lab1 - Factory Method End

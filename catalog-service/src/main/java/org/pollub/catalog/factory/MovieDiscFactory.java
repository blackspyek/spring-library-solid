package org.pollub.catalog.factory;

import org.pollub.catalog.model.ItemType;
import org.pollub.catalog.model.LibraryItem;
import org.pollub.catalog.model.MovieDisc;
import org.springframework.stereotype.Component;

//Lab1 - Factory Method Start
/**
 * Factory Method implementation for creating MovieDisc objects.
 * Uses MovieDisc.builder() (Builder pattern) internally.
 */
@Component
public class MovieDiscFactory implements LibraryItemFactory {

    private final MovieDisc templateMovie;

    public MovieDiscFactory() {
        this.templateMovie = MovieDisc.builder()
                .itemType(ItemType.MOVIE)
                .resolution("1080p")
                .fileFormat("MP4")
                .digitalRights("Standard")
                .duration(120)
                .shelfNumber(1)
                .build();
    }

    @Override
    public LibraryItem createItem(String title, String director, String genre, String description,
                                  String imageUrl, Integer releaseYear, Boolean isBestseller) {
        //Lab1 - Prototype Start
        MovieDisc clonedMovie = templateMovie.clone();
        clonedMovie.setTitle(title);
        clonedMovie.setDirector(director);
        clonedMovie.setGenre(genre);
        clonedMovie.setDescription(description);
        clonedMovie.setImageUrl(imageUrl);
        clonedMovie.setReleaseYear(releaseYear);
        clonedMovie.setIsBestseller(isBestseller);
        return clonedMovie;
        //Lab1 End Prototype
    }

    @Override
    public ItemType getItemType() {
        return ItemType.MOVIE;
    }
}
//Lab1 End Factory Method

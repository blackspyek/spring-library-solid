package org.pollub.catalog.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import org.pollub.common.config.DateTimeProvider;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Book extends LibraryItem {
    
    @Column(nullable = false)
    private Integer pageCount;

    @Column(nullable = false)
    private String isbn;

    @Column(nullable = false)
    private String paperType;

    @Column(nullable = false)
    private String publisher;

    @Column(nullable = false)
    private Integer shelfNumber;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String genre;

    @Override
    @Deprecated
    public LocalDateTime calculateDueTime() {
        //Lab1 - Singleton 1 Start
        return DateTimeProvider.getInstance().now().plusDays(14);
        //Lab1 - Singleton 1 End
    }
    
    @Override
    public int getRentalDurationDays() {
        return 14;
    }

    //Lab1 - Prototype Start
    @Override
    public Book clone() {
        return (Book) super.clone();
    }
    //Lab1 End Prototype
    //Lab1 - Builder 1 Start

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder()
                .title(getTitle())
                .description(getDescription())
                .imageUrl(getImageUrl())
                .itemType(getItemType())
                .releaseYear(getReleaseYear())
                .isBestseller(getIsBestseller())
                .pageCount(pageCount)
                .isbn(isbn)
                .paperType(paperType)
                .publisher(publisher)
                .shelfNumber(shelfNumber)
                .author(author)
                .genre(genre);
    }

    /**
     * Manual Builder for Book with fluent API.
     * Handles both Book-specific and inherited LibraryItem fields.
     */
    public static class Builder {
        // LibraryItem fields
        private String title;
        private String description;
        private String imageUrl;
        private ItemType itemType;
        private Integer releaseYear;
        private Boolean isBestseller = false;

        // Book-specific fields
        private Integer pageCount;
        private String isbn;
        private String paperType;
        private String publisher;
        private Integer shelfNumber;
        private String author;
        private String genre;

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Builder itemType(ItemType itemType) {
            this.itemType = itemType;
            return this;
        }

        public Builder releaseYear(Integer releaseYear) {
            this.releaseYear = releaseYear;
            return this;
        }

        public Builder isBestseller(Boolean isBestseller) {
            this.isBestseller = isBestseller;
            return this;
        }

        public Builder pageCount(Integer pageCount) {
            this.pageCount = pageCount;
            return this;
        }

        public Builder isbn(String isbn) {
            this.isbn = isbn;
            return this;
        }

        public Builder paperType(String paperType) {
            this.paperType = paperType;
            return this;
        }

        public Builder publisher(String publisher) {
            this.publisher = publisher;
            return this;
        }

        public Builder shelfNumber(Integer shelfNumber) {
            this.shelfNumber = shelfNumber;
            return this;
        }

        public Builder author(String author) {
            this.author = author;
            return this;
        }

        public Builder genre(String genre) {
            this.genre = genre;
            return this;
        }

        public Book build() {
            Book book = new Book();
            // LibraryItem fields
            book.setTitle(title);
            book.setDescription(description);
            book.setImageUrl(imageUrl);
            book.setItemType(itemType);
            book.setReleaseYear(releaseYear);
            book.setIsBestseller(isBestseller);
            // Book-specific fields
            book.setPageCount(pageCount);
            book.setIsbn(isbn);
            book.setPaperType(paperType);
            book.setPublisher(publisher);
            book.setShelfNumber(shelfNumber);
            book.setAuthor(author);
            book.setGenre(genre);
            return book;
        }

    }
    //Lab1 End Builder 1

}

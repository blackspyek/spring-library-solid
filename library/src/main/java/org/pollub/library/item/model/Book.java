package org.pollub.library.item.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.pollub.library.item.model.utils.IHasAuthor;
import org.pollub.library.item.model.utils.IHasGenre;
import org.pollub.library.item.model.utils.IPaperBasedItem;

import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Book extends LibraryItem implements IPaperBasedItem, IHasAuthor, IHasGenre {
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
    public String getAuthor() {
        return author;
    }

    @Override
    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String getGenre() {
        return genre;
    }

    @Override
    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public Integer getPageCount() {
        return pageCount;
    }

    @Override
    public void setPageCount(Integer pages) {
        this.pageCount = pages;
    }

    @Override
    public String getPaperType() {
        return paperType;
    }

    @Override
    public void setPaperType(String paperType) {
        this.paperType = paperType;
    }

    @Override
    public String getPublisher() {
        return publisher;
    }

    @Override
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    @Override
    public Integer getShelfNumber() {
        return shelfNumber;
    }

    @Override
    public void setShelfNumber(Integer shelfNumber) {
        this.shelfNumber = shelfNumber;
    }

    @Override
    public LocalDateTime calculateDueTime() {
        return getRentedAt().plusDays(14);
    }
}

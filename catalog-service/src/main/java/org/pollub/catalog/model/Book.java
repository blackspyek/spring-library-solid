package org.pollub.catalog.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
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
        // Deprecated: use BranchInventory for tracking due dates
        return LocalDateTime.now().plusDays(14);
    }
    
    @Override
    public int getRentalDurationDays() {
        return 14;
    }
}

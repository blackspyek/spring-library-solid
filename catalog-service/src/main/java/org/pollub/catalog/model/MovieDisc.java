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
@Table(name = "movie_discs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class MovieDisc extends LibraryItem {
    
    @Column(nullable = false)
    private String director;
    
    @Column(nullable = false)
    private String resolution;
    
    @Column(nullable = false)
    private String fileFormat;
    
    @Column(nullable = false)
    private String digitalRights;
    
    @Column(nullable = false)
    private Integer duration;
    
    @Column(nullable = false)
    private String genre;
    
    @Column(nullable = false)
    private Integer shelfNumber;

    @Override
    @Deprecated
    public LocalDateTime calculateDueTime() {
        // Deprecated: use BranchInventory for tracking due dates
        return LocalDateTime.now().plusDays(7);
    }
    
    @Override
    public int getRentalDurationDays() {
        return 7;
    }
}

package org.pollub.catalog.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * Base class for all library items (Books, Movies, etc.)
 * Note: Rental/availability tracking is now handled by BranchInventory entity.
 */
@Entity
@Table(name = "library_items")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class LibraryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemType itemType;

    @Column(name = "release_year")
    private Integer releaseYear;

    @Column(name = "is_bestseller", nullable = false)
    private Boolean isBestseller = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Calculate due date based on item type (overridden by subclasses)
     * @deprecated Use BranchInventory for tracking rental due dates
     */
    @Deprecated
    public abstract LocalDateTime calculateDueTime();
    
    /**
     * Get the rental duration in days for this item type
     */
    public abstract int getRentalDurationDays();
}

package org.pollub.library.item.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import org.pollub.library.branch.model.LibraryBranch;
import org.pollub.library.user.model.User;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class LibraryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "rented_by_user_id")
    @JsonBackReference
    private User rentedByUser;

    private LocalDateTime rentedAt;
    private LocalDateTime dueDate;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "release_year")
    private Integer releaseYear;

    @Column(name = "is_bestseller", nullable = false)
    private Boolean isBestseller = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private ItemStatus status = ItemStatus.AVAILABLE;

    @ManyToMany
    @JoinTable(
            name = "item_branch_availability",
            joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "branch_id")
    )
    private Set<LibraryBranch> availableAtBranches = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public abstract LocalDateTime calculateDueTime();
}


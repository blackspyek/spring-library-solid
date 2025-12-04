package org.pollub.library.item.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import org.pollub.library.user.model.User;

import java.time.LocalDateTime;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private ItemStatus status = ItemStatus.AVAILABLE;

    public abstract LocalDateTime calculateDueTime();
}


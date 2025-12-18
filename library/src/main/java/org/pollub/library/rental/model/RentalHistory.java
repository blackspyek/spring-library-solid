package org.pollub.library.rental.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.pollub.library.branch.model.LibraryBranch;
import org.pollub.library.item.model.LibraryItem;
import org.pollub.library.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "rental_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private LibraryItem item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private LibraryBranch branch;

    @Column(name = "rented_at", nullable = false)
    private LocalDateTime rentedAt;

    @Column(name = "returned_at", nullable = false)
    private LocalDateTime returnedAt;
}

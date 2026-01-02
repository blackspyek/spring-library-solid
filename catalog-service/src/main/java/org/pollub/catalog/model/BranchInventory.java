package org.pollub.catalog.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents a copy of a library item at a specific branch.
 * Each branch can have its own copy with independent rental status.
 */
@Entity
@Table(name = "branch_inventory",
       uniqueConstraints = @UniqueConstraint(columnNames = {"item_id", "branch_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BranchInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "branch_id", nullable = false)
    private Long branchId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CopyStatus status = CopyStatus.AVAILABLE;

    // Rental info (populated when status = RENTED)
    @Column(name = "rented_by_user_id")
    private Long rentedByUserId;

    @Column(name = "rented_at")
    private LocalDateTime rentedAt;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "rent_extended")
    @Builder.Default
    private Boolean rentExtended = false;

    // Reservation info (populated when status = RESERVED)
    @Column(name = "reserved_by_user_id")
    private Long reservedByUserId;

    @Column(name = "reserved_at")
    private LocalDateTime reservedAt;

    @Column(name = "reservation_expires_at")
    private LocalDateTime reservationExpiresAt;
}

package org.pollub.branch.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "library_branches")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibraryBranch {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String branchNumber;
    
    private String name;
    
    @Column(nullable = false)
    private String city;
    
    @Column(nullable = false)
    private String address;
    
    @Column(nullable = false)
    private Double latitude;
    
    @Column(nullable = false)
    private Double longitude;
    
    private String phone;
    
    private String email;
    
    @Column(columnDefinition = "TEXT")
    private String openingHours;
}

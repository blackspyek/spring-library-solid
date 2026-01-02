package org.pollub.branch.repository;

import org.pollub.branch.model.LibraryBranch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<LibraryBranch, Long> {
    
    Optional<LibraryBranch> findByBranchNumber(String branchNumber);
    
    @Query("SELECT b FROM LibraryBranch b WHERE " +
           "LOWER(b.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.city) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.address) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.branchNumber) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<LibraryBranch> searchBranches(@Param("query") String query);
}

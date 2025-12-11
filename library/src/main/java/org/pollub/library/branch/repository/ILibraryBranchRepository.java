package org.pollub.library.branch.repository;

import org.pollub.library.branch.model.LibraryBranch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ILibraryBranchRepository extends JpaRepository<LibraryBranch, Long> {
    
    Optional<LibraryBranch> findByBranchNumber(String branchNumber);
    
    List<LibraryBranch> findByCityContainingIgnoreCase(String city);
    
    List<LibraryBranch> findByAddressContainingIgnoreCase(String address);
    
    @Query("SELECT b FROM LibraryBranch b WHERE " +
           "LOWER(b.city) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.address) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.branchNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<LibraryBranch> searchBranches(@Param("query") String query);
}

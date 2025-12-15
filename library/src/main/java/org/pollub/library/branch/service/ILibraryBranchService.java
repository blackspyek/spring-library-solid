package org.pollub.library.branch.service;

import org.pollub.library.branch.model.LibraryBranch;
import org.pollub.library.branch.model.dto.LibraryBranchCreateDto;

import java.util.List;

public interface ILibraryBranchService {
    
    List<LibraryBranch> getAllBranches();
    
    LibraryBranch getBranchById(Long id);
    
    LibraryBranch getBranchByNumber(String branchNumber);
    
    List<LibraryBranch> searchBranches(String query);
    
    LibraryBranch createBranch(LibraryBranchCreateDto dto);
    
    LibraryBranch updateBranch(Long id, LibraryBranchCreateDto dto);
    
    void deleteBranch(Long id);
}

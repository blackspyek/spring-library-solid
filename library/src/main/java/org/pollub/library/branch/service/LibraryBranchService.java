package org.pollub.library.branch.service;

import lombok.RequiredArgsConstructor;
import org.pollub.library.branch.model.LibraryBranch;
import org.pollub.library.branch.model.dto.LibraryBranchCreateDto;
import org.pollub.library.branch.repository.ILibraryBranchRepository;
import org.pollub.library.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class LibraryBranchService implements ILibraryBranchService {
    
    private final ILibraryBranchRepository branchRepository;
    
    @Override
    public List<LibraryBranch> getAllBranches() {
        return branchRepository.findAll();
    }
    
    @Override
    public LibraryBranch getBranchById(Long id) {
        return branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Library branch not found with id: " + id));
    }
    
    @Override
    public LibraryBranch getBranchByNumber(String branchNumber) {
        return branchRepository.findByBranchNumber(branchNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Library branch not found with number: " + branchNumber));
    }
    
    @Override
    public List<LibraryBranch> searchBranches(String query) {
        if (query == null || query.trim().isEmpty()) {
            return branchRepository.findAll();
        }
        return branchRepository.searchBranches(query.trim());
    }
    
    @Override
    public LibraryBranch createBranch(LibraryBranchCreateDto dto) {
        LibraryBranch branch = new LibraryBranch();
        mapDtoToEntity(dto, branch);
        return branchRepository.save(branch);
    }
    
    @Override
    public LibraryBranch updateBranch(Long id, LibraryBranchCreateDto dto) {
        LibraryBranch branch = getBranchById(id);
        mapDtoToEntity(dto, branch);
        return branchRepository.save(branch);
    }
    
    @Override
    public void deleteBranch(Long id) {
        if (!branchRepository.existsById(id)) {
            throw new ResourceNotFoundException("Library branch not found with id: " + id);
        }
        branchRepository.deleteById(id);
    }
    
    private void mapDtoToEntity(LibraryBranchCreateDto dto, LibraryBranch branch) {
        branch.setBranchNumber(dto.getBranchNumber());
        branch.setName(dto.getName());
        branch.setCity(dto.getCity());
        branch.setAddress(dto.getAddress());
        branch.setLatitude(dto.getLatitude());
        branch.setLongitude(dto.getLongitude());
        branch.setPhone(dto.getPhone());
        branch.setEmail(dto.getEmail());
    }
}

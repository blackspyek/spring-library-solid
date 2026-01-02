package org.pollub.branch.service;

import lombok.RequiredArgsConstructor;
import org.pollub.branch.client.UserServiceClient;
import org.pollub.branch.model.LibraryBranch;
import org.pollub.branch.model.dto.BranchCreateDto;
import org.pollub.branch.repository.BranchRepository;
import org.pollub.common.dto.UserDto;
import org.pollub.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BranchService {
    
    private final BranchRepository branchRepository;
    private final UserServiceClient userServiceClient;
    
    public List<LibraryBranch> getAllBranches() {
        return branchRepository.findAll();
    }
    
    public LibraryBranch getBranchById(Long id) {
        return branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LibraryBranch", id));
    }
    
    public LibraryBranch getBranchByNumber(String branchNumber) {
        return branchRepository.findByBranchNumber(branchNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with number: " + branchNumber));
    }
    
    public List<LibraryBranch> searchBranches(String query) {
        if (query == null || query.trim().isEmpty()) {
            return branchRepository.findAll();
        }
        return branchRepository.searchBranches(query.trim());
    }
    
    public LibraryBranch createBranch(BranchCreateDto dto) {
        LibraryBranch branch = LibraryBranch.builder()
                .branchNumber(dto.getBranchNumber())
                .name(dto.getName())
                .city(dto.getCity())
                .address(dto.getAddress())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .openingHours(dto.getOpeningHours())
                .build();
        return branchRepository.save(branch);
    }
    
    public LibraryBranch updateBranch(Long id, BranchCreateDto dto) {
        LibraryBranch branch = getBranchById(id);
        branch.setBranchNumber(dto.getBranchNumber());
        branch.setName(dto.getName());
        branch.setCity(dto.getCity());
        branch.setAddress(dto.getAddress());
        branch.setLatitude(dto.getLatitude());
        branch.setLongitude(dto.getLongitude());
        branch.setPhone(dto.getPhone());
        branch.setEmail(dto.getEmail());
        branch.setOpeningHours(dto.getOpeningHours());
        return branchRepository.save(branch);
    }
    
    public void deleteBranch(Long id) {
        if (!branchRepository.existsById(id)) {
            throw new ResourceNotFoundException("LibraryBranch", id);
        }
        branchRepository.deleteById(id);
    }
    
    /**
     * Get employees assigned to this branch from user-service
     */
    public List<UserDto> getBranchEmployees(Long branchId) {
        // Verify branch exists
        getBranchById(branchId);
        return userServiceClient.getEmployeesByBranch(branchId);
    }

    /**
     * Get multiple branches by IDs
     */
    public List<LibraryBranch> getBranchesByIds(List<Long> branchIds) {
        return branchRepository.findAllById(branchIds);
    }
}

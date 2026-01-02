package org.pollub.branch.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pollub.branch.model.LibraryBranch;
import org.pollub.branch.model.dto.BranchCreateDto;
import org.pollub.branch.service.BranchService;
import org.pollub.common.dto.BranchDto;
import org.pollub.common.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/branches")
@RequiredArgsConstructor
public class BranchController {
    
    private final BranchService branchService;
    
    @GetMapping
    public ResponseEntity<List<BranchDto>> getAllBranches() {
        List<BranchDto> branches = branchService.getAllBranches().stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(branches);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<BranchDto> getBranchById(@PathVariable Long id) {
        LibraryBranch branch = branchService.getBranchById(id);
        return ResponseEntity.ok(toDto(branch));
    }
    
    @GetMapping("/number/{branchNumber}")
    public ResponseEntity<BranchDto> getBranchByNumber(@PathVariable String branchNumber) {
        LibraryBranch branch = branchService.getBranchByNumber(branchNumber);
        return ResponseEntity.ok(toDto(branch));
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<BranchDto>> searchBranches(@RequestParam(required = false) String query) {
        List<BranchDto> branches = branchService.searchBranches(query).stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(branches);
    }
    
    @GetMapping("/{id}/employees")
    public ResponseEntity<List<UserDto>> getBranchEmployees(@PathVariable Long id) {
        List<UserDto> employees = branchService.getBranchEmployees(id);
        return ResponseEntity.ok(employees);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BranchDto> createBranch(@Valid @RequestBody BranchCreateDto dto) {
        LibraryBranch branch = branchService.createBranch(dto);
        return ResponseEntity.ok(toDto(branch));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BranchDto> updateBranch(
            @PathVariable Long id,
            @Valid @RequestBody BranchCreateDto dto) {
        LibraryBranch branch = branchService.updateBranch(id, dto);
        return ResponseEntity.ok(toDto(branch));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBranch(@PathVariable Long id) {
        branchService.deleteBranch(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Internal endpoint for other services to check if branch exists
     */
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> branchExists(@PathVariable Long id) {
        try {
            branchService.getBranchById(id);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }

    /**
     * Internal endpoint to fetch multiple branches by IDs in a single request.
     * Returns a map of branchId -> BranchDto for efficient batch operations.
     */
    @PostMapping("/batch")
    public ResponseEntity<Map<Long, BranchDto>> getBranchesByIds(@RequestBody List<Long> branchIds) {
        List<LibraryBranch> branches = branchService.getBranchesByIds(branchIds);
        Map<Long, BranchDto> branchMap = branches.stream()
                .collect(Collectors.toMap(
                        LibraryBranch::getId,
                        this::toDto
                ));
        return ResponseEntity.ok(branchMap);
    }
    
    private BranchDto toDto(LibraryBranch branch) {
        return BranchDto.builder()
                .id(branch.getId())
                .branchNumber(branch.getBranchNumber())
                .name(branch.getName())
                .city(branch.getCity())
                .address(branch.getAddress())
                .latitude(branch.getLatitude())
                .longitude(branch.getLongitude())
                .phone(branch.getPhone())
                .email(branch.getEmail())
                .openingHours(branch.getOpeningHours())
                .build();
    }
}

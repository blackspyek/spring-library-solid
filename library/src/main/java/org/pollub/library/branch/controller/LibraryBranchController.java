package org.pollub.library.branch.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pollub.library.branch.model.LibraryBranch;
import org.pollub.library.branch.model.dto.LibraryBranchCreateDto;
import org.pollub.library.branch.service.ILibraryBranchService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches")
@RequiredArgsConstructor
public class LibraryBranchController {
    
    private final ILibraryBranchService branchService;
    
    @GetMapping
    public ResponseEntity<List<LibraryBranch>> getAllBranches() {
        return ResponseEntity.ok(branchService.getAllBranches());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<LibraryBranch> getBranchById(@PathVariable Long id) {
        return ResponseEntity.ok(branchService.getBranchById(id));
    }
    
    @GetMapping("/number/{branchNumber}")
    public ResponseEntity<LibraryBranch> getBranchByNumber(@PathVariable String branchNumber) {
        return ResponseEntity.ok(branchService.getBranchByNumber(branchNumber));
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<LibraryBranch>> searchBranches(@RequestParam(required = false) String query) {
        return ResponseEntity.ok(branchService.searchBranches(query));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LibraryBranch> createBranch(@Valid @RequestBody LibraryBranchCreateDto dto) {
        return ResponseEntity.ok(branchService.createBranch(dto));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LibraryBranch> updateBranch(
            @PathVariable Long id,
            @Valid @RequestBody LibraryBranchCreateDto dto
    ) {
        return ResponseEntity.ok(branchService.updateBranch(id, dto));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBranch(@PathVariable Long id) {
        branchService.deleteBranch(id);
        return ResponseEntity.noContent().build();
    }
}

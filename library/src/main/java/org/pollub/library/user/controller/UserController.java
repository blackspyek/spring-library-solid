package org.pollub.library.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pollub.library.auth.model.ApiTextResponse;
import org.pollub.library.branch.model.LibraryBranch;
import org.pollub.library.auth.model.ChangePasswordDto;
import org.pollub.library.user.model.RoleSetDto;
import org.pollub.library.user.model.User;
import org.pollub.library.user.service.IUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;

    @PutMapping("/roles/{username}")
    @PreAuthorize("hasRole('ADMIN')" )
    public ResponseEntity<ApiTextResponse> updateRoles(@PathVariable String username,
                                                       @Valid @RequestBody RoleSetDto roles) {
        ApiTextResponse response = userService.updateUserRoles(username, roles);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/username/{username}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        User user = userService.findByEmail(email);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/id/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiTextResponse> deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.ok(new ApiTextResponse(true, "User deleted successfully"));
    }

    @PutMapping("/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiTextResponse> changePassword(@Valid @RequestBody ChangePasswordDto passwordDto, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        ApiTextResponse response = userService.changePassword(username, passwordDto);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/favourite-branch")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> updateFavouriteBranch(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long branchId
    ) {
        User user = userService.updateFavouriteBranch(userDetails.getUsername(), branchId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/favourite-branch")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LibraryBranch> getFavouriteBranch(@AuthenticationPrincipal UserDetails userDetails) {
        LibraryBranch branch = userService.getFavouriteBranch(userDetails.getUsername());
        return ResponseEntity.ok(branch);
    }

}

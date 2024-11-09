package org.pollub.library.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pollub.library.auth.model.ApiResponse;
import org.pollub.library.user.model.RoleSetDto;
import org.pollub.library.user.service.IUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;

    @PutMapping("/roles/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> updateRoles(@PathVariable String username,
                                                   @Valid @RequestBody RoleSetDto roles) {
        ApiResponse response = userService.updateUserRoles(username, roles);
        return ResponseEntity.ok(response);
    }

}

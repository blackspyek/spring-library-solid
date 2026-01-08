package org.pollub.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pollub.common.dto.BranchDto;
import org.pollub.common.dto.UserAddressDto;
import org.pollub.common.dto.UserDto;
import org.pollub.user.dto.*;
import org.pollub.user.model.Role;
import org.pollub.user.model.User;
import org.pollub.user.model.UserAddress;
import org.pollub.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.findAll().stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(toDto(user));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String username) {
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(toDto(user));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email) {
        User user = userService.findByEmail(email);
        return ResponseEntity.ok(toDto(user));
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchUsers(@RequestParam String query) {
        List<UserDto> users = userService.searchUsers(query).stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/branch/{branchId}/employees")
    public ResponseEntity<List<UserDto>> getEmployeesByBranch(@PathVariable Long branchId) {
        List<UserDto> employees = userService.findEmployeesByBranch(branchId).stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(employees);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody User user) {
        User updated = userService.updateUser(id, user);
        return ResponseEntity.ok(toDto(updated));
    }

    @PutMapping("/{id}/address")
    public ResponseEntity<UserDto> updateAddress(@PathVariable Long id, @RequestBody UserAddressDto addressDto) {
        User updated = userService.updateAddress(id, addressDto);
        return ResponseEntity.ok(toDto(updated));
    }

    @PutMapping("/{id}/roles")
    public ResponseEntity<UserDto> updateRoles(@PathVariable Long id, @RequestBody Set<Role> roles) {
        User updated = userService.updateRoles(id, roles);
        return ResponseEntity.ok(toDto(updated));
    }

    @PutMapping("/{id}/favourite-branch")
    public ResponseEntity<User> updateFavouriteBranch(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long branchId
    ) {
        User user = userService.updateFavouriteBranch(userDetails.getUsername(), branchId);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/password")
    public ResponseEntity<ApiTextResponse> changePassword(@Valid @RequestBody ChangePasswordDto passwordDto, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        ApiTextResponse response = userService.changePassword(username, passwordDto);
        return ResponseEntity.ok(response);
    }

    /**
     * Reset password for a user identified by email and PESEL.
     * This endpoint is called by auth-service and should be accessible internally.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ResetPasswordResponseDto> resetPassword(@Valid @RequestBody ResetPasswordRequestDto request) {
        ResetPasswordResponseDto response = userService.resetPassword(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/favourite-branch")
    public ResponseEntity<Long> getFavouriteBranch(@PathVariable Long id) {
        Long branchId = userService.getFavouriteBranchId(id);
        return ResponseEntity.ok(branchId);
    }
    
    /**
     * Get current user's favourite branch. UserId is extracted from JWT token.
     * This endpoint is used by the frontend to get the user's favourite branch.
     */
    @GetMapping("/favourite-branch")
    public ResponseEntity<BranchDto> getMyFavouriteBranch(
            @AuthenticationPrincipal org.pollub.common.security.JwtUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        BranchDto branch = userService.getFavouriteBranch(userId);
        return ResponseEntity.ok(branch);
    }
    
    /**
     * Update current user's favourite branch. UserId is extracted from JWT token.
     */
    @PutMapping("/favourite-branch")
    public ResponseEntity<User> updateMyFavouriteBranch(
            @AuthenticationPrincipal org.pollub.common.security.JwtUserDetails userDetails,
            @RequestParam(required = false) Long branchId
    ) {
        Long userId = userDetails.getUserId();
        User user = userService.findById(userId);
        User updated = userService.updateFavouriteBranch(user.getUsername(), branchId);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}/employee-branch")
    public ResponseEntity<Long> getEmployeeBranch(@PathVariable Long id) {
        Long branchId = userService.getEmployeeBranchId(id);
        return ResponseEntity.ok(branchId);
    }
    
    /**
     * Get current user's employee branch. UserId is extracted from JWT token.
     * This endpoint is used by the frontend for librarian loan management.
     */
    @GetMapping("/employee-branch")
    public ResponseEntity<BranchDto> getMyEmployeeBranch(
            @AuthenticationPrincipal org.pollub.common.security.JwtUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        BranchDto branch = userService.getEmployeeBranchById(userId);
        return ResponseEntity.ok(branch);
    }

    @GetMapping("/username/{username}/branch")
    public ResponseEntity<BranchDto> getEmployeeBranchByUsername(@PathVariable String username) {
        BranchDto branch = userService.getEmployeeBranch(username);
        return ResponseEntity.ok(branch);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Internal endpoint for other services to validate user existence
     */
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> userExists(@PathVariable Long id) {
        try {
            userService.findById(id);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }
    
    /**
     * Validate credentials for auth-service login
     */
    @PostMapping("/validate")
    public ResponseEntity<UserDto> validateCredentials(@RequestBody CredentialsDto credentials) {
        try {
            User user = userService.validateCredentials(
                    credentials.getUsernameOrEmail(), 
                    credentials.getPassword()
            );

            
            UserDto dto = toDto(user);

            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).build();
        }
    }
    
    /**
     * Create new user (for auth-service registration)
     */
    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        try {
            UserAddress address = null;
            if (userDto.getAddress() != null) {
                address = UserAddress.builder()
                        .street(userDto.getAddress().getStreet())
                        .city(userDto.getAddress().getCity())
                        .postalCode(userDto.getAddress().getPostalCode())
                        .country(userDto.getAddress().getCountry())
                        .buildingNumber(userDto.getAddress().getBuildingNumber())
                        .apartmentNumber(userDto.getAddress().getApartmentNumber())
                        .build();
            }
            
            User user = User.builder()
                    .username(userDto.getUsername())
                    .email(userDto.getEmail())
                    .name(userDto.getFirstName())
                    .surname(userDto.getLastName())
                    .password(userDto.getPassword())
                    .pesel(userDto.getPesel())
                    .phone(userDto.getPhone())
                    .address(address)
                    .roles(Set.of(Role.ROLE_READER))
                    .enabled(true)
                    .build();
            

            User created = userService.createUser(user);
            

            return ResponseEntity.ok(toDto(created));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .readerId(user.getReaderId())
                .phone(user.getPhone())
                .username(user.getUsername())
                .email(user.getEmail())
                .enabled(user.isEnabled())
                .roles(user.getRoles().stream()
                        .map(Role::name)
                        .collect(Collectors.toSet()))
                .favouriteBranchId(user.getFavouriteBranchId())
                .employeeBranchId(user.getEmployeeBranchId())
                .pesel(user.getPesel())
                .address(addressToDto(user.getAddress()))
                .mustChangePassword(user.isMustChangePassword())
                .build();
    }

    private UserAddressDto addressToDto(UserAddress address) {
        if (address == null) {
            return null;
        }
        return UserAddressDto.builder()
                .street(address.getStreet())
                .city(address.getCity())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .buildingNumber(address.getBuildingNumber())
                .apartmentNumber(address.getApartmentNumber())
                .build();
    }

    private UserAddress dtoToAddress(UserAddressDto dto) {
        if (dto == null) {
            return null;
        }
        return UserAddress.builder()
                .street(dto.getStreet())
                .city(dto.getCity())
                .postalCode(dto.getPostalCode())
                .country(dto.getCountry())
                .buildingNumber(dto.getBuildingNumber())
                .apartmentNumber(dto.getApartmentNumber())
                .build();
    }
}

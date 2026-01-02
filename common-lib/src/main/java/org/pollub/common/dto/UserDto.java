package org.pollub.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * DTO for user data shared across services.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    private String surname;
    private String firstName;
    private String lastName;
    private String readerId;
    private String phone;
    private String username;
    private String email;
    private boolean enabled;
    private Set<String> roles;
    private Long favouriteBranchId;
    private Long employeeBranchId;
    private String password;
    private String pesel;
    private UserAddressDto address;
    private boolean mustChangePassword;
    
    /**
     * Get primary role - returns first role or null
     */
    public String getRole() {
        if (roles != null && !roles.isEmpty()) {
            return roles.iterator().next();
        }
        return null;
    }
}


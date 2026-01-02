package org.pollub.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.pollub.user.validation.ValidPesel;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

/**
 * User entity for the user-service.
 * Note: Branch references are stored as IDs only (not full entities)
 * since branches are managed by branch-service.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String surname;

    @Column(unique = true)
    private String readerId;

    private String phone;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Column(nullable = false)
    private boolean enabled;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @JsonIgnore
    private Set<Role> roles;

    /**
     * Reference to favourite branch (stored as ID, actual branch data from branch-service)
     */
    @Column(name = "favourite_branch_id")
    private Long favouriteBranchId;

    /**
     * Reference to employee branch (stored as ID, actual branch data from branch-service)
     */
    @Column(name = "employee_branch_id")
    private Long employeeBranchId;

    @Column(unique = true)
    @ValidPesel
    private String pesel;

    @Embedded
    @NotNull
    @Valid
    private UserAddress address;

    @Builder.Default
    @Column(nullable = false)
    private boolean mustChangePassword = true;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> (GrantedAuthority) role::name)
                .toList();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}

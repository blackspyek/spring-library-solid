package org.pollub.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.pollub.user.validation.ValidPesel;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails, Cloneable {
    
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

    @Column(name = "favourite_branch_id")
    private Long favouriteBranchId;

    @Column(name = "employee_branch_id")
    private Long employeeBranchId;

    @Column(unique = true)
    @ValidPesel
    private String pesel;

    @Embedded
    @NotNull
    @Valid
    private UserAddress address;

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

    //Lab1 - Builder 4 Start


    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String name;
        private String surname;
        private String readerId;
        private String phone;
        private String username;
        private String email;
        private String password;
        private boolean enabled = true;
        private Set<Role> roles;
        private Long favouriteBranchId;
        private Long employeeBranchId;
        private String pesel;
        private UserAddress address;
        private boolean mustChangePassword = true;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder surname(String surname) { this.surname = surname; return this; }
        public Builder readerId(String readerId) { this.readerId = readerId; return this; }
        public Builder phone(String phone) { this.phone = phone; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder password(String password) { this.password = password; return this; }
        public Builder enabled(boolean enabled) { this.enabled = enabled; return this; }
        public Builder roles(Set<Role> roles) { this.roles = roles; return this; }
        public Builder favouriteBranchId(Long favouriteBranchId) { this.favouriteBranchId = favouriteBranchId; return this; }
        public Builder employeeBranchId(Long employeeBranchId) { this.employeeBranchId = employeeBranchId; return this; }
        public Builder pesel(String pesel) { this.pesel = pesel; return this; }
        public Builder address(UserAddress address) { this.address = address; return this; }
        public Builder mustChangePassword(boolean mustChangePassword) { this.mustChangePassword = mustChangePassword; return this; }

        public User build() {
            User user = new User();
            user.setId(id);
            user.setName(name);
            user.setSurname(surname);
            user.setReaderId(readerId);
            user.setPhone(phone);
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(password);
            user.setEnabled(enabled);
            user.setRoles(roles);
            user.setFavouriteBranchId(favouriteBranchId);
            user.setEmployeeBranchId(employeeBranchId);
            user.setPesel(pesel);
            user.setAddress(address);
            user.setMustChangePassword(mustChangePassword);
            return user;
        }
    }

    //Lab1 - Prototype 3 Start
    @Override
    public User clone() {
        try {
            return (User) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone not supported for User", e);
        }
    }
    //Lab1 End Prototype 3

    public Builder toBuilder() {
        return new Builder()
                .id(this.id)
                .name(this.name)
                .surname(this.surname)
                .readerId(this.readerId)
                .phone(this.phone)
                .username(this.username)
                .email(this.email)
                .password(this.password)
                .enabled(this.enabled)
                .roles(this.roles)
                .favouriteBranchId(this.favouriteBranchId)
                .employeeBranchId(this.employeeBranchId)
                .pesel(this.pesel)
                .address(this.address)
                .mustChangePassword(this.mustChangePassword);
    }
    //Lab1 End Builder 4

}

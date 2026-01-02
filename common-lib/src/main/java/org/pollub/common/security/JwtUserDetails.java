package org.pollub.common.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

import java.util.stream.Collectors;

/**
 * User details extracted from JWT token for Spring Security.
 */
public class JwtUserDetails implements UserDetails {
    
    private final Long userId;
    private final String username;
    private final Collection<String> roles;
    
    public JwtUserDetails(Long userId, String username, Collection<String> roles) {
        this.userId = userId;
        this.username = username;
        this.roles = roles;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public Collection<String> getRoles() {
        return roles;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
    
    @Override
    public String getPassword() {
        return null; // Not needed for JWT authentication
    }
    
    @Override
    public String getUsername() {
        return username;
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
        return true;
    }
}

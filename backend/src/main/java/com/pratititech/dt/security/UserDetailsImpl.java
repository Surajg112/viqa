package com.pratititech.dt.security;


import com.pratititech.dt.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserDetailsImpl implements UserDetails {

    private final User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // No roles/authorities implemented
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmailId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // No expiry implemented
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // No locking implemented
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // No credentials expiry
    }

    @Override
    public boolean isEnabled() {
        return true; // No enable/disable implemented
    }
}

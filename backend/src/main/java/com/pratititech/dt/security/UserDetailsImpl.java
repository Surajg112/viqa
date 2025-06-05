package com.pratititech.dt.security;

import com.pratititech.dt.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class UserDetailsImpl implements UserDetails, Serializable {

    private static final long serialVersionUID = 1L;

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
        // Only verified users are enabled
        return user.isVerified();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserDetailsImpl)) return false;
        UserDetailsImpl that = (UserDetailsImpl) o;
        return Objects.equals(user.getUserId(), that.user.getUserId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(user.getUserId());
    }
}

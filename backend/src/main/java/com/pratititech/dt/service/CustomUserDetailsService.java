package com.pratititech.dt.service;

import com.pratititech.dt.model.User;
import com.pratititech.dt.repository.UserRepository;
import com.pratititech.dt.security.UserDetailsImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // Constructor injection (preferred over field injection)
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String emailId) throws UsernameNotFoundException {
        User user = userRepository.findByEmailId(emailId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + emailId));

        if (!user.isVerified()) {
            throw new UsernameNotFoundException("User email not verified: " + emailId);
        }

        return new UserDetailsImpl(user);
    }
}

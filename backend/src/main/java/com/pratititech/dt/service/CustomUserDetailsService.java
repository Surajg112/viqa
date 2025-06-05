package com.pratititech.dt.service;


import com.pratititech.dt.model.User;
import com.pratititech.dt.repository.UserRepository;
import com.pratititech.dt.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    // Load UserDetails by username (email here)
    @Override
    public UserDetails loadUserByUsername(String emailId) throws UsernameNotFoundException {
        User user = userRepository.findByEmailId(emailId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + emailId));

        return new UserDetailsImpl(user);
    }
}

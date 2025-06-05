package com.pratititech.dt.repository;

import com.pratititech.dt.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Find user by email
    Optional<User> findByEmailId(String emailId);

    // Check if a user exists by email (for duplicate email check)
    boolean existsByEmailId(String emailId);

    // Optional: Find unverified user by email and OTP
    Optional<User> findByEmailIdAndOtpAndIsVerifiedFalse(String emailId, String otp);
}

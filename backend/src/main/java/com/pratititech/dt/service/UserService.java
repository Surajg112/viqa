package com.pratititech.dt.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pratititech.dt.dto.UserResponseDTO;
import com.pratititech.dt.dto.UserSignupRequestDTO;
import com.pratititech.dt.exception.InvalidAgeException;
import com.pratititech.dt.model.User;
import com.pratititech.dt.repository.UserRepository;

import jakarta.mail.MessagingException;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    // Register user & send OTP
    public User initiateSignupWithOtp(UserSignupRequestDTO signupRequest) {
        LocalDate birthDate = signupRequest.getBirthDate();
        int age = Period.between(birthDate, LocalDate.now()).getYears();

        if (age < 14 || age > 65) {
            throw new InvalidAgeException("User must be between 14 and 65 years old");
        }

        if (userRepository.existsByEmailId(signupRequest.getEmailId())) {
            throw new IllegalArgumentException("Email already in use");
        }

        // Build User object from DTO
        User user = new User();
        user.setFirstName(signupRequest.getFirstName());
        user.setLastName(signupRequest.getLastName());
        user.setEmailId(signupRequest.getEmailId());
        user.setGender(signupRequest.getGender());
        user.setBirthDate(signupRequest.getBirthDate());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setIsVerified(false);

        // Generate OTP
        String otp = generateOtp();
        user.setOtp(otp);
        user.setOtpGeneratedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        try {
            emailService.sendOtpEmail(
                savedUser.getEmailId(), otp,
                savedUser.getFirstName(), savedUser.getLastName()
            );
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send OTP email", e);
        }

        return savedUser;
    }

    // Verify OTP
    public User verifyOtp(String emailId, String otp) {
        Optional<User> optionalUser = userRepository.findByEmailIdAndOtpAndIsVerifiedFalse(emailId, otp);

        if (optionalUser.isEmpty()) {
            return null;
        }

        User user = optionalUser.get();

        if (user.getOtpGeneratedAt().isBefore(LocalDateTime.now().minusMinutes(5))) {
            return null; // OTP expired
        }

        user.setIsVerified(true);
        user.setOtp(null);
        user.setOtpGeneratedAt(null);

        return userRepository.save(user);
    }

    // Resend OTP
    public String resendOtp(String emailId) {
        Optional<User> optionalUser = userRepository.findByEmailId(emailId);

        if (optionalUser.isEmpty()) {
            return "User not found";
        }

        User user = optionalUser.get();

        if (user.isVerified()) {
            return "User already verified";
        }

        if (user.getOtpGeneratedAt() != null &&
            Duration.between(user.getOtpGeneratedAt(), LocalDateTime.now()).getSeconds() < 60) {
            return "Please wait before requesting another OTP";
        }

        String newOtp = generateOtp();
        user.setOtp(newOtp);
        user.setOtpGeneratedAt(LocalDateTime.now());

        userRepository.save(user);

        try {
            emailService.sendOtpEmail(user.getEmailId(), newOtp, user.getFirstName(), user.getLastName());
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send OTP email", e);
        }

        return "New OTP sent to your email";
    }

    // Login
    public User loginUser(String emailId, String rawPassword) {
        Optional<User> optionalUser = userRepository.findByEmailId(emailId);

        if (optionalUser.isEmpty()) {
            return null;
        }

        User user = optionalUser.get();

        if (!user.isVerified() || !passwordEncoder.matches(rawPassword, user.getPassword())) {
            return null;
        }

        return user;
    }

    // Convert User to UserResponseDTO
    public UserResponseDTO convertToResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setUserId(user.getUserId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setGender(user.getGender());
        dto.setEmailId(user.getEmailId());
        dto.setBirthDate(user.getBirthDate());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setVerified(user.isVerified());
        return dto;
    }

    // Utility: Generate 6-digit OTP
    private String generateOtp() {
        return String.format("%06d", new Random().nextInt(999999));
    }
}

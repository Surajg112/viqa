package com.pratititech.dt.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pratititech.dt.dto.LoginResponseDTO;
import com.pratititech.dt.dto.PasswordResetRequestDTO;
import com.pratititech.dt.dto.UserResponseDTO;
import com.pratititech.dt.dto.UserSignupRequestDTO;
import com.pratititech.dt.dto.UserUpdateRequestDTO;
import com.pratititech.dt.exception.InvalidAgeException;
import com.pratititech.dt.exception.InvalidPasswordException;
import com.pratititech.dt.exception.UserNotVerifiedException;
import com.pratititech.dt.exception.UserNotFoundException;
import com.pratititech.dt.exception.OtpExpiredException;
import com.pratititech.dt.exception.OtpInvalidException;
import com.pratititech.dt.model.User;
import com.pratititech.dt.repository.UserRepository;
import com.pratititech.dt.security.jwt.JwtTokenProvider;

import jakarta.mail.MessagingException;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

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

        if (signupRequest.getPassword() == null || signupRequest.getPassword().length() < 6) {
            throw new InvalidPasswordException("Password must be at least 6 characters long");
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
            emailService.sendOtpEmail(savedUser.getEmailId(), otp, savedUser.getFirstName(), savedUser.getLastName());
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send OTP email", e);
        }

        return savedUser;
    }

    // Verify OTP
    public LoginResponseDTO verifyOtp(String emailId, String otp) {
        User user = userRepository.findByEmailId(emailId)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + emailId));

        if (user.isVerified()) {
            throw new IllegalArgumentException("User is already verified");
        }

        if (!otp.equals(user.getOtp())) {
            throw new OtpInvalidException("Invalid OTP");
        }

        if (user.getOtpGeneratedAt().isBefore(LocalDateTime.now().minusMinutes(5))) {
            throw new OtpExpiredException("OTP has expired");
        }

        user.setIsVerified(true);
        user.setOtp(null);
        user.setOtpGeneratedAt(null);
        userRepository.save(user);

        String token = jwtTokenProvider.generateToken(user.getEmailId());
        LoginResponseDTO responseDTO = new LoginResponseDTO();
        responseDTO.setToken(token);
        responseDTO.setUser(convertToResponseDTO(user));
        return responseDTO;
    }


    // Resend OTP
    public String resendOtp(String emailId) {
        User user = userRepository.findByEmailId(emailId)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + emailId));

        if (user.isVerified()) {
            return "User already verified";
        }

        if (user.getOtpGeneratedAt() != null
                && Duration.between(user.getOtpGeneratedAt(), LocalDateTime.now()).getSeconds() < 60) {
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

    // Login returning User entity (simple)
    public User loginUser(String emailId, String rawPassword) {
        User user = userRepository.findByEmailId(emailId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!user.isVerified()) {
            throw new UserNotVerifiedException("User email not verified");
        }

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
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

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailId(email);
    }

    public User resetPassword(Long userId, PasswordResetRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("New password must be different from old password");
        }

        if (request.getNewPassword() == null || request.getNewPassword().length() < 6) {
            throw new InvalidPasswordException("Password must be at least 6 characters long");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        return userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmailId(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    public User updateUserDetails(String email, UserUpdateRequestDTO updateRequest) {
        User user = userRepository.findByEmailId(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        int age = Period.between(updateRequest.getBirthDate(), LocalDate.now()).getYears();
        if (age < 14 || age > 65) {
            throw new IllegalArgumentException("User age must be between 14 and 65 years");
        }

        user.setFirstName(updateRequest.getFirstName());
        user.setLastName(updateRequest.getLastName());
        user.setGender(updateRequest.getGender());
        user.setBirthDate(updateRequest.getBirthDate());

        return userRepository.save(user);
    }

    // Login with JWT token generation & DTO response
    public LoginResponseDTO loginUserAndGenerateToken(String emailId, String rawPassword) {
        User user = userRepository.findByEmailId(emailId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!user.isVerified()) {
            throw new UserNotVerifiedException("User email not verified");
        }

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new InvalidPasswordException("Invalid password");
        }

        String token = jwtTokenProvider.generateToken(user.getEmailId());

        LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
        loginResponseDTO.setToken(token);
        loginResponseDTO.setUser(convertToResponseDTO(user));

        return loginResponseDTO;
    }
}

package com.pratititech.dt.controller;

import com.pratititech.dt.dto.*;
import com.pratititech.dt.exception.InvalidAgeException;
import com.pratititech.dt.exception.InvalidPasswordException;
import com.pratititech.dt.exception.OtpExpiredException;
import com.pratititech.dt.exception.OtpInvalidException;
import com.pratititech.dt.exception.UserNotFoundException;
import com.pratititech.dt.exception.UserNotVerifiedException;
import com.pratititech.dt.model.User;
import com.pratititech.dt.security.jwt.JwtTokenProvider;
import com.pratititech.dt.service.UserService;
import com.pratititech.dt.util.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private final String uploadDir = "uploads";

    @PostMapping("/signup")
    public Result registerUser(@Valid @RequestBody UserSignupRequestDTO signupRequest) {
        try {
            User savedUser = userService.initiateSignupWithOtp(signupRequest);
            UserResponseDTO responseDTO = userService.convertToResponseDTO(savedUser);
            return new Result(true, HttpStatus.OK.value(), "Signup successful. OTP sent to email.", responseDTO);
        } catch (InvalidAgeException | IllegalArgumentException e) {
            return new Result(false, HttpStatus.BAD_REQUEST.value(), e.getMessage());
        } catch (Exception e) {
            return new Result(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Something went wrong");
        }
    }

    @PostMapping("/verify-otp")
    public Result verifyOtp(@Valid @RequestBody OtpVerificationRequestDTO otpRequest) {
        try {
            LoginResponseDTO loginResponse = userService.verifyOtp(otpRequest.getEmailId(), otpRequest.getOtp());
            return new Result(true, HttpStatus.OK.value(), "User verified successfully", loginResponse);
        } catch (OtpInvalidException | OtpExpiredException | UserNotFoundException e) {
            return new Result(false, HttpStatus.BAD_REQUEST.value(), e.getMessage());
        } catch (Exception e) {
            return new Result(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Something went wrong");
        }
    }


    @PostMapping("/resend-otp")
    public Result resendOtp(@RequestParam String emailId) {
        try {
            String result = userService.resendOtp(emailId);
            boolean success = "New OTP sent to your email".equals(result);
            return new Result(success,
                    success ? HttpStatus.OK.value() : HttpStatus.BAD_REQUEST.value(),
                    result);
        } catch (Exception e) {
            return new Result(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Something went wrong");
        }
    }

    @PostMapping("/signin")
    public Result loginUser(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            LoginResponseDTO loginResponse = userService.loginUserAndGenerateToken(
                    loginRequest.getEmailId(), loginRequest.getPassword());

            return new Result(true, HttpStatus.OK.value(), "Login successful", loginResponse);
        } catch (UserNotFoundException | UserNotVerifiedException | InvalidPasswordException e) {
            // These are handled by GlobalExceptionHandler, rethrow them
            throw e;
        } catch (Exception e) {
            // Catch-all for any unexpected errors
            return new Result(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Something went wrong");
        }
    }


    @PostMapping("/upload")
    public Result handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String filePath = uploadDir + File.separator + file.getOriginalFilename();
            Path path = Paths.get(filePath);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            return new Result(true, HttpStatus.OK.value(), "File uploaded successfully", path.toString());
        } catch (IOException e) {
            return new Result(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Upload failed: " + e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public Result resetPassword(@RequestBody @Valid PasswordResetRequestDTO request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            Optional<User> optionalUser = userService.findByEmail(email);
            if (optionalUser.isEmpty()) {
                return new Result(false, HttpStatus.UNAUTHORIZED.value(), "Unauthorized");
            }

            userService.resetPassword(optionalUser.get().getUserId(), request);
            return new Result(true, HttpStatus.OK.value(), "Password reset successful");
        } catch (IllegalArgumentException e) {
            return new Result(false, HttpStatus.BAD_REQUEST.value(), e.getMessage());
        } catch (Exception e) {
            return new Result(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Something went wrong");
        }
    }

    @GetMapping("/me")
    public Result getLoggedInUserProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            User user = userService.getUserByEmail(email);
            UserResponseDTO responseDTO = userService.convertToResponseDTO(user);

            return new Result(true, HttpStatus.OK.value(), "User profile fetched successfully", responseDTO);
        } catch (UsernameNotFoundException e) {
            return new Result(false, HttpStatus.NOT_FOUND.value(), "User not found");
        } catch (Exception e) {
            return new Result(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Something went wrong");
        }
    }

    @PutMapping("/update-profile")
    public Result updateUserProfile(@Valid @RequestBody UserUpdateRequestDTO updateRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            User updatedUser = userService.updateUserDetails(email, updateRequest);
            UserResponseDTO responseDTO = userService.convertToResponseDTO(updatedUser);

            return new Result(true, HttpStatus.OK.value(), "User details updated successfully", responseDTO);
        } catch (IllegalArgumentException e) {
            return new Result(false, HttpStatus.BAD_REQUEST.value(), e.getMessage());
        } catch (Exception e) {
            return new Result(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Something went wrong");
        }
    }
}

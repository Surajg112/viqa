package com.pratititech.dt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.pratititech.dt.dto.*;
import com.pratititech.dt.exception.InvalidAgeException;
import com.pratititech.dt.model.User;
import com.pratititech.dt.service.UserService;
import com.pratititech.dt.util.Result;

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public Result registerUser(@Valid @RequestBody UserSignupRequestDTO signupRequest) {
        try {
            User savedUser = userService.initiateSignupWithOtp(signupRequest);
            return new Result(true, HttpStatus.OK.value(), "Signup successful. OTP sent to email.", savedUser);
        } catch (InvalidAgeException e) {
            return new Result(false, HttpStatus.BAD_REQUEST.value(), e.getMessage());
        } catch (IllegalArgumentException e) {
            return new Result(false, HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @PostMapping("/verify-otp")
    public Result verifyOtp(@Valid @RequestBody OtpVerificationRequestDTO otpRequest) {
        User verifiedUser = userService.verifyOtp(otpRequest.getEmailId(), otpRequest.getOtp());
        if (verifiedUser == null) {
            return new Result(false, HttpStatus.BAD_REQUEST.value(), "Invalid OTP or already verified / OTP expired");
        }
        UserResponseDTO responseDTO = userService.convertToResponseDTO(verifiedUser);
        return new Result(true, HttpStatus.OK.value(), "User verified successfully", responseDTO);
    }

    @PostMapping("/resend-otp")
    public Result resendOtp(@RequestParam String emailId) {
        String result = userService.resendOtp(emailId);
        if ("New OTP sent to your email".equals(result)) {
            return new Result(true, HttpStatus.OK.value(), result);
        }
        return new Result(false, HttpStatus.BAD_REQUEST.value(), result);
    }

    @PostMapping("/signin")
    public Result loginUser(@Valid @RequestBody LoginRequestDTO loginRequest) {
        User loggedInUser = userService.loginUser(loginRequest.getEmailId(), loginRequest.getPassword());
        if (loggedInUser == null) {
            return new Result(false, HttpStatus.UNAUTHORIZED.value(), "Invalid email/password or email not verified");
        }
        UserResponseDTO responseDTO = userService.convertToResponseDTO(loggedInUser);
        return new Result(true, HttpStatus.OK.value(), "Login successful", responseDTO);
    }
}

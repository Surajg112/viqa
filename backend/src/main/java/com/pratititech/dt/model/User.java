package com.pratititech.dt.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(columnNames = "emailId") })
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	@NotBlank(message = "First name is required")
	@Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
	private String firstName;

	@NotBlank(message = "Last name is required")
	@Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
	private String lastName;

	@NotBlank(message = "Gender is required")
	private String gender; // expected values: "Male", "Female", etc.

	@PastOrPresent(message = "Created date can't be in the future")
	private LocalDateTime createdAt;

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format")
	@Column(unique = true)
	private String emailId;

	@NotBlank(message = "Password is required")
	@Size(min = 6, message = "Password must be at least 6 characters long")
	private String password;

	@NotNull(message = "Birth date is required")
	@Past(message = "Birth date must be in the past")
	private LocalDate birthDate;

	// ✅ New Fields for OTP verification
	@Column(name = "otp")
	private String otp;

	@Column(name = "otp_generated_at")
	private LocalDateTime otpGeneratedAt;

	@Column(name = "is_verified")
	private boolean isVerified = false;

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
	}

	// Getters and Setters

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public LocalDateTime getOtpGeneratedAt() {
		return otpGeneratedAt;
	}

	public void setOtpGeneratedAt(LocalDateTime otpGeneratedAt) {
		this.otpGeneratedAt = otpGeneratedAt;
	}

	public boolean isVerified() {
		return isVerified;
	}

	public void setIsVerified(boolean isVerified) {
		this.isVerified = isVerified;
	}
}

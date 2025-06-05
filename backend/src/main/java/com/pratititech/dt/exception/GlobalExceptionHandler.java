package com.pratititech.dt.exception;

import com.pratititech.dt.util.Result;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result handleUserNotFound(UserNotFoundException ex) {
        return new Result(false, HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ExceptionHandler(UserNotVerifiedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result handleUserNotVerified(UserNotVerifiedException ex) {
        return new Result(false, HttpStatus.FORBIDDEN.value(), ex.getMessage());
    }

    @ExceptionHandler(InvalidPasswordException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result handleInvalidPassword(InvalidPasswordException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("password", ex.getMessage());  // e.g. "Invalid password"

        return new Result(false, HttpStatus.UNAUTHORIZED.value(), "Validation failed", errors);
    }


    @ExceptionHandler(OtpExpiredException.class)
    @ResponseStatus(HttpStatus.GONE)
    public Result handleExpiredOtp(OtpExpiredException ex) {
        return new Result(false, HttpStatus.GONE.value(), ex.getMessage());
    }

    @ExceptionHandler(InvalidAgeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result handleInvalidAge(InvalidAgeException ex) {
        return new Result(false, HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result handleIllegalArgs(IllegalArgumentException ex) {
        return new Result(false, HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return new Result(false, HttpStatus.BAD_REQUEST.value(), "Validation failed", errors);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result handleGeneric(Exception ex) {
        ex.printStackTrace(); // Optionally use a logger here
        return new Result(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Something went wrong");
    }
}

package com.pratititech.dt.exception;

public class UserNotVerifiedException extends RuntimeException {
    public UserNotVerifiedException() {
        super();
    }
    public UserNotVerifiedException(String message) {
        super(message);
    }
}

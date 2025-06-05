package com.pratititech.dt.exception;

public class OtpInvalidException extends RuntimeException {
    public OtpInvalidException() {
        super();
    }
    public OtpInvalidException(String message) {
        super(message);
    }
}

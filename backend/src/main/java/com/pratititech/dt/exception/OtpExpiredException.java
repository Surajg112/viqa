package com.pratititech.dt.exception;

public class OtpExpiredException extends RuntimeException {
    public OtpExpiredException() {
        super();
    }
    public OtpExpiredException(String message) {
        super(message);
    }
}

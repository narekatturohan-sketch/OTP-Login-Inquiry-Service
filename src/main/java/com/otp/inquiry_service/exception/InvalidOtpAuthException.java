package com.otp.inquiry_service.exception;

public class InvalidOtpAuthException extends RuntimeException {
    public InvalidOtpAuthException(String message) {
        super(message);
    }
}
package com.otp.inquiry_service.controller;

import com.otp.inquiry_service.dto.OtpValidationDto;
import com.otp.inquiry_service.exception.InvalidOtpAuthException;
import com.otp.inquiry_service.service.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/otp-service")
@RequiredArgsConstructor
public class OtpController {
    private final OtpService otpService;

    @PostMapping("validate-otp")
    public ResponseEntity<String> validateOtp(@Valid @RequestBody OtpValidationDto request) {
        String accessToken = otpService.validateOtp(request.getTransactionId(),  request.getOtp());

        if (accessToken == null) {
            //return ResponseEntity.status(401).body("Invalid or Expired OTP");
            throw new InvalidOtpAuthException("Invalid or Expired OTP");
        }
        return ResponseEntity.ok(accessToken);
    }
}

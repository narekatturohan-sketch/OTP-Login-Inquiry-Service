package com.otp.inquiry_service.controller;

import com.otp.inquiry_service.dto.InquiryResponseDto;
import com.otp.inquiry_service.exception.ClientNotFoundException;
import com.otp.inquiry_service.exception.InvalidAuthenticationException;
import com.otp.inquiry_service.service.InquiryService;
import com.otp.inquiry_service.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/otp-service/inquiry")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;
    private final OtpService otpService;

    @GetMapping
    public ResponseEntity<InquiryResponseDto> getClientDetails(
            @RequestHeader(value = "Authorization", required = false)
            String authorization) {

        if (authorization == null || !authorization.startsWith("Bearer ")) {

            //return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            throw new InvalidAuthenticationException("Authorization token is missing");
        }

        String sessionId = authorization.substring(7);

        String boid = otpService.validateSession(sessionId);

        if (boid == null) {
            //return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            throw new InvalidAuthenticationException("Invalid session");
        }

        InquiryResponseDto response =
                inquiryService.getClientDetails(boid);

        if (response == null) {
            //return ResponseEntity.notFound().build();
            throw new ClientNotFoundException("Client not found");
        }

        return ResponseEntity.ok(response);
    }
}
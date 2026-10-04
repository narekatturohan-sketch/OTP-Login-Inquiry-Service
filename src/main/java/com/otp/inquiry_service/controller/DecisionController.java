package com.otp.inquiry_service.controller;

import com.otp.inquiry_service.dto.ClientDecisionDto;
import com.otp.inquiry_service.exception.ClientNotFoundException;
import com.otp.inquiry_service.exception.InvalidAuthenticationException;
import com.otp.inquiry_service.service.DecisionService;
import com.otp.inquiry_service.service.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/otp-service/decision")
@RequiredArgsConstructor
public class DecisionController {
    private final DecisionService decisionService;
    private final OtpService otpService;

    @PostMapping
    public ResponseEntity<String> processDecision(
            @RequestHeader(value = "Authorization", required = false)
            String authorization,
            @Valid @RequestBody ClientDecisionDto request
    ) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            //return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization token is missing");
            throw new InvalidAuthenticationException("Authorization token is missing");
        }

        String sessionId = authorization.substring(7);
        String clientId = otpService.validateSession(sessionId);

        if (clientId == null) {
            //return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid or expired session");
            throw new InvalidAuthenticationException("Invalid or expired session");
        }

        boolean updated = decisionService.ProcessDecision(clientId, request.getDecision());

        if (!updated) {
            //return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Client not found");
            throw new ClientNotFoundException("Client not found");
        }

        return ResponseEntity.ok("Client " + request.getDecision() + " successfully processed");
    }
}

package com.otp.inquiry_service.controller;
import com.otp.inquiry_service.dto.ClientLoginDto;
import com.otp.inquiry_service.exception.InvalidAuthenticationException;
import com.otp.inquiry_service.service.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/otp-service/login")
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

    @PostMapping
    public ResponseEntity<String> login(@Valid @RequestBody ClientLoginDto clientLoginDto) {
        String transactionId = loginService.login(clientLoginDto);

        if (transactionId == null) {
            //return ResponseEntity.status(401).body("Invalid BOID or PAN");
            throw new InvalidAuthenticationException("Invalid BOID or PAN");
        }

        return ResponseEntity.ok(transactionId);
    }
}

package com.otp.inquiry_service.dto;
import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpValidationDto {
    @NotBlank(message = "Transaction ID cannot be blank")
    private String transactionId;

    @NotBlank(message = "OTP is required")
    private String otp;
}

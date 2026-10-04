package com.otp.inquiry_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InquiryResponseDto {
    private String clientId;
    private String panNumber;
    private String fullName;
    private LocalDate dob;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String pincode;
    private String email;
    private String mobile;
    private String kycStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String decision;
}
package com.otp.inquiry_service.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "CLIENTS")
@Getter
@Setter
public class Clients {
    @Id
    @Column(name = "CLIENT_ID")
    private String clientId;

    @Column(name = "PAN_NUMBER")
    private String panNumber;

    @Column(name = "FULL_NAME")
    private String fullName;

    @Column(name = "DOB")
    private LocalDate dob;

    @Column(name = "ADDRESS_LINE1")
    private String addressLine1;

    @Column(name = "ADDRESS_LINE2")
    private String addressLine2;

    @Column(name = "CITY")
    private String city;

    @Column(name = "STATE")
    private String state;

    @Column(name = "PINCODE")
    private String pincode;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "MOBILE")
    private String mobile;

    @Column(name = "KYC_STATUS")
    private String kycStatus;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Column(name = "DECISION")
    private String decision;
}
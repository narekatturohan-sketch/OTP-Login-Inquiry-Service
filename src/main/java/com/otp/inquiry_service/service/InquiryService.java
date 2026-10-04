package com.otp.inquiry_service.service;

import com.otp.inquiry_service.dto.InquiryResponseDto;
import com.otp.inquiry_service.entity.Clients;
import com.otp.inquiry_service.repository.ClientsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final ClientsRepository clientsRepository;

    public InquiryResponseDto getClientDetails(String clientId) {

        Clients client = clientsRepository
                .findByClientId(clientId)
                .orElse(null);

        if (client == null) {
            return null;
        }

        return new InquiryResponseDto(
                client.getClientId(),
                client.getPanNumber(),
                client.getFullName(),
                client.getDob(),
                client.getAddressLine1(),
                client.getAddressLine2(),
                client.getCity(),
                client.getState(),
                client.getPincode(),
                client.getEmail(),
                client.getMobile(),
                client.getKycStatus(),
                client.getCreatedAt(),
                client.getUpdatedAt(),
                client.getDecision()
        );
    }
}
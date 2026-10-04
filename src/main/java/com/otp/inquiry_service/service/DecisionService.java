package com.otp.inquiry_service.service;
import com.otp.inquiry_service.dto.ClientDecisionDto;
import com.otp.inquiry_service.entity.Clients;
import com.otp.inquiry_service.repository.ClientsRepository;
import io.lettuce.core.failover.api.InitializationPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DecisionService {
    private final ClientsRepository clientsRepository;

    @Transactional
    public boolean ProcessDecision(String clientId,
                                   ClientDecisionDto.Decision decision) {

        Clients client = clientsRepository.findByClientId(clientId).orElse(null);

        if (client == null) {
            return false;
        }

        client.setUpdatedAt(LocalDateTime.now());
        client .setDecision(String.valueOf(decision));
        clientsRepository.save(client);

        return true;
    }
}

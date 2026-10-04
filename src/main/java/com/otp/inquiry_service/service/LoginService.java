package com.otp.inquiry_service.service;

import com.otp.inquiry_service.dto.ClientLoginDto;
import com.otp.inquiry_service.repository.ClientsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

    private final ClientsRepository clientsRepository;
    private final OtpService otpService;

    public boolean validateClient(ClientLoginDto request) {
        String pan = request.getPan();
        Number boid = request.getBoid();

        log.info("pan:{} boid:{}", pan, boid);

        return clientsRepository.findByClientIdAndPanNumber(boid, pan).isPresent();
    }

    public String login(ClientLoginDto request) {
        boolean valid = validateClient(request);
        if (!valid) {
            return null;
        }

        String otp = otpService.generateOtp();
        String transactionId = otpService.createTransactionId();
        otpService.storeOtp(transactionId, otp);
        otpService.storeTransactionClient(
                transactionId,
                request.getBoid()
        );
        log.info("otp:{} transactionId:{}", otp, transactionId);
        return transactionId;
    }
}

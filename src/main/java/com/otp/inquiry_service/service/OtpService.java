package com.otp.inquiry_service.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
public class OtpService {
    private static final Duration TRANSACTION_TTL = Duration.ofMinutes(5);
    private final StringRedisTemplate redisTemplate;
    private final SecureRandom secureRandom = new SecureRandom();
    private static final Duration OTP_TTL = Duration.ofMinutes(5);
    private static final Duration SESSION_TTL = Duration.ofMinutes(15);

    public OtpService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String generateOtp() {
        int otp = 100000 + secureRandom.nextInt(900000);
        return String.valueOf(otp);
    }

    public String createTransactionId() {
        return UUID.randomUUID().toString();
    }

    public void storeOtp(String transactionId, String otp) {
        String key = "otp:INQUIRY_AUTH:" + transactionId;
        log.info("key:{}", key);
        redisTemplate.opsForValue().set(key, otp, OTP_TTL);
    }

    public String getOtp(String transactionId) {
        String key = "otp:INQUIRY_AUTH:" + transactionId;
        log.info("key:{}", key);
        return redisTemplate.opsForValue().get(key);
    }

    public String validateOtp(String transactionId, String otp) {
        String storedOtp = getOtp(transactionId);
        log.info("storedOtp:{}", storedOtp);
        log.info("otp:{}", otp);
        if (storedOtp == null) {
            return null;
        }

        if (!storedOtp.equals(otp)) {
            return null;
        }
        String transactionKey = "otp:TRANSACTION:" + transactionId;
        log.info("transactionKey:{}", transactionKey);
        String boid = redisTemplate.opsForValue().get(transactionKey);
        log.info("boid:{}", transactionKey);

        if (boid == null) {
            return null;
        }

        String sessionId = UUID.randomUUID().toString();
        log.info("sessionId:{}", sessionId);
        storeSessionId(sessionId, boid);
        redisTemplate.delete("otp:INQUIRY_AUTH:" + transactionId);
        redisTemplate.delete(transactionKey);
        return sessionId;
    }

    public void storeSessionId(String sessionId, String boid) {
        String key = "otp:SESSION:" + sessionId;
        log.info("storeSessionId key:{}", key);
        redisTemplate.opsForValue().set(key, boid, SESSION_TTL);
    }

    public void storeTransactionClient(String transactionId, Number boid) {

        String key = "otp:TRANSACTION:" + transactionId;
        log.info("storeTransactionClient key:{}", key);
        redisTemplate.opsForValue().set(
                key,
                boid.toString(),
                TRANSACTION_TTL
        );
    }

    public String validateSession(String sessionId) {

        String key = "otp:SESSION:" + sessionId;
        log.info("validateSession key:{}", key);
        return redisTemplate.opsForValue().get(key);
    }
}

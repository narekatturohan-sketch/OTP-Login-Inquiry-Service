package com.otp.inquiry_service.repository;
import com.otp.inquiry_service.entity.Clients;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ClientsRepository extends JpaRepository<Clients, Long> {
    Optional<Clients> findByClientIdAndPanNumber(Number clientId, String panNumber);
    Optional<Clients> findByClientId(String clientId);
}
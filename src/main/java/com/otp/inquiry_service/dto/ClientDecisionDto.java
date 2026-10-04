package com.otp.inquiry_service.dto;
import io.lettuce.core.failover.api.InitializationPolicy;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDecisionDto {

    @NotNull(message = "Decision Cannot be Null")
    private Decision decision;

    public enum Decision {
        ACCEPT,
        REJECT
    }
}

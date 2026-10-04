package com.otp.inquiry_service.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientLoginDto {
    @NotNull(message = "BOID cannot be Blank")
    private Number boid;

    @Pattern(
            regexp = "[A-Z]{5}[0-9]{4}[A-Z]",
            message = "Invalid PAN format"
    )
    private String pan;
}

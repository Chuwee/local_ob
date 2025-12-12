package es.onebox.common.datasources.distribution.dto.order;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

public record PaymentRequest(
        @NotNull(message = "type is required")
        PaymentType type,
        @NotNull(message = "value is required")
        Double value,
        String reference,
        String merchant,
        @JsonProperty("gateway_sid")
        String gatewaySid
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 7424295449405885213L;
}

package es.onebox.common.datasources.distribution.dto.order;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record ConfirmRequest(
        List<PaymentRequest> payments,
        @JsonProperty("external_code")
        String externalCode,
        Boolean forceMultiTicket
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 7424295449405885213L;


}

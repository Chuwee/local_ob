package es.onebox.common.datasources.distribution.dto.order;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public record OrderStatus(
        @JsonProperty("type")
        OrderStatusType type,
        @JsonProperty("expires_in")
        OrderExpiration expiresIn
) implements Serializable {
    @Serial
    private static final long serialVersionUID = -3365872811569917966L;
}

package es.onebox.common.datasources.distribution.dto.order.items;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;

import java.io.Serial;
import java.io.Serializable;

public record ProductDelivery(
        @JsonProperty("session")
        IdNameDTO session,
        @JsonProperty("point")
        IdNameDTO point,
        @JsonProperty("date")
        ProductDeliveryDate date,
        @JsonProperty("type")
        ProductDeliveryType type

) implements Serializable {
    @Serial
    private static final long serialVersionUID = -7248696302212177674L;
}

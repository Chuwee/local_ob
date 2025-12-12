package es.onebox.common.datasources.distribution.dto.order.price;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public record B2BPrice(
        @JsonProperty("discount")
        Double discount,
        @JsonProperty("commission")
        Double commission
) implements Serializable {

    @Serial
    private static final long serialVersionUID = -2306477026637870905L;
}

package es.onebox.common.datasources.distribution.dto.order.items;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public record Reallocation(
        @JsonProperty("seat_id")
        Long seatId,
        @JsonProperty("order_code")
        String orderCode,
        ItemSeatAllocation allocation
) implements Serializable {
    @Serial
    private static final long serialVersionUID = -7248696302212177674L;
}

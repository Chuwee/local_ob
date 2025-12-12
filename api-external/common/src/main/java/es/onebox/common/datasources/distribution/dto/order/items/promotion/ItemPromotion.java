package es.onebox.common.datasources.distribution.dto.order.items.promotion;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public record ItemPromotion(
        @JsonProperty("id")
        Long id,
        @JsonProperty("name")
        String name,
        @JsonProperty("type")
        OrderPromotionType type,
        @JsonProperty("subtype")
        PromotionSubtype subtype,
        @JsonProperty("non_cumulative")
        Boolean nonCumulative,
        @JsonProperty("activator")
        CollectiveApplied activator
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 2034604615207233669L;
}

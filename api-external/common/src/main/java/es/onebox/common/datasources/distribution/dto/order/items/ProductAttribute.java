package es.onebox.common.datasources.distribution.dto.order.items;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public record ProductAttribute(
        @JsonProperty("id")
        Long id,
        @JsonProperty("name")
        String name,
        @JsonProperty("texts")
        ProductText texts,
        @JsonProperty("values")
        ProductValue values

) implements Serializable {
    @Serial
    private static final long serialVersionUID = -7248696302212177674L;
}

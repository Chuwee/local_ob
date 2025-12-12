package es.onebox.common.datasources.distribution.dto.order.items;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public record ProductData(
        @JsonProperty("id")
        Long id,
        @JsonProperty("name")
        String name,
        @JsonProperty("type")
        ProductType type,
        @JsonProperty("texts")
        ProductText texts,
        @JsonProperty("variant_id")
        Long variantId,
        @JsonProperty("attribute_1")
        ProductAttribute attribute1,
        @JsonProperty("attribute_2")
        ProductAttribute attribute2,
        @JsonProperty("delivery")
        ProductDelivery delivery
) implements Serializable {
    @Serial
    private static final long serialVersionUID = -7248696302212177674L;
}

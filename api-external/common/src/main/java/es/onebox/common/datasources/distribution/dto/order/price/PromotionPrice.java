package es.onebox.common.datasources.distribution.dto.order.price;

import es.onebox.common.datasources.distribution.dto.order.items.promotion.OrderPromotionType;
import es.onebox.common.datasources.distribution.dto.order.items.promotion.PromotionSubtype;

import java.io.Serial;
import java.io.Serializable;

public record PromotionPrice(
        OrderPromotionType type,
        PromotionSubtype subtype,
        Double value
) implements Serializable {
    @Serial
    private static final long serialVersionUID = -6871985033282400213L;
}

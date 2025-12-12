package es.onebox.common.datasources.distribution.dto.order;

import es.onebox.common.datasources.distribution.dto.order.items.promotion.CollectiveApplied;
import es.onebox.common.datasources.distribution.dto.order.items.promotion.OrderPromotionType;
import es.onebox.common.datasources.distribution.dto.order.items.promotion.PromotionSubtype;

import java.io.Serial;
import java.io.Serializable;

public record OrderPromotion(
        Long id,
        String name,
        OrderPromotionType type,
        PromotionSubtype subtype,
        CollectiveApplied activator
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 5289474904812707351L;
}

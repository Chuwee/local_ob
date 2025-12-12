package es.onebox.common.datasources.distribution.dto.order.price;

import java.io.Serial;
import java.io.Serializable;

public record SurchargePrice(
        SurchargeType type,
        Double value
) implements Serializable {
    @Serial
    private static final long serialVersionUID = -5444479572157869759L;
}

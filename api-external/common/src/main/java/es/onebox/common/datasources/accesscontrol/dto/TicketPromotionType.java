package es.onebox.common.datasources.accesscontrol.dto;

import java.util.Arrays;

public enum TicketPromotionType {
    AUTOMATIC(1),
    PROMOTION(2),
    DISCOUNT(3);

    private final Integer type;

    TicketPromotionType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return this.type;
    }

    public static TicketPromotionType byType(Integer type) {
        return Arrays.stream(TicketPromotionType.values())
                .filter(p -> type.equals(p.getType()))
                .findFirst().orElse(null);
    }
}

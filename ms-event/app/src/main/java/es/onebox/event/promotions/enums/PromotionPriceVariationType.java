package es.onebox.event.promotions.enums;

import java.util.Arrays;

public enum PromotionPriceVariationType {

    FIXED(0),
    PERCENTAGE(1),
    NEW_BASE_PRICE(2),
    NO_DISCOUNT(3);

    private final Integer id;

    PromotionPriceVariationType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static PromotionPriceVariationType fromId(Integer id) {
        return Arrays.stream(PromotionPriceVariationType.values())
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}

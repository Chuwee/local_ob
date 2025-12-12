package es.onebox.ms.notification.webhooks.enums.promotion;

import java.util.stream.Stream;

public enum PromotionDiscountType {
    FIXED(0),
    PERCENTAGE(1),
    BASE_PRICE(2),
    NO_DISCOUNT(3);

    private Integer id;

    PromotionDiscountType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static PromotionDiscountType fromId(Integer id) {
        return Stream.of(values())
                .filter(p -> p.id.equals(id))
                .findAny()
                .orElse(null);
    }
}

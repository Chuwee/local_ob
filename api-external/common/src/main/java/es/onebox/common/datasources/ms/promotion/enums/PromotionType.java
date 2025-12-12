package es.onebox.common.datasources.ms.promotion.enums;

import java.util.stream.Stream;

public enum PromotionType {
    AUTOMATIC(1),
    PROMOTION(2),
    DISCOUNT(3);

    private Integer type;

    PromotionType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return this.type;
    }

    public static PromotionType get(Integer id) {
        return Stream.of(values()).filter(p -> p.type.equals(id)).findAny().orElse(null);
    }
}

package es.onebox.mgmt.products.promotions.enums;

import java.util.stream.Stream;

public enum ProductPromotionActivator {

    DEFAULT(0),
    COLLECTIVE(1);

    private Integer type;

    ProductPromotionActivator(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return this.type;
    }

    public static ProductPromotionActivator get(Integer id) {
        return Stream.of(values()).filter(p -> p.type.equals(id)).findAny().orElse(null);
    }

    public static ProductPromotionActivator fromId(Integer type) {
        return Stream.of(values())
                .filter(p -> p.type.equals(type))
                .findAny()
                .orElse(null);
    }
}

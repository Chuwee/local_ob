package es.onebox.mgmt.products.promotions.enums;

import java.util.stream.Stream;

public enum ProductPromotionType {

    AUTOMATIC(1);

    private Integer type;

    ProductPromotionType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    public static ProductPromotionType fromId(Integer type) {
        return Stream.of(values())
                .filter(p -> p.type.equals(type))
                .findAny()
                .orElse(null);
    }

}

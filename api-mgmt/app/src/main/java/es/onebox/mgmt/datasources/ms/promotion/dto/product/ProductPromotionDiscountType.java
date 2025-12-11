package es.onebox.mgmt.datasources.ms.promotion.dto.product;

import java.util.stream.Stream;

public enum ProductPromotionDiscountType {

    FIXED(0),
    PERCENTAGE(1);

    private Integer id;

    ProductPromotionDiscountType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static ProductPromotionDiscountType fromId(Integer id) {
        return Stream.of(values())
                .filter(p -> p.id.equals(id))
                .findAny()
                .orElse(null);
    }
}

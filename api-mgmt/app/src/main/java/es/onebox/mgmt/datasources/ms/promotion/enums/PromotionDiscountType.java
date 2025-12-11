package es.onebox.mgmt.datasources.ms.promotion.enums;

import java.io.Serializable;

public enum PromotionDiscountType implements Serializable {

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

}

package es.onebox.mgmt.datasources.ms.promotion.enums;

import java.io.Serializable;

public enum PromotionType implements Serializable {
    AUTOMATIC(1),
    PROMOTION(2),
    DISCOUNT(3);

    private int type;

    private PromotionType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    public static PromotionType get(int type) {
        return values()[type - 1];
    }
}

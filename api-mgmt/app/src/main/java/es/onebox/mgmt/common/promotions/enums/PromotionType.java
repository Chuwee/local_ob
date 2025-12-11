package es.onebox.mgmt.common.promotions.enums;

public enum PromotionType {
    AUTOMATIC(1),
    PROMOTION(2),
    DISCOUNT(3);

    private int type;

    PromotionType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

}

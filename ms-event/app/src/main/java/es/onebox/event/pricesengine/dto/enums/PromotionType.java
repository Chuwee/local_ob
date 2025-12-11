package es.onebox.event.pricesengine.dto.enums;

public enum PromotionType {

    AUTOMATIC(1), PROMOTION(2), DISCOUNT(3);

    private int id;

    PromotionType(int id) {
        this.id = id;
    }

    public static PromotionType getById(int id) {
        return switch (id) {
            case 1 -> AUTOMATIC;
            case 2 -> PROMOTION;
            case 3 -> DISCOUNT;
            default -> null;
        };

    }

    public int getId() {
        return id;
    }
}

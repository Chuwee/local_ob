package es.onebox.event.priceengine.simulation.domain.enums;

public enum PromotionType {

    AUTOMATIC(1), PROMOTION(2), DISCOUNT(3);

    private int id;

    PromotionType(int id) {
        this.id = id;
    }

    public static PromotionType getById(int id) {
        switch (id) {
            case 1:
                return AUTOMATIC;
            case 2:
                return PROMOTION;
            case 3:
                return DISCOUNT;
            default:
                return  null;
        }

    }

    public int getId() {
        return id;
    }
}

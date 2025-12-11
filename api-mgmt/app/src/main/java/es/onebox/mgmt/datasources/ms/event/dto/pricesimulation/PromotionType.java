package es.onebox.mgmt.datasources.ms.event.dto.pricesimulation;

public enum PromotionType {

    AUTOMATIC(1), PROMOTION(2), DISCOUNT(3);

    private int id;

    PromotionType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}

package es.onebox.mgmt.salerequests.pricesimulation.dto.enums;

public enum PromotionTypeDTO {

    AUTOMATIC(1), PROMOTION(2), DISCOUNT(3);

    private int id;

    PromotionTypeDTO(int id) {
        this.id = id;
    }

    public static PromotionTypeDTO getById(int id) {
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

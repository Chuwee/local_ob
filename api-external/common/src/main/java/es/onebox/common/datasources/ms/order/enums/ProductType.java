package es.onebox.common.datasources.ms.order.enums;

public enum ProductType {
    SEAT(1),
    PRODUCT(2),
    GROUP(3);

    private int id;

    private ProductType(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static ProductType get(int id) {
        return values()[id - 1];
    }
}

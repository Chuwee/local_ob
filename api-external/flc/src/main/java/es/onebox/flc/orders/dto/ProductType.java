package es.onebox.flc.orders.dto;

public enum ProductType {
    SEAT(1),
    PRODUCT(2),
    GROUP(3);

    private int id;

    ProductType(int id)
    {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static ProductType get(int id) {
        return values()[id -1];
    }
}

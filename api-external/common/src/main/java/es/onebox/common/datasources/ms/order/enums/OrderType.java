package es.onebox.common.datasources.ms.order.enums;

public enum OrderType {
    SHOPPING_CART(0),
    PURCHASE(1),
    BOOKING(2),
    ISSUE(3),
    REFUND(4),
    SEC_MKT_PURCHASE(5),
    SEAT_REALLOCATION(6);

    private int id;

    private OrderType(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static OrderType get(int id) {
        return values()[id];
    }
}

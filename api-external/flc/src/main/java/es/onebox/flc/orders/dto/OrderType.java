package es.onebox.flc.orders.dto;

public enum OrderType {
    SHOPPING_CART(0),
    PURCHASE(1),
    BOOKING(2),
    ISSUE(3),
    REFUND(4),
    SEC_MKT_PURCHASE(5),
    SEAT_REALLOCATION(6);

    private int id;

    OrderType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static OrderType get(int id){
        return values()[id];
    }
}

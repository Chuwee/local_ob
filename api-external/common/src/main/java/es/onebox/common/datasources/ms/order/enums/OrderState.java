package es.onebox.common.datasources.ms.order.enums;

public enum OrderState {
    PRE_ORDER(0),
    CONFIRMED(1),
    PAID(2),
    CANCELLED(3),
    EXPIRED(4),
    PENDING(5);

    private int id;

    private OrderState(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static OrderState get(int id) {
        return values()[id];
    }
}

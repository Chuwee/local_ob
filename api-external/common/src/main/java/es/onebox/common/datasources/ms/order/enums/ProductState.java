package es.onebox.common.datasources.ms.order.enums;

public enum ProductState {
    PENDING(1),
    SOLD(2),
    REFUNDED(3),
    EXPIRED(4),
    SEC_MKT_LOCKED(5),
    SEC_MKT_SOLD(6),
    SEC_MKT_PARTIAL_LOCKED(7),
    SEC_MKT_PARTIAL_SOLD(8);

    private int id;

    private ProductState(int id) {
        this.id = id;
    }

    public static ProductState get(int id) {
        return values()[id];
    }

    public int getId() {
        return this.id;
    }
}

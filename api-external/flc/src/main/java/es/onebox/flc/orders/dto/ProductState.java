package es.onebox.flc.orders.dto;

public enum ProductState {
    PENDING(1),
    SOLD(2),
    REFUNDED(3),
    EXPIRED(4);

    private int id;

    ProductState(int id) {
        this.id = id;
    }

    public static ProductState get(int id){
        return values()[id-1];
    }

    public int getId() {
        return id;
    }
}

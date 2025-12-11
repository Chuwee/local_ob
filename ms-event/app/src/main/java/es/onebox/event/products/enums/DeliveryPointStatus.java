package es.onebox.event.products.enums;

public enum DeliveryPointStatus {
    DELETED(0),
    ACTIVE(1),
    INACTIVE(2);

    private final int status;

    DeliveryPointStatus(int status) {
        this.status = status;
    }

    public int getId() {
        return status;
    }

    public static DeliveryPointStatus get(int id) {
        return values()[id];
    }

    @Override
    public String toString() {
        return String.valueOf(status);
    }

}

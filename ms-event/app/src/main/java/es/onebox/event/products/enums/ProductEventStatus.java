package es.onebox.event.products.enums;

import java.util.Arrays;

public enum ProductEventStatus {
    DELETED(0),
    ACTIVE(1),
    INACTIVE(2);

    private final int status;

    ProductEventStatus(int status) {
        this.status = status;
    }

    public int getId() {
        return status;
    }

    public static ProductEventStatus get(int id) {
        return Arrays.stream(values()).filter(t -> t.getId() == id).findAny()
                .orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public String toString() {
        return String.valueOf(status);
    }
}

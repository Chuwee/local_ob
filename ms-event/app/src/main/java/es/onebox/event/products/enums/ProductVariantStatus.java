package es.onebox.event.products.enums;

import java.util.Arrays;

public enum ProductVariantStatus {
    INACTIVE(0),
    ACTIVE(1);
    private final int status;

    ProductVariantStatus(int status) {
        this.status = status;
    }

    public int getId() {
        return status;
    }

    public static ProductVariantStatus get(int id) {
        return Arrays.stream(values()).filter(t -> t.getId() == id).findAny()
                .orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public String toString() {
        return String.valueOf(status);
    }
}

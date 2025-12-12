package es.onebox.common.datasources.ms.event.enums;

import java.util.Arrays;

public enum ProductStockType {
    BOUNDED(1),
    UNBOUNDED(2),
    SESSION_BOUNDED(3);

    private final int id;

    ProductStockType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static ProductStockType get(Integer id) {
        if(id == null){
            throw new IllegalArgumentException("ProductStockType id cannot be null");
        }
        return Arrays.stream(values()).filter(t -> t.getId() == id).findAny()
                .orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}

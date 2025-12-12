package es.onebox.common.datasources.ms.event.enums;

import java.util.Arrays;

public enum ProductType {
    SIMPLE(1),
    VARIANT(2);

    private final int id;

    ProductType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static ProductType get(Integer id) {
        if(id == null){
            throw new IllegalArgumentException("ProductType id cannot be null");
        }
        return Arrays.stream(values()).filter(t -> t.getId() == id).findAny()
                .orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}

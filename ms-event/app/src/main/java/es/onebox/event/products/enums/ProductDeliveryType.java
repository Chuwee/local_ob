package es.onebox.event.products.enums;

import java.util.Arrays;

public enum ProductDeliveryType {
    SESSION(1),
    PURCHASE(2),
    FIXED_DATES(3);

    private final int id;

    ProductDeliveryType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static ProductDeliveryType get(Integer id) {
        if(id == null){
            throw new IllegalArgumentException("ProductDeliveryType id cannot be null");
        }
        return Arrays.stream(values()).filter(t -> t.getId() == id).findAny()
                .orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }

}

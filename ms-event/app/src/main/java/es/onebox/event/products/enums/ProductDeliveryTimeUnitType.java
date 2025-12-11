package es.onebox.event.products.enums;

import java.util.Arrays;

public enum ProductDeliveryTimeUnitType {
    MINUTES(1),
    HOURS(2),
    DAYS(3),
    WEEKS(4),
    MONTHS(5);

    private final int status;

    ProductDeliveryTimeUnitType(int status) {
        this.status = status;
    }

    public int getId() {
        return status;
    }

    public static ProductDeliveryTimeUnitType get(Integer id) {
        if(id == null){
            throw new IllegalArgumentException("ProductDeliveryType id cannot be null");
        }
        return Arrays.stream(values())
                .filter(v -> v.getId() == id)
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public String toString() {
        return String.valueOf(status);
    }

}

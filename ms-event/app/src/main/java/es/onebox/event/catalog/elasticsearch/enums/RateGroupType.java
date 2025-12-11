package es.onebox.event.catalog.elasticsearch.enums;

import java.util.Arrays;

public enum RateGroupType {
    PRODUCT(1),
    RATE(2);

    private final Integer id;

    RateGroupType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return this.id;
    }

    public static RateGroupType fromId(Integer id) {
        if (id == null) {
            return null;
        }
        return Arrays.stream(RateGroupType.values())
                .filter(rateGroupType -> rateGroupType.getId().equals(id)).findFirst()
                .orElse(null);
    }
}

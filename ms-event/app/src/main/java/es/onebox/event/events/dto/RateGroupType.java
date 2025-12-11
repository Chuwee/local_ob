package es.onebox.event.events.dto;

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
        return Arrays.stream(RateGroupType.values())
                .filter(rateGroupType -> rateGroupType.getId().equals(id)).findFirst()
                .orElse(null);
    }
}

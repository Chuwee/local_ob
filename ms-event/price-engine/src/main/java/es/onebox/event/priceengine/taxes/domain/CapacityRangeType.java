package es.onebox.event.priceengine.taxes.domain;

import java.util.stream.Stream;

public enum CapacityRangeType {
    TOTAL(0),
    PERCENT(1);

    private int id;

    CapacityRangeType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static CapacityRangeType getById(int id) {
        return Stream.of(values())
                .filter(cs -> cs.id == id)
                .findAny()
                .orElse(null);
    }

}

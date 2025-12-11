package es.onebox.mgmt.events.enums;

import java.io.Serializable;
import java.util.stream.Stream;

public enum TierConditionDTO implements Serializable {
    DATE(1),
    STOCK_OR_DATE(2);

    TierConditionDTO(int id) {
        this.id = id;
    }

    private final int id;

    public int getId() {
        return id;
    }

    public static TierConditionDTO getById(int id) {
        return Stream.of(values())
                .filter(cond -> cond.getId() == id)
                .findAny()
                .orElse(null);
    }
}

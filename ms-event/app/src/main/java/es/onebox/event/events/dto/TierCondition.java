package es.onebox.event.events.dto;

import java.io.Serializable;
import java.util.stream.Stream;

public enum TierCondition implements Serializable {
    DATE(1),
    STOCK_OR_DATE(2);

    TierCondition(int id) {
        this.id = id;
    }

    private final int id;

    public int getId() {
        return id;
    }

    public static TierCondition getById(int id) {
        return Stream.of(values())
                .filter(cond -> cond.getId() == id)
                .findAny()
                .orElse(null);
    }
}

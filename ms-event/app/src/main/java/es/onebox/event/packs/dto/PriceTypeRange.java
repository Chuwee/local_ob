package es.onebox.event.packs.dto;

import java.util.stream.Stream;

public enum PriceTypeRange {
    ALL(0),
    RESTRICTED(1);

    private final Integer type;

    PriceTypeRange(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return this.type;
    }

    public static PriceTypeRange getByType(final Integer type) {
        return Stream.of(values()).filter(field -> field.getType().equals(type)).findFirst().orElse(null);
    }
}

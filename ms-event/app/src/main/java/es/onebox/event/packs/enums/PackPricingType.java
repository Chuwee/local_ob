package es.onebox.event.packs.enums;

import java.util.stream.Stream;

public enum PackPricingType {
    COMBINED(1),
    INCREMENTAL(2),
    NEW_PRICE(3);

    private final Integer id;

    PackPricingType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return this.id;
    }

    public static PackPricingType getById(Integer id) {
        return Stream.of(values())
                .filter(type -> type.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}

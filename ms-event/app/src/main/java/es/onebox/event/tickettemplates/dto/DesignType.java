package es.onebox.event.tickettemplates.dto;

import java.util.Objects;
import java.util.stream.Stream;

public enum DesignType {
    PDF(1),
    ZPL_GENERAL(2),
    ZPL_IVA(3),
    ZPL_PRICE_ZONE(4),
    ZPL_WONDERLAND(5),
    ZPL_NO_CHANNEL(6),
    ZPL_LONG_TITLE(7),
    ZPL_PRICE_ZONE_NO_CHANNEL(8),
    ZPL_ACTIVITY(9),
    ZPL_ACTIVITY_GROUPS(10),
    ZPL_CODE_ZEIC(11),
    PRODUCT(13),
    HTML(14);

    private static final long serialVersionUID = 1L;

    private final Integer value;

    DesignType(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public static DesignType byValue(Integer id) {
        return Stream.of(DesignType.values())
                .filter(value -> Objects.equals(value.value, id))
                .findFirst()
                .orElse(null);
    }
}

package es.onebox.mgmt.datasources.ms.promotion.enums;

import java.util.stream.Stream;

public enum PromotionValidityType {
    EVENT(0), PERIOD(1), SESION(2);

    private Integer id;

    PromotionValidityType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static PromotionValidityType fromId(Integer id) {
        return Stream.of(values()).filter(p -> p.id.equals(id)).findAny().orElse(null);
    }
}

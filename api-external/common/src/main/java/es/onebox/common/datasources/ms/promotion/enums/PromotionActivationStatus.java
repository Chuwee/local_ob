package es.onebox.common.datasources.ms.promotion.enums;

import java.util.stream.Stream;

public enum PromotionActivationStatus {
    ACTIVE(1),
    INACTIVE(0);

    private Integer id;

    PromotionActivationStatus(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static PromotionActivationStatus fromId(Integer id) {
        return Stream.of(values())
                .filter(p -> p.id.equals(id))
                .findAny()
                .orElse(null);
    }
}

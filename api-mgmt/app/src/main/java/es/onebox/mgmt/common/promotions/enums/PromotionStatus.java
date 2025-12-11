package es.onebox.mgmt.common.promotions.enums;

import java.util.stream.Stream;

public enum PromotionStatus {
    ACTIVE(1), INACTIVE(0);

    private Integer id;

    PromotionStatus(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static PromotionStatus fromId(Integer id) {
        return Stream.of(values()).filter(p -> p.id.equals(id)).findAny().orElse(null);
    }
}

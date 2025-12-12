package es.onebox.ms.notification.webhooks.enums.promotion;


import org.apache.commons.lang3.BooleanUtils;

public enum PromotionTargetType {
    ALL(0),
    RESTRICTED(1);

    private Integer id;

    PromotionTargetType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static PromotionTargetType fromBoolean(Boolean value) {
        if (value == null) {
            return null;
        }
        return BooleanUtils.isTrue(value) ? RESTRICTED : ALL;
    }

    public static PromotionTargetType fromInteger(Integer value) {
        if (value == null) {
            return null;
        } else if (value == 0) {
            return ALL;
        } else {
            return RESTRICTED;
        }
    }
}

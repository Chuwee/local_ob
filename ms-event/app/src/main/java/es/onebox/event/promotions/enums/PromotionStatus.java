package es.onebox.event.promotions.enums;

/**
 * @author ignasi
 */
public enum PromotionStatus {

    ACTIVE,
    INACTIVE,
    DELETED;

    public static PromotionStatus from(Boolean isActive, Integer status) {
        if (Integer.valueOf(0).equals(status)) {
            return DELETED;
        }
        return isActive ? ACTIVE : INACTIVE;
    }
}

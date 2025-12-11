package es.onebox.mgmt.channels.purchaseconfig.enums;

public enum ChannelPromotionCodePersistence {
    MAINTAIN_AFTER_VALIDATION(Boolean.TRUE),
    DISAPPEAR_AFTER_VALIDATION(Boolean.FALSE);

    boolean keepSalesCode;

    ChannelPromotionCodePersistence(Boolean keepSalesCode) {
        this.keepSalesCode = keepSalesCode;
    }

    public Boolean getKeepSalesCode() {
        return keepSalesCode;
    }

    public static ChannelPromotionCodePersistence fromBoolean(boolean b) {
        return b ? MAINTAIN_AFTER_VALIDATION : DISAPPEAR_AFTER_VALIDATION;
    }
}

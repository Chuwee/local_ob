package es.onebox.mgmt.datasources.ms.promotion.enums;

import java.io.Serializable;

public enum ChannelPromotionType implements Serializable {
    AUTOMATIC(1),
    COLLECTIVE(2);

    private int type;

    private ChannelPromotionType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    public static ChannelPromotionType get(int type) {
        return values()[type - 1];
    }
}

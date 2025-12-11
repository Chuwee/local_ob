package es.onebox.mgmt.channels.promotions.enums;

public enum ChannelPromotionType {
    AUTOMATIC(1),
    COLLECTIVE(2);

    private int type;

    ChannelPromotionType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

}

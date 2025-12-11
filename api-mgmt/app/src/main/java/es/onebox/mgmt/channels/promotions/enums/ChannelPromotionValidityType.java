package es.onebox.mgmt.channels.promotions.enums;

import java.util.stream.Stream;

public enum ChannelPromotionValidityType {

    CHANNEL(0), PERIOD(1);

    private Integer id;

    ChannelPromotionValidityType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static ChannelPromotionValidityType fromId(Integer id) {
        return Stream.of(values()).filter(p -> p.id.equals(id)).findAny().orElse(null);
    }
}

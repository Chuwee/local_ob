package es.onebox.mgmt.channels.promotions.enums;

import java.util.stream.Stream;

public enum ChannelPromotionDiscountType {
    FIXED(0),
    PERCENTAGE(1),
    DYNAMIC(2);

    private Integer id;

    ChannelPromotionDiscountType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static ChannelPromotionDiscountType fromId(Integer id) {
        return Stream.of(values())
                .filter(p -> p.id.equals(id))
                .findAny()
                .orElse(null);
    }
}

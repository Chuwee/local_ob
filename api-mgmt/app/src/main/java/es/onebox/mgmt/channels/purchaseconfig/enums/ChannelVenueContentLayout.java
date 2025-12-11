package es.onebox.mgmt.channels.purchaseconfig.enums;

import java.util.Arrays;

public enum ChannelVenueContentLayout {
    AUTO_DETECT(0),
    DESKTOP(1),
    MOBILE(2);

    private final Integer id;

    ChannelVenueContentLayout(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static ChannelVenueContentLayout fromId(Integer id) {
        return Arrays.stream(values())
                .filter(v -> v.id.equals(id)).findFirst().orElse(null);
    }

}

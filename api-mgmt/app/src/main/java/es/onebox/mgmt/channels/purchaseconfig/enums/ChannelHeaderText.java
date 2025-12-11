package es.onebox.mgmt.channels.purchaseconfig.enums;

import java.util.Arrays;

public enum ChannelHeaderText {
    SEAT_SELECTION(es.onebox.mgmt.datasources.ms.channel.enums.ChannelHeaderText.SELECT_LOCATIONS),
    PURCHASE_OPTIONS_AND_CLIENT_INFO(es.onebox.mgmt.datasources.ms.channel.enums.ChannelHeaderText.USER_DATA),
    PURCHASE_SUMMARY(es.onebox.mgmt.datasources.ms.channel.enums.ChannelHeaderText.SUMMARY),
    PAYMENT_GATEWAY(es.onebox.mgmt.datasources.ms.channel.enums.ChannelHeaderText.PAYMENT_GATEWAY);

    private final es.onebox.mgmt.datasources.ms.channel.enums.ChannelHeaderText value;

    ChannelHeaderText(es.onebox.mgmt.datasources.ms.channel.enums.ChannelHeaderText value) {
        this.value = value;
    }

    public static ChannelHeaderText getFromValue(es.onebox.mgmt.datasources.ms.channel.enums.ChannelHeaderText value) {
        return Arrays.stream(values())
                .filter(elem -> elem.value.equals(value))
                .findFirst()
                .orElse(null);
    }

    public es.onebox.mgmt.datasources.ms.channel.enums.ChannelHeaderText getValue() {
        return value;
    }
}

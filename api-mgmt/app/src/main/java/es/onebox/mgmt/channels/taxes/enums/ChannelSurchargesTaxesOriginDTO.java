package es.onebox.mgmt.channels.taxes.enums;

import es.onebox.mgmt.datasources.ms.channel.enums.ChannelSurchargesTaxesOrigin;

import java.util.Arrays;

public enum ChannelSurchargesTaxesOriginDTO {

    EVENT,
    CHANNEL;

    public static ChannelSurchargesTaxesOriginDTO fromMs(ChannelSurchargesTaxesOrigin in) {
        if (in == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(elem -> elem.name().equals(in.name()))
                .findFirst()
                .orElse(null);
    }
}

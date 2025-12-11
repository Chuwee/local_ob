package es.onebox.mgmt.datasources.ms.channel.enums;

import es.onebox.mgmt.channels.taxes.enums.ChannelSurchargesTaxesOriginDTO;

import java.util.Arrays;

public enum ChannelSurchargesTaxesOrigin {

    EVENT,
    CHANNEL;

    public static ChannelSurchargesTaxesOrigin fromDTO(ChannelSurchargesTaxesOriginDTO in) {
        if (in == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(elem -> elem.name().equals(in.name()))
                .findFirst()
                .orElse(null);
    }

}

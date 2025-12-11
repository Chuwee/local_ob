package es.onebox.mgmt.datasources.ms.channel.enums;

import es.onebox.mgmt.channels.enums.ChannelSurchargesCalculation;

import java.util.Arrays;

public enum ChannelSurchargesCalculationDTO {

    BEFORE_CHANNEL_PROMOTIONS,
    AFTER_CHANNEL_PROMOTIONS;

    public static ChannelSurchargesCalculationDTO fromMs(ChannelSurchargesCalculation in) {
        if (in == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(elem -> elem.name().equals(in.name()))
                .findFirst()
                .orElse(null);
    }
}

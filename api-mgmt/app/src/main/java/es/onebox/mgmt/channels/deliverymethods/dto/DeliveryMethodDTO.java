package es.onebox.mgmt.channels.deliverymethods.dto;

import es.onebox.mgmt.datasources.ms.channel.dto.deliverymethod.DeliveryMethod;

import java.util.Arrays;

public enum DeliveryMethodDTO {
    PRINT_AT_HOME,
    TAQ_PICKUP,
    PRINT_EXPRESS,
    PHONE,
    EXTERNAL_CHANNEL,
    NATIONAL_POST_DELIVERY,
    INTERNATIONAL_POST_DELIVERY,
    WHATSAPP;

    public static DeliveryMethodDTO fromMs(DeliveryMethod deliveryMethod) {
        if (deliveryMethod == null) {
            return null;
        }
        return Arrays.stream(DeliveryMethodDTO.values())
                .filter(t -> t.name().equals(deliveryMethod.name()))
                .findFirst()
                .orElse(null);
    }
}

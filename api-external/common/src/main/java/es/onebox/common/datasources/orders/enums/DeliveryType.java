package es.onebox.common.datasources.orders.enums;

import java.util.Arrays;

public enum DeliveryType {

    PRINT_AT_HOME(DeliveryMethod.PRINT_AT_HOME),
    VENUE_PICKUP(DeliveryMethod.TAQ_PICKUP),
    PRINT_DIRECT(DeliveryMethod.PRINT_EXPRESS),
    SMS(DeliveryMethod.PHONE),
    EXTERNAL_CHANNEL(DeliveryMethod.EXTERNAL_CHANNEL),
    NATIONAL_POST_DELIVERY(DeliveryMethod.NATIONAL_POST_DELIVERY),
    INTERNATIONAL_POST_DELIVERY(DeliveryMethod.INTERNATIONAL_POST_DELIVERY),
    WHATSAPP(DeliveryMethod.WHATSAPP);

    private final DeliveryMethod msOrderValue;

    DeliveryType(DeliveryMethod msOrderValue) {
        this.msOrderValue = msOrderValue;
    }

    public static DeliveryType fromMsOrder(DeliveryMethod deliveryMethod) {
        return Arrays.stream(values()).filter(it -> it.msOrderValue == deliveryMethod).findFirst().orElse(null);
    }
}

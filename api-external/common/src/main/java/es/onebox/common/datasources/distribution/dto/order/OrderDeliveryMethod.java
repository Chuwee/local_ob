package es.onebox.common.datasources.distribution.dto.order;

import java.util.Arrays;

public enum OrderDeliveryMethod {
    EMAIL(MsDeliveryMethod.PRINT_AT_HOME),
    VENUE_PICKUP(MsDeliveryMethod.TAQ_PICKUP),
    PRINT_EXPRESS(MsDeliveryMethod.PRINT_EXPRESS),
    PHONE(MsDeliveryMethod.PHONE),
    NATIONAL_POST(MsDeliveryMethod.NATIONAL_POST_DELIVERY),
    INTERNATIONAL_POST(MsDeliveryMethod.INTERNATIONAL_POST_DELIVERY),
    WHATSAPP(MsDeliveryMethod.WHATSAPP);

    private MsDeliveryMethod msValue;

    OrderDeliveryMethod(MsDeliveryMethod type) {
        this.msValue = type;
    }

    public MsDeliveryMethod getMsValue() {
        return msValue;
    }

    public static OrderDeliveryMethod fromMsDeliveryMethod(MsDeliveryMethod msDeliveryMethod) {
        if (msDeliveryMethod == null) return null;
        return Arrays.stream(OrderDeliveryMethod.values()).filter(v -> v.getMsValue().equals(msDeliveryMethod)).findFirst().orElse(null);
    }
}

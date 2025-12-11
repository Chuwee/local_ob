package es.onebox.mgmt.salerequests.taxes.enums;

import es.onebox.mgmt.datasources.ms.channel.enums.SaleRequestSurchargesTaxesOrigin;

import java.util.Arrays;

public enum SaleRequestSurchargesTaxesOriginDTO {

    EVENT,
    CHANNEL,
    SALE_REQUEST;

    public static SaleRequestSurchargesTaxesOriginDTO fromMs(SaleRequestSurchargesTaxesOrigin in) {
        if (in == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(elem -> elem.name().equals(in.name()))
                .findFirst()
                .orElse(null);
    }
}

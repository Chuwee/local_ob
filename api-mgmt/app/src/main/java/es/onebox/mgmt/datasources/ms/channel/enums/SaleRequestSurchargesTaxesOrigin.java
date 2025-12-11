package es.onebox.mgmt.datasources.ms.channel.enums;

import es.onebox.mgmt.salerequests.taxes.enums.SaleRequestSurchargesTaxesOriginDTO;

import java.util.Arrays;

public enum SaleRequestSurchargesTaxesOrigin {

    EVENT,
    CHANNEL,
    SALE_REQUEST;

    public static SaleRequestSurchargesTaxesOrigin fromDTO(SaleRequestSurchargesTaxesOriginDTO in) {
        if (in == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(elem -> elem.name().equals(in.name()))
                .findFirst()
                .orElse(null);
    }

}

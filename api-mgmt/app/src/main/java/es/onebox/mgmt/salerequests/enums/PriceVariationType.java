package es.onebox.mgmt.salerequests.enums;

import es.onebox.mgmt.datasources.ms.channel.salerequests.enums.MsPriceVariationType;

import java.io.Serializable;

public enum PriceVariationType implements Serializable{
    FIXED,
    PERCENTAGE,
    BASE_PRICE;

    private static final long serialVersionUID = 1L;

    PriceVariationType() {}

    public static MsPriceVariationType toMsChannelEnum(PriceVariationType priceVariationType) {
        if(priceVariationType == null){
            return null;
        }
        return valueOf(MsPriceVariationType.class, priceVariationType.name());
    }

    public static PriceVariationType fromMsChannelEnum(MsPriceVariationType priceVariationType) {
        if (priceVariationType == null){
            return null;
        }
        return valueOf(priceVariationType.name());
    }
}
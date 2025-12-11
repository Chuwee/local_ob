package es.onebox.event.events.enums;


import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.exception.MsEventErrorCode;

public enum PriceType {

    INDIVIDUAL,
    GROUP;

    public static PriceType fromString(String name) {
        for (PriceType value : PriceType.values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        throw new OneboxRestException(MsEventErrorCode.INVALID_PRICE_TYPE);
    }
}

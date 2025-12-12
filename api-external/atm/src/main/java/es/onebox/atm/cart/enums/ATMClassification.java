package es.onebox.atm.cart.enums;

import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.OneboxRestException;

public enum ATMClassification {
    SUBSCRIBER_MEMBER,
    MEMBER,
    FAN,
    RED_WHITE,
    REGISTERED_USER,
    VIP,
    VIP_MEMBER,
    VIP_SUBSCRIBER_MEMBER,
    SPONSOR,
    TOURIST_PACK;


    public static ATMClassification fromString(String name) {
        try {
            return ATMClassification.valueOf(name);
        } catch (Exception e) {
            throw new OneboxRestException(ApiExternalErrorCode.INVALID_CREDENTIALS);
        }
    }
}

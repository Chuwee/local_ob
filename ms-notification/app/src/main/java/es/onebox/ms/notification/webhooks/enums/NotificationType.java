package es.onebox.ms.notification.webhooks.enums;

import java.io.Serializable;

public enum NotificationType implements Serializable {

    ORDER,
    MEMBERORDER,
    PREORDER,
    ITEM,
    EVENT,
    SESSION,
    PROMOTION,
    CHANNEL,
    B2BBALANCE,
    ENTITY_FVZONE,
    USER_FVZONE,
    PRODUCT;

    NotificationType() {
    }

    public static NotificationType fromString(String aName){
        if(aName == null){
            throw new IllegalArgumentException("parameter cannot be null");
        }
        return NotificationType.valueOf(aName);
    }


}

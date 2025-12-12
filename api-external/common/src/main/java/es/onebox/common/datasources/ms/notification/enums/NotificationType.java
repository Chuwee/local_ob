package es.onebox.common.datasources.ms.notification.enums;

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
    ENTITY_FVZONE,
    USER_FVZONE,
    B2BBALANCE,
    PRODUCT;

    NotificationType() {
    }

}

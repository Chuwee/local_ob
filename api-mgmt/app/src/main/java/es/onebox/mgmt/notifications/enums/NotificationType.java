package es.onebox.mgmt.notifications.enums;

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

}

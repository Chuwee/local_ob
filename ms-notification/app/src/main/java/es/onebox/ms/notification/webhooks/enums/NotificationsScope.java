package es.onebox.ms.notification.webhooks.enums;

import java.io.Serializable;

public enum NotificationsScope implements Serializable {

    ENTITY,
    CHANNEL,
    SYS_ADMIN,
    OPERATOR;

    NotificationsScope() {
    }

}

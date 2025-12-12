package es.onebox.ms.notification.webhooks.enums;

import java.io.Serializable;

public enum NotificationAction implements Serializable {

    PURCHASE,
    BOOKING,
    REFUND,
    UPDATE,
    CANCEL,
    CATALOG,
    ABANDONED,
    PRINT,
    CREATE,
    REACTIVATE,
    DEACTIVATE,
    RELOCATE,
    TRANSFER;

    NotificationAction() {
    }

}

package es.onebox.ms.notification.webhooks.enums;

import java.io.Serializable;

public enum OrderAction implements Serializable {

    PURCHASE,
    BOOKING,
    REFUND,
    UPDATE,
    CANCEL,
    PRINT;

    OrderAction() {
    }

}

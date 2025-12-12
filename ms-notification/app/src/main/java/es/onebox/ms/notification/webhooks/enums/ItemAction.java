package es.onebox.ms.notification.webhooks.enums;

import java.io.Serializable;

public enum ItemAction implements Serializable {

    PURCHASE,
    BOOKING,
    REFUND,
    TRANSFER;

    ItemAction() {
    }

}

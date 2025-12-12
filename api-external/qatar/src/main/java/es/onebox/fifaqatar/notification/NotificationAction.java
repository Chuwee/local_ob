package es.onebox.fifaqatar.notification;

import java.util.Arrays;

public enum NotificationAction {

    TRANSFER,
    PURCHASE,
    REFUND,
    CANCEL;

    public static NotificationAction fromValue(String value) {
        return Arrays.stream(NotificationAction.values())
                .filter(action -> action.name().equals(value))
                .findFirst()
                .orElse(null);
    }
}

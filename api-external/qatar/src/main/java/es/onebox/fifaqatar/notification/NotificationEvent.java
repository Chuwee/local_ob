package es.onebox.fifaqatar.notification;

import java.util.Arrays;

public enum NotificationEvent {

    ITEM,
    ORDER;

    public static NotificationEvent fromValue(String value) {
        return Arrays.stream(NotificationEvent.values())
                .filter(event -> event.name().equals(value))
                .findFirst().
                orElse(null);
    }
}

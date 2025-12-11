package es.onebox.event.events.domain;

import es.onebox.core.utils.common.CommonUtils;

import java.util.Arrays;

public enum NotificationType {
    REQUEST_TO_CHANNEL(1),
    SALE_EVENT_STATUS_CHANGE(2),
    CHANNEL_RESPONSE(3);

    private int id;

    NotificationType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public NotificationType getById(Integer id) {
        if (CommonUtils.isNull(id)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(nt -> nt.getId() == id)
                .findAny()
                .orElse(null);
    }


}
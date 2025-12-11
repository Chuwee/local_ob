package es.onebox.event.events.enums;

import java.io.Serializable;

public enum ChannelEventStatus implements Serializable {

    REFUSED(0),
    REQUESTED(1),
    ACCEPTED(2),
    PENDING_REQUESTED(3);

    private final Integer id;

    ChannelEventStatus(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}

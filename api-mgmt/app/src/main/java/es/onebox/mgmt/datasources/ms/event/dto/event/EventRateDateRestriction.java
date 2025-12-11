package es.onebox.mgmt.datasources.ms.event.dto.event;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class EventRateDateRestriction implements Serializable {

    private static final long serialVersionUID = 1L;

    private ZonedDateTime from;

    private ZonedDateTime to;

    public ZonedDateTime getFrom() {
        return from;
    }

    public void setFrom(ZonedDateTime from) {
        this.from = from;
    }

    public ZonedDateTime getTo() {
        return to;
    }

    public void setTo(ZonedDateTime to) {
        this.to = to;
    }
}

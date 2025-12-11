package es.onebox.mgmt.datasources.ms.promotion.dto.channel;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class SessionDate implements Serializable {

    private static final long serialVersionUID = -4054319055950386759L;

    private ZonedDateTime start;
    private ZonedDateTime end;

    public ZonedDateTime getStart() {
        return start;
    }

    public void setStart(ZonedDateTime start) {
        this.start = start;
    }

    public ZonedDateTime getEnd() {
        return end;
    }

    public void setEnd(ZonedDateTime end) {
        this.end = end;
    }
}

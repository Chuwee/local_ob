package es.onebox.mgmt.channels.promotions.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class SessionDateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 151083259489081125L;

    private ZonedDateTime start;
    private ZonedDateTime end;

    public SessionDateDTO() {
    }

    public SessionDateDTO(ZonedDateTime start, ZonedDateTime end) {
        this.start = start;
        this.end = end;
    }

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

package es.onebox.event.catalog.elasticsearch.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class RateDatesRestriction implements Serializable {

    @Serial
    private static final long serialVersionUID = 253609915189981338L;

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

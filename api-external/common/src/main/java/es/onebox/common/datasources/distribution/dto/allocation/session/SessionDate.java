package es.onebox.common.datasources.distribution.dto.allocation.session;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class SessionDate implements Serializable {

    @Serial
    private static final long serialVersionUID = -1419778067838884847L;

    private ZonedDateTime start;
    private ZonedDateTime end;
    @JsonProperty("start_unconfirmed")
    private Boolean startUnconfirmed;

    public ZonedDateTime getStart() {
        return start;
    }

    public void setStart(ZonedDateTime start) {
        this.start = start;
    }

    public ZonedDateTime getEnd() { return end; }

    public void setEnd(ZonedDateTime end) { this.end = end; }

    public Boolean getStartUnconfirmed() { return startUnconfirmed; }

    public void setStartUnconfirmed(Boolean startUnconfirmed) { this.startUnconfirmed = startUnconfirmed; }
}

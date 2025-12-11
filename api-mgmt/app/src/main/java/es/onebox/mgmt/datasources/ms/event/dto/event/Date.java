package es.onebox.mgmt.datasources.ms.event.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.datasources.common.dto.TimeZone;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class Date implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime dateTime;
    @JsonProperty("timezone")
    private TimeZone timeZone;

    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(ZonedDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }
}

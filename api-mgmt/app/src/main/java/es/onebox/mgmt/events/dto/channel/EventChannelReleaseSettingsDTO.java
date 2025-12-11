package es.onebox.mgmt.events.dto.channel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import es.onebox.core.utils.dto.DateConvertible;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class EventChannelReleaseSettingsDTO implements Serializable, DateConvertible {

    private static final long serialVersionUID = 1L;

    private Boolean enabled;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime date;
    @JsonIgnore
    private String timeZone;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    @Override
    public void convertDates() {
        if (date != null && timeZone != null) {
            date = date.withZoneSameInstant(ZoneId.of(timeZone));
        }
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}

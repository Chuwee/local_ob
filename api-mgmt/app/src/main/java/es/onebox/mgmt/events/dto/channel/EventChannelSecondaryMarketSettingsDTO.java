package es.onebox.mgmt.events.dto.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.utils.dto.DateConvertible;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class EventChannelSecondaryMarketSettingsDTO implements Serializable, DateConvertible {
    @Serial private static final long serialVersionUID = 1L;

    @JsonProperty("enabled")
    private Boolean enabled;
    @JsonProperty("start_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime startDate;
    @JsonProperty("end_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime endDate;

    public Boolean getEnabled() {
        return enabled;
    }
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }
    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }
    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    @Override
    public void convertDates() {
        if (startDate != null) {
            startDate = startDate.withZoneSameInstant(startDate.getZone());
        }
        if (endDate != null) {
            endDate = endDate.withZoneSameInstant(endDate.getZone());
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

package es.onebox.mgmt.seasontickets.dto.channels;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.utils.dto.DateConvertible;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class SeasonTicketChannelSecondaryMarketSettingsDTO implements Serializable, DateConvertible {
    @Serial private static final long serialVersionUID = 1L;

    @JsonProperty("enabled")
    private Boolean enabled;
    @JsonProperty("start_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime startDate;
    @JsonIgnore
    private String startDateTimeZone;
    @JsonProperty("end_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime endDate;
    @JsonIgnore
    private String endDateTimeZone;

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

    public String getStartDateTimeZone() {
        return startDateTimeZone;
    }
    public void setStartDateTimeZone(String startDateTimeZone) {
        this.startDateTimeZone = startDateTimeZone;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }
    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    public String getEndDateTimeZone() {
        return endDateTimeZone;
    }
    public void setEndDateTimeZone(String endDateTimeZone) {
        this.endDateTimeZone = endDateTimeZone;
    }

    @Override
    public void convertDates() {
        if (startDate != null && startDateTimeZone != null) {
            startDate = startDate.withZoneSameInstant(ZoneId.of(startDateTimeZone));
        }
        if (endDate != null && endDateTimeZone != null) {
            endDate = endDate.withZoneSameInstant(ZoneId.of(endDateTimeZone));
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

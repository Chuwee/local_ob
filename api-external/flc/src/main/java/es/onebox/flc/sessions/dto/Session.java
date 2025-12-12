package es.onebox.flc.sessions.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.common.utils.TimeZoneResolver;
import es.onebox.core.utils.dto.DateConvertible;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class Session implements DateConvertible, Serializable {

    @Serial
    private static final long serialVersionUID = 6546226849322080043L;

    private Long id;
    private String name;
    @JsonProperty("start_date")
    private ZonedDateTime startDate;
    @JsonProperty("end_date")
    private ZonedDateTime endDate;
    @JsonProperty("event_id")
    private Long eventId;
    @JsonProperty("space_id")
    private Long spaceId;
    @JsonProperty("access_validation_space_id")
    private Long accessValidationSpaceId;
    @JsonProperty("venue_id")
    private Long venueId;
    private String reference;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(Long spaceId) {
        this.spaceId = spaceId;
    }

    public Long getAccessValidationSpaceId() {
        return accessValidationSpaceId;
    }

    public void setAccessValidationSpaceId(Long accessValidationSpaceId) {
        this.accessValidationSpaceId = accessValidationSpaceId;
    }

    public Long getVenueId() {
        return venueId;
    }

    public void setVenueId(Long venueId) {
        this.venueId = venueId;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    @JsonIgnore
    private String timeZone;

    @Override
    public void convertDates() {
        if (startDate != null) {
            startDate = TimeZoneResolver.applyTimeZone(startDate, timeZone);
        }

        if (endDate != null) {
            endDate = TimeZoneResolver.applyTimeZone(endDate, timeZone);
        }
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
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

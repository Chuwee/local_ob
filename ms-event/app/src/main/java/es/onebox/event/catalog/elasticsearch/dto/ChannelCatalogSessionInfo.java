package es.onebox.event.catalog.elasticsearch.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class ChannelCatalogSessionInfo extends ChannelCatalogInfo {

    @Serial
    private static final long serialVersionUID = 1L;

    private ChannelCatalogDates date;
    private String timeZone;
    private Boolean mandatoryAttendants;

    public ChannelCatalogDates getDate() {
        return date;
    }

    public void setDate(ChannelCatalogDates date) {
        this.date = date;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public Boolean getMandatoryAttendants() {
        return mandatoryAttendants;
    }

    public void setMandatoryAttendants(Boolean mandatoryAttendants) {
        this.mandatoryAttendants = mandatoryAttendants;
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

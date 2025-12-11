package es.onebox.mgmt.channels.catalog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class ChannelEventUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("event_id")
    @NotNull(message = "Event id can not be null")
    private Long eventId;

    private ChannelEventCatalogDataDTO catalog;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public ChannelEventCatalogDataDTO getCatalog() {
        return catalog;
    }

    public void setCatalog(ChannelEventCatalogDataDTO catalog) {
        this.catalog = catalog;
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

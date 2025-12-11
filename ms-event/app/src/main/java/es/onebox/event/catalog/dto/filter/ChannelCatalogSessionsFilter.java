package es.onebox.event.catalog.dto.filter;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public class ChannelCatalogSessionsFilter extends ChannelCatalogFilter {

    private static final long serialVersionUID = -7543684169532369514L;

    private List<Long> eventId;
    private List<Long> sessionId;
    private List<SessionType> type;
    private List<Long> venueConfigId;

    public List<Long> getEventId() {
        return eventId;
    }

    public void setEventId(List<Long> eventId) {
        this.eventId = eventId;
    }

    public List<Long> getSessionId() { return sessionId; }

    public void setSessionId(List<Long> sessionId) { this.sessionId = sessionId; }

    public List<SessionType> getType() {
        return type;
    }

    public void setType(List<SessionType> type) {
        this.type = type;
    }

    public List<Long> getVenueConfigId() {
        return venueConfigId;
    }

    public void setVenueConfigId(List<Long> venueConfigId) {
        this.venueConfigId = venueConfigId;
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

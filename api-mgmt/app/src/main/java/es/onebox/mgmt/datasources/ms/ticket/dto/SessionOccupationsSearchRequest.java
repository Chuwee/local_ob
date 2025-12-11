package es.onebox.mgmt.datasources.ms.ticket.dto;

import es.onebox.mgmt.datasources.ms.event.dto.event.EventType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class SessionOccupationsSearchRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private EventType eventType;

    private List<SessionWithQuotasDTO> sessions;

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public List<SessionWithQuotasDTO> getSessions() {
        return sessions;
    }

    public void setSessions(List<SessionWithQuotasDTO> sessions) {
        this.sessions = sessions;
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

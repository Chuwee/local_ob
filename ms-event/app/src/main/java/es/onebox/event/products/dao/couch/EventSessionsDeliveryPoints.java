package es.onebox.event.products.dao.couch;

import es.onebox.event.products.enums.SelectionType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class EventSessionsDeliveryPoints implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // eventId
    private Long id;
    //event
    private Set<Long> eventDeliveryPoints;
    private SelectionType sessionSelectionType;
    private Map<Long,Set<Long>> sessionsDeliveryPoints;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SelectionType getSessionSelectionType() {
        return sessionSelectionType;
    }

    public void setSessionSelectionType(SelectionType sessionSelectionType) {
        this.sessionSelectionType = sessionSelectionType;
    }

    public Map<Long, Set<Long>> getSessionsDeliveryPoints() {
        return sessionsDeliveryPoints;
    }

    public void setSessionsDeliveryPoints(Map<Long, Set<Long>> sessionsDeliveryPoints) {
        this.sessionsDeliveryPoints = sessionsDeliveryPoints;
    }

    public Set<Long> getEventDeliveryPoints() {
        return eventDeliveryPoints;
    }

    public void setEventDeliveryPoints(Set<Long> eventDeliveryPoints) {
        this.eventDeliveryPoints = eventDeliveryPoints;
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

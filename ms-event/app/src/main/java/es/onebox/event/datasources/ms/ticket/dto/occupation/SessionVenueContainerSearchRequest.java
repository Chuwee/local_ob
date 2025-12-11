package es.onebox.event.datasources.ms.ticket.dto.occupation;

import es.onebox.event.events.enums.EventType;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class SessionVenueContainerSearchRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -1641438615962135064L;

    private Long sessionId;
    private List<Long> quotas;
    private List<Long> containers;
    private List<Long> sectors;
    private List<Long> priceTypes;
    private EventType eventType;

    public List<Long> getQuotas() {
        return quotas;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public void setQuotas(List<Long> quotas) {
        this.quotas = quotas;
    }

    public List<Long> getContainers() {
        return containers;
    }

    public void setContainers(List<Long> containers) {
        this.containers = containers;
    }

    public List<Long> getSectors() {
        return sectors;
    }

    public void setSectors(List<Long> sectors) {
        this.sectors = sectors;
    }

    public List<Long> getPriceTypes() {
        return priceTypes;
    }

    public void setPriceTypes(List<Long> priceTypes) {
        this.priceTypes = priceTypes;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }
}

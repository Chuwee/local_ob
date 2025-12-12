package es.onebox.common.datasources.ms.event.dto.response.catalog.event;

import es.onebox.common.datasources.ms.event.dto.response.catalog.CommunicationElement;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class EventCatalog implements Serializable {

    @Serial
    private static final long serialVersionUID = -7868575337431701734L;

    private Long eventId;
    private String eventName;
    private Byte eventType;
    private Integer eventStatus;
    private String eventDefaultLanguage;
    private String promoterRef;
    private Integer operatorId;
    private Integer entityId;
    private String entityName;
    private List<CommunicationElement> communicationElements;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Byte getEventType() {
        return eventType;
    }

    public void setEventType(Byte eventType) {
        this.eventType = eventType;
    }

    public Integer getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(Integer eventStatus) {
        this.eventStatus = eventStatus;
    }

    public String getEventDefaultLanguage() {
        return eventDefaultLanguage;
    }

    public void setEventDefaultLanguage(String eventDefaultLanguage) {
        this.eventDefaultLanguage = eventDefaultLanguage;
    }

    public String getPromoterRef() {
        return promoterRef;
    }

    public void setPromoterRef(String promoterRef) {
        this.promoterRef = promoterRef;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public List<CommunicationElement> getCommunicationElements() {
        return communicationElements;
    }

    public void setCommunicationElements(List<CommunicationElement> communicationElements) {
        this.communicationElements = communicationElements;
    }
}

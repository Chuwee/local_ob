package es.onebox.event.seasontickets.dto;

import es.onebox.event.catalog.elasticsearch.dto.session.SessionCommunicationElement;
import es.onebox.event.events.enums.SessionPackType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class SessionResultDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 699697805296511291L;

    private Integer eventId;
    private Date beginBookingDate;
    private String sessionName;
    private Integer sessionStatus;
    private Integer entityId;
    private Integer sessionId;
    private Boolean published;
    private Integer promoterId;
    private Date publishSessionDate;
    private Date endBookingDate;
    private Integer eventStatus;
    private Date beginSessionDate;
    private Date realEndSessionDate;
    private String eventName;
    private Long venueId;
    private String venueName;
    private List<Integer> relatedSeasonSessionIds;
    private SessionPackType eventSeasonType;
    private List<SessionCommunicationElement> communicationElements;

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Date getBeginBookingDate() {
        return beginBookingDate;
    }

    public void setBeginBookingDate(Date beginBookingDate) {
        this.beginBookingDate = beginBookingDate;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public Integer getSessionStatus() {
        return sessionStatus;
    }

    public void setSessionStatus(Integer sessionStatus) {
        this.sessionStatus = sessionStatus;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public Integer getPromoterId() {
        return promoterId;
    }

    public void setPromoterId(Integer promoterId) {
        this.promoterId = promoterId;
    }

    public Date getPublishSessionDate() {
        return publishSessionDate;
    }

    public void setPublishSessionDate(Date publishSessionDate) {
        this.publishSessionDate = publishSessionDate;
    }

    public Date getEndBookingDate() {
        return endBookingDate;
    }

    public void setEndBookingDate(Date endBookingDate) {
        this.endBookingDate = endBookingDate;
    }

    public Integer getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(Integer eventStatus) {
        this.eventStatus = eventStatus;
    }

    public Date getBeginSessionDate() {
        return beginSessionDate;
    }

    public void setBeginSessionDate(Date beginSessionDate) {
        this.beginSessionDate = beginSessionDate;
    }

    public Date getRealEndSessionDate() {
        return realEndSessionDate;
    }

    public void setRealEndSessionDate(Date realEndSessionDate) {
        this.realEndSessionDate = realEndSessionDate;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Long getVenueId() {
        return venueId;
    }

    public void setVenueId(Long venueId) {
        this.venueId = venueId;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public List<Integer> getRelatedSeasonSessionIds() {
        return relatedSeasonSessionIds;
    }

    public void setRelatedSeasonSessionIds(List<Integer> relatedSeasonSessionIds) {
        this.relatedSeasonSessionIds = relatedSeasonSessionIds;
    }

    public SessionPackType getEventSeasonType() {
        return eventSeasonType;
    }

    public void setEventSeasonType(SessionPackType eventSeasonType) {
        this.eventSeasonType = eventSeasonType;
    }

    public List<SessionCommunicationElement> getCommunicationElements() {
        return communicationElements;
    }

    public void setCommunicationElements(List<SessionCommunicationElement> communicationElements) {
        this.communicationElements = communicationElements;
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

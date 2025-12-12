package es.onebox.common.datasources.ms.event.dto.response.catalog.session;

import es.onebox.common.datasources.ms.event.dto.response.catalog.CommunicationElement;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class SessionCatalog implements Serializable {

    @Serial
    private static final long serialVersionUID = -7868575337431701734L;

    private Long sessionId;
    private Long eventId;
    private String sessionName;
    private Byte sessionStatus;
    private String timeZone;
    private Long beginSessionDate;
    private Long endSessionDate;
    private Long realEndSessionDate;
    private Long publishSessionDate;
    private Long beginBookingDate;
    private Long endBookingDate;
    private Long beginAdmissionDate;
    private Long endAdmissionDate;
    private Long venueId;
    private Long venueConfigId;
    private Boolean isGraphic;
    private List<CommunicationElement> communicationElements;
    private List<Long> promotions;
    private Long entityId;
    private Boolean isSeasonPackSession;
    private SessionPackSettings sessionPackSettings;
    private List<Long> relatedSeasonSessionIds;
    private Boolean showDate;
    private Boolean showDateTime;
    private Boolean showUnconfirmedDate;
    private Boolean noFinalDate;
    private String reference;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public Byte getSessionStatus() {
        return sessionStatus;
    }

    public void setSessionStatus(Byte sessionStatus) {
        this.sessionStatus = sessionStatus;
    }

    public Long getVenueId() {
        return venueId;
    }

    public void setVenueId(Long venueId) {
        this.venueId = venueId;
    }

    public Long getVenueConfigId() {
        return venueConfigId;
    }

    public void setVenueConfigId(Long venueConfigId) {
        this.venueConfigId = venueConfigId;
    }

    public Boolean getGraphic() {
        return isGraphic;
    }

    public void setGraphic(Boolean graphic) {
        isGraphic = graphic;
    }

    public List<CommunicationElement> getCommunicationElements() {
        return communicationElements;
    }

    public void setCommunicationElements(List<CommunicationElement> communicationElements) {
        this.communicationElements = communicationElements;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public Long getBeginSessionDate() {
        return beginSessionDate;
    }

    public void setBeginSessionDate(Long beginSessionDate) {
        this.beginSessionDate = beginSessionDate;
    }

    public Long getEndSessionDate() {
        return endSessionDate;
    }

    public void setEndSessionDate(Long endSessionDate) {
        this.endSessionDate = endSessionDate;
    }

    public Long getRealEndSessionDate() {
        return realEndSessionDate;
    }

    public void setRealEndSessionDate(Long realEndSessionDate) {
        this.realEndSessionDate = realEndSessionDate;
    }

    public Long getPublishSessionDate() {
        return publishSessionDate;
    }

    public void setPublishSessionDate(Long publishSessionDate) {
        this.publishSessionDate = publishSessionDate;
    }

    public Long getBeginBookingDate() {
        return beginBookingDate;
    }

    public void setBeginBookingDate(Long beginBookingDate) {
        this.beginBookingDate = beginBookingDate;
    }

    public Long getEndBookingDate() {
        return endBookingDate;
    }

    public void setEndBookingDate(Long endBookingDate) {
        this.endBookingDate = endBookingDate;
    }

    public Long getBeginAdmissionDate() {
        return beginAdmissionDate;
    }

    public void setBeginAdmissionDate(Long beginAdmissionDate) {
        this.beginAdmissionDate = beginAdmissionDate;
    }

    public Long getEndAdmissionDate() {
        return endAdmissionDate;
    }

    public void setEndAdmissionDate(Long endAdmissionDate) {
        this.endAdmissionDate = endAdmissionDate;
    }

    public List<Long> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<Long> promotions) {
        this.promotions = promotions;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Boolean getSeasonPackSession() {
        return isSeasonPackSession;
    }

    public void setSeasonPackSession(Boolean seasonPackSession) {
        isSeasonPackSession = seasonPackSession;
    }

    public List<Long> getRelatedSeasonSessionIds() {
        return relatedSeasonSessionIds;
    }

    public void setRelatedSeasonSessionIds(List<Long> relatedSeasonSessionIds) {
        this.relatedSeasonSessionIds = relatedSeasonSessionIds;
    }

    public Boolean getShowDate() {
        return showDate;
    }

    public void setShowDate(Boolean showDate) {
        this.showDate = showDate;
    }

    public Boolean getShowDateTime() {
        return showDateTime;
    }

    public void setShowDateTime(Boolean showDateTime) {
        this.showDateTime = showDateTime;
    }

    public Boolean getShowUnconfirmedDate() {
        return showUnconfirmedDate;
    }

    public void setShowUnconfirmedDate(Boolean showUnconfirmedDate) {
        this.showUnconfirmedDate = showUnconfirmedDate;
    }

    public Boolean getNoFinalDate() {
        return noFinalDate;
    }

    public void setNoFinalDate(Boolean noFinalDate) {
        this.noFinalDate = noFinalDate;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public SessionPackSettings getSessionPackSettings() {
        return sessionPackSettings;
    }

    public void setSessionPackSettings(SessionPackSettings sessionPackSettings) {
        this.sessionPackSettings = sessionPackSettings;
    }
}

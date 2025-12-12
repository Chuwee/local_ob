package es.onebox.common.tickets.dto;

import es.onebox.common.datasources.common.enums.SessionType;
import es.onebox.common.datasources.ms.event.dto.RateDTO;
import es.onebox.common.datasources.ms.event.dto.TicketCommunicationElementDTO;
import es.onebox.common.datasources.ms.entity.dto.Producer;
import es.onebox.common.datasources.ms.venue.dto.VenueDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SessionData implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String sessionName;
    private RateDTO rate;
    private VenueDTO venue;
    private List<TicketCommunicationElementDTO> eventCommElement;
    private List<TicketCommunicationElementDTO> sessionCommElement;
    private Map<String, String> ticketTemplateLiteralsByCode;
    private Map<String, String> ticketCommElementsByTagType;
    private Producer promoter;
    private SeasonDate seasonsDate;

    private SessionDate sessionDate;
    private SessionType sessionType;
    private Boolean showDate;
    private Boolean showDatetime;


    public VenueDTO getVenue() {
        return venue;
    }

    public void setVenue(VenueDTO venue) {
        this.venue = venue;
    }

    public RateDTO getRate() {
        return rate;
    }

    public void setRate(RateDTO rate) {
        this.rate = rate;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public List<TicketCommunicationElementDTO> getEventCommElement() {
        return eventCommElement;
    }

    public void setEventCommElement(List<TicketCommunicationElementDTO> eventCommElement) {
        this.eventCommElement = eventCommElement;
    }

    public List<TicketCommunicationElementDTO> getSessionCommElement() {
        return sessionCommElement;
    }

    public void setSessionCommElement(List<TicketCommunicationElementDTO> sessionCommElement) {
        this.sessionCommElement = sessionCommElement;
    }

    public Producer getPromoter() {
        return promoter;
    }

    public void setPromoter(Producer promoter) {
        this.promoter = promoter;
    }

    public SeasonDate getSeasonsDate() {
        return seasonsDate;
    }

    public void setSeasonsDate(SeasonDate seasonsDate) {
        this.seasonsDate = seasonsDate;
    }

    public Map<String, String> getTicketTemplateLiteralsByCode() {
        return ticketTemplateLiteralsByCode;
    }

    public void setTicketTemplateLiteralsByCode(Map<String, String> ticketTemplateLiteralsByCode) {
        this.ticketTemplateLiteralsByCode = ticketTemplateLiteralsByCode;
    }

    public Map<String, String> getTicketCommElementsByTagType() {
        return ticketCommElementsByTagType;
    }

    public void setTicketCommElementsByTagType(Map<String, String> ticketCommElementsByTagType) {
        this.ticketCommElementsByTagType = ticketCommElementsByTagType;
    }

    public SessionDate getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(SessionDate sessionDate) {
        this.sessionDate = sessionDate;
    }

    public SessionType getSessionType() {
        return sessionType;
    }

    public void setSessionType(SessionType sessionType) {
        this.sessionType = sessionType;
    }

    public Boolean getShowDate() {
        return showDate;
    }

    public void setShowDate(Boolean showDate) {
        this.showDate = showDate;
    }

    public Boolean getShowDatetime() {
        return showDatetime;
    }

    public void setShowDatetime(Boolean showDatetime) {
        this.showDatetime = showDatetime;
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

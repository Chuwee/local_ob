package es.onebox.common.datasources.orders.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class OrderProductAdditionalData implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private ZonedDateTime purchaseDate;
    private String eventName;
    private String eventEntityName;
    private String sessionName;
    private ZonedDateTime sessionDate;
    private String venueName;
    private String venueOlsonId;
    private Boolean includeAllSeasonSessionTickets;

    public OrderProductAdditionalData() {
        super();
    }

    public ZonedDateTime getPurchaseDate() {
        return this.purchaseDate;
    }

    public void setPurchaseDate(ZonedDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getEventName() {
        return this.eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventEntityName() {
        return this.eventEntityName;
    }

    public void setEventEntityName(String eventEntityName) {
        this.eventEntityName = eventEntityName;
    }

    public String getSessionName() {
        return this.sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public ZonedDateTime getSessionDate() {
        return this.sessionDate;
    }

    public void setSessionDate(ZonedDateTime sessionDate) {
        this.sessionDate = sessionDate;
    }

    public String getVenueName() {
        return this.venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getVenueOlsonId() {
        return this.venueOlsonId;
    }

    public void setVenueOlsonId(String venueOlsonId) {
        this.venueOlsonId = venueOlsonId;
    }

    public Boolean getIncludeAllSeasonSessionTickets() {
        return this.includeAllSeasonSessionTickets;
    }

    public void setIncludeAllSeasonSessionTickets(Boolean includeAllSeasonSessionTickets) {
        this.includeAllSeasonSessionTickets = includeAllSeasonSessionTickets;
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

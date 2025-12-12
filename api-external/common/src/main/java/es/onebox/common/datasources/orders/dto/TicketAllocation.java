package es.onebox.common.datasources.orders.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.common.datasources.common.dto.Event;
import es.onebox.common.datasources.common.dto.Session;
import es.onebox.common.datasources.common.dto.Venue;
import es.onebox.common.datasources.orderitems.dto.Access;
import es.onebox.common.datasources.orderitems.enums.TicketAllocationType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class TicketAllocation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    
    private TicketAllocationType type;
    private Event event;
    private Session session;
    private Venue venue;
    private Seat seat;
    private Row row;
    @JsonProperty("price_type")
    private PriceType priceType;
    private Sector sector;
    @JsonProperty("not_numbered_area")
    private NotNumberedArea notNumberedArea;
    private Access access;
    private String accessibility;

    public TicketAllocationType getType() {
        return type;
    }

    public void setType(TicketAllocationType type) {
        this.type = type;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    public Row getRow() {
        return row;
    }

    public void setRow(Row row) {
        this.row = row;
    }

    public PriceType getPriceType() {
        return priceType;
    }

    public void setPriceType(PriceType priceType) {
        this.priceType = priceType;
    }

    public Sector getSector() {
        return sector;
    }

    public void setSector(Sector sector) {
        this.sector = sector;
    }

    public NotNumberedArea getNotNumberedArea() {
        return notNumberedArea;
    }

    public void setNotNumberedArea(NotNumberedArea notNumberedArea) {
        this.notNumberedArea = notNumberedArea;
    }

    public Access getAccess() {
        return access;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    public String getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(String accessibility) {
        this.accessibility = accessibility;
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

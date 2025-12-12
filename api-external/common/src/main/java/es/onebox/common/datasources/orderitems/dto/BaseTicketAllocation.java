package es.onebox.common.datasources.orderitems.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.common.datasources.common.dto.Event;
import es.onebox.common.datasources.common.dto.Session;
import es.onebox.common.datasources.orderitems.enums.Accessibility;
import es.onebox.common.datasources.orderitems.enums.TicketAllocationType;
import es.onebox.common.datasources.orderitems.enums.Visibility;
import es.onebox.common.datasources.orders.dto.NotNumberedArea;
import es.onebox.common.datasources.orders.dto.PriceType;
import es.onebox.common.datasources.orders.dto.Row;
import es.onebox.common.datasources.orders.dto.Seat;
import es.onebox.common.datasources.orders.dto.Sector;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class BaseTicketAllocation implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private TicketAllocationType type;
    private Event event;
    private Session session;
    private Seat seat;
    private Row row;
    @JsonProperty("price_type")
    private PriceType priceType;
    private Sector sector;
    @JsonProperty("not_numbered_area")
    private NotNumberedArea notNumberedArea;
    private Access access;
    private Accessibility accessibility;
    private Visibility visibility;

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

    public Accessibility getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(Accessibility accessibility) {
        this.accessibility = accessibility;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
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

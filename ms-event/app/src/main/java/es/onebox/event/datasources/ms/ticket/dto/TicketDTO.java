package es.onebox.event.datasources.ms.ticket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import es.onebox.event.datasources.ms.ticket.enums.TicketStatus;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class TicketDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    private Long id;
    @JsonProperty("status")
    private TicketStatus status;
    @JsonProperty("price_type_id")
    private Long priceTypeId;
    @JsonProperty("session_id")
    private Long sessionId;
    @JsonProperty("sector_id")
    private Long sectorId;
    @JsonProperty("not_numbered_area_id")
    private Long notNumberedAreaId;
    @JsonProperty("row")
    private Long row;
    @JsonProperty("seat")
    private String seat;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getSectorId() {
        return sectorId;
    }

    public void setSectorId(Long sectorId) {
        this.sectorId = sectorId;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public Long getRow() {
        return row;
    }

    public void setRow(Long row) {
        this.row = row;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public Long getPriceTypeId() {
        return priceTypeId;
    }

    public void setPriceTypeId(Long priceTypeId) {
        this.priceTypeId = priceTypeId;
    }

    public Long getNotNumberedAreaId() {
        return notNumberedAreaId;
    }

    public void setNotNumberedAreaId(Long notNumberedAreaId) {
        this.notNumberedAreaId = notNumberedAreaId;
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

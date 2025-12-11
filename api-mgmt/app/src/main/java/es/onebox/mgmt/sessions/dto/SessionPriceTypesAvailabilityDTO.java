package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.events.enums.TicketType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class SessionPriceTypesAvailabilityDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3794022380727363525L;

    @JsonProperty("price_type")
    private IdNameDTO priceType;

    @JsonProperty("quota")
    private IdNameDTO quota;

    @JsonProperty("ticket_type")
    private TicketType ticketType;

    private SessionAvailabilityDetailDTO availability;

    public IdNameDTO getPriceType() {
        return priceType;
    }

    public void setPriceType(IdNameDTO priceType) {
        this.priceType = priceType;
    }

    public IdNameDTO getQuota() {
        return quota;
    }

    public void setQuota(IdNameDTO quota) {
        this.quota = quota;
    }

    public TicketType getTicketType() {
        return ticketType;
    }

    public void setTicketType(TicketType ticketType) {
        this.ticketType = ticketType;
    }

    public SessionAvailabilityDetailDTO getAvailability() {
        return availability;
    }

    public void setAvailability(SessionAvailabilityDetailDTO availability) {
        this.availability = availability;
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

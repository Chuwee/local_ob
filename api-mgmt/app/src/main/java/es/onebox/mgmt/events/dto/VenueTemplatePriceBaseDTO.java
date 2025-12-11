package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.events.enums.TicketType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class VenueTemplatePriceBaseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -6223691715867161158L;

    @JsonProperty("ticket_type")
    private TicketType ticketType;
    @JsonProperty("price_type")
    private PriceTypeDTO priceType;
    private Double value;

    public TicketType getTicketType() {
        return ticketType;
    }

    public void setTicketType(TicketType ticketType) {
        this.ticketType = ticketType;
    }

    public PriceTypeDTO getPriceType() {
        return priceType;
    }

    public void setPriceType(PriceTypeDTO priceType) {
        this.priceType = priceType;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
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

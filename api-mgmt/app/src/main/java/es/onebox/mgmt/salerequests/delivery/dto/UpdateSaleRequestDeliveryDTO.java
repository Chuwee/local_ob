package es.onebox.mgmt.salerequests.delivery.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class UpdateSaleRequestDeliveryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("ticket_handling")
    private TicketHandlingType ticketHandling;

    public TicketHandlingType getTicketHandling() {
        return ticketHandling;
    }

    public void setTicketHandling(TicketHandlingType ticketHandling) {
        this.ticketHandling = ticketHandling;
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

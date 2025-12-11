package es.onebox.mgmt.channels.dto;

import jakarta.validation.Valid;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class ChannelLimitsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Valid
    private ChannelLimitsTicketsDTO tickets;

    public ChannelLimitsTicketsDTO getTickets() {
        return tickets;
    }

    public void setTickets(ChannelLimitsTicketsDTO tickets) {
        this.tickets = tickets;
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

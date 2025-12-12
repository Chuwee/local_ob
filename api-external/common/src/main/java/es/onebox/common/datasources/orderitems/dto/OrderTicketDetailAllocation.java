package es.onebox.common.datasources.orderitems.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class OrderTicketDetailAllocation extends BaseTicketAllocation{

    @Serial
    private static final long serialVersionUID = 1L;

    private VenueDetail venue;

    public VenueDetail getVenue() {
        return venue;
    }

    public void setVenue(VenueDetail venue) {
        this.venue = venue;
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

package es.onebox.mgmt.pricetype.dto;

import es.onebox.mgmt.common.CommunicationElementTextDTO;
import es.onebox.mgmt.common.ticketcontents.TicketContentTextType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class PriceTypeTicketContentTextDTO extends CommunicationElementTextDTO<TicketContentTextType> {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}

package es.onebox.mgmt.pricetype.dto;

import es.onebox.mgmt.common.CommunicationElementImageDTO;
import es.onebox.mgmt.common.ticketcontents.TicketContentImagePassbookType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class PriceTypeTicketContentImagePASSBOOKDTO extends CommunicationElementImageDTO<TicketContentImagePassbookType> {

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

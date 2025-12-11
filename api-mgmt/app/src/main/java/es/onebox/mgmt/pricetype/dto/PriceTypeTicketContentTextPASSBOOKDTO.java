package es.onebox.mgmt.pricetype.dto;

import es.onebox.mgmt.common.CommunicationElementTextDTO;
import es.onebox.mgmt.common.ticketcontents.PriceTypeTicketContentTextPASSBOOKType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class PriceTypeTicketContentTextPASSBOOKDTO extends CommunicationElementTextDTO<PriceTypeTicketContentTextPASSBOOKType> {

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

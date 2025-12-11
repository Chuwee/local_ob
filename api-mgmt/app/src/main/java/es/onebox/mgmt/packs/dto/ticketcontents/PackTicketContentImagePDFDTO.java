package es.onebox.mgmt.packs.dto.ticketcontents;

import es.onebox.mgmt.common.CommunicationElementImageDTO;
import es.onebox.mgmt.common.ticketcontents.TicketContentImagePDFType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class PackTicketContentImagePDFDTO extends CommunicationElementImageDTO<TicketContentImagePDFType> {

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

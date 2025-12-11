package es.onebox.mgmt.packs.dto.ticketcontents;

import es.onebox.mgmt.common.CommunicationElementFilter;
import es.onebox.mgmt.common.ticketcontents.TicketContentImagePDFType;
import es.onebox.mgmt.common.ticketcontents.TicketContentImagePrinterType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class PackTicketContentImagePrinterFilter extends CommunicationElementFilter<TicketContentImagePrinterType> {

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

package es.onebox.mgmt.datasources.ms.event.dto.event;

import es.onebox.mgmt.datasources.common.dto.BaseCommunicationElement;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class TicketCommunicationElement extends BaseCommunicationElement {

    @Serial
    private static final long serialVersionUID = 6983230504146870315L;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}

package es.onebox.common.datasources.ms.ticket.dto;

import es.onebox.core.serializer.dto.response.ListWithMetadata;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class SessionTicketSearchResponse extends ListWithMetadata<TicketDTO> {

    @Serial
    private static final long serialVersionUID = -3414898080822273986L;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}

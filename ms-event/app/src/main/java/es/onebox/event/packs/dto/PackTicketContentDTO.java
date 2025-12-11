package es.onebox.event.packs.dto;

import es.onebox.event.communicationelements.dto.CommunicationElementDTO;
import es.onebox.event.packs.enums.PackTicketContentTagType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class PackTicketContentDTO extends CommunicationElementDTO<PackTicketContentTagType> {

    @Serial
    private static final long serialVersionUID = 3355528657670039455L;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}

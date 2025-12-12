package es.onebox.common.datasources.accesscontrol.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class TicketSessionDTO extends IdNameDTO {

    private static final long serialVersionUID = -3313584138422148340L;

    private TicketSessionDateDTO date;

    public TicketSessionDateDTO getDate() {
        return date;
    }

    public void setDate(TicketSessionDateDTO date) {
        this.date = date;
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

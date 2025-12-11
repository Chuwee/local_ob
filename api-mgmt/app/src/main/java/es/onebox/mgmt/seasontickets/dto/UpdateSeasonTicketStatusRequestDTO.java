package es.onebox.mgmt.seasontickets.dto;

import es.onebox.mgmt.seasontickets.enums.SeasonTicketStatusDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class UpdateSeasonTicketStatusRequestDTO implements Serializable {

    private static final long serialVersionUID = 2726875588205938727L;

    private SeasonTicketStatusDTO status;

    public SeasonTicketStatusDTO getStatus() {
        return status;
    }

    public void setStatus(SeasonTicketStatusDTO status) {
        this.status = status;
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

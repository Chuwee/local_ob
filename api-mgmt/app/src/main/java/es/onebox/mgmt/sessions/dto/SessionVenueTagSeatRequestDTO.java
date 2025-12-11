package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.datasources.ms.venue.dto.template.GateUpdateType;
import es.onebox.mgmt.venues.dto.VenueTagSeatRequestDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class SessionVenueTagSeatRequestDTO extends VenueTagSeatRequestDTO {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("gate_update_type")
    private GateUpdateType gateUpdateType;

    public GateUpdateType getGateUpdateType() {
        return gateUpdateType;
    }

    public void setGateUpdateType(GateUpdateType gateUpdateType) {
        this.gateUpdateType = gateUpdateType;
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

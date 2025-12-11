package es.onebox.mgmt.loyaltypoints.seasontickets.dto;

import jakarta.validation.Valid;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class SeasonTicketLoyaltyPointsConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Valid
    private List<SessionLoyaltyPointsDTO> sessions;

    public List<SessionLoyaltyPointsDTO> getSessions() { return sessions; }

    public void setSessions(List<SessionLoyaltyPointsDTO> sessions) { this.sessions = sessions; }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
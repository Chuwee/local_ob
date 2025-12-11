package es.onebox.event.loyaltypoints.seasontickets.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class SeasonTicketLoyaltyPointsConfigDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -5083810816453509198L;

    private List<SessionLoyaltyPointsConfigDTO> sessions;

    public List<SessionLoyaltyPointsConfigDTO> getSessions() { return sessions; }

    public void setSessions(List<SessionLoyaltyPointsConfigDTO> sessions) { this.sessions = sessions; }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}